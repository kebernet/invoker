/*
 *    Copyright (c) 2016 Robert Cooper
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.kebernet.invoker.runtime.impl;

import net.kebernet.invoker.annotation.Invokable;
import net.kebernet.invoker.runtime.InvokerException;
import net.kebernet.invoker.runtime.ParameterValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *  This is a wrapper class for j.l.r.Method that pulls metadata off the method annotations
 *  and contains the code for evaluating whether or not the method is a match for a set
 *  of ParameterValues.
 */
public class InvokableMethod {
    private final Method method;
    private final String name;
    private final LinkedHashSet<NamedParameter> parameters = new LinkedHashSet<>();
    private final int requiredParameterCount;
    /**
     * A map of Memoizations to Integer match scores to increase performance of matchValues()
     */
    private final HashMap<Memoization, Integer> memoizations = new HashMap<>();

    public InvokableMethod(@Nonnull Method method, @Nonnull Optional<Function<Method, String>> findInvocationName, @Nonnull Optional<Function<Parameter, String>> findParameterName) {
        this.method = method;
        this.name = findInvocationName.orElse((m)->{
            Invokable invokable = m.getAnnotation(Invokable.class);
            if(invokable != null && !"".equals(invokable.invocationName())){
                return invokable.invocationName();
            } else {
                return m.getName();
            }
        }).apply(method);
        Arrays.stream(method.getParameters())
                .map(p -> new NamedParameter(p, findParameterName))
                .forEach(this.parameters::add);
        this.requiredParameterCount = (int) parameters.stream().filter(NamedParameter::isRequired).count();
    }

    /**
     * The invocation name of the method. May NOT match the actual name of the enclosed method.
     * @return Invocation name
     */
    public @Nonnull String getName() {
        return name;
    }

    /**
     * The number of required parameters for the method.
     * @return Count of required parameters
     */
    public int getRequiredParameterCount(){
        return requiredParameterCount;
    }

    /**
     * Returns the j.l.r.Method this encapsulates.
     * @return The native method.
     */
    public Method getNativeMethod(){
        return this.method;
    }

    public Set<NamedParameter> getParameters(){
        return Collections.unmodifiableSet(parameters);
    }

    /**
     * This method returns a match distance for the given name and parameter values. The
     * return result will be <0 for no match, 0 for exact match, and a positive integer
     * representing the distance from being an exact match.
     *
     * @param name The name of the method to match.
     * @param values The list of parameter names and values to match.
     * @return An integer value of <0 for no match, or a distance from exact match.
     */
    public int matchValue(@Nonnull String name, @Nonnull List<ParameterValue> values) {
        if (!name.equals(this.name)) {
            return -1;
        }
        return lookupMemoized(values).orElseGet(()-> matchValues(values));
    }

    /**
     * Invokes the method on the target.
     * @param target The target object to invoke the method on.
     * @param values the values to pass to the method.
     * @param <T> the return type of the method.
     * @return The return value of the method or Void.class
     * @throws InvokerException thrown if something goes wrong.
     */
    public <T> T invoke(Object target, HashMap<String, ParameterValue> values) throws InvokerException {
        Object[] arguments = new Object[this.parameters.size()];
        int index = 0;
        for(NamedParameter parameter : parameters){
            arguments[index] = Optional.ofNullable(values.get(parameter.getName()))
                    .map(ParameterValue::getValue)
                    .orElse(null);
            index++;
        }
        try {
            Object result = this.method.invoke(target, arguments);
            if(this.method.getReturnType().equals(Void.class)){
                return (T) Void.class;
            } else {
                return (T) result;
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new InvokerException("Failed to invoke "+this.method.getName()+" with args"+ Arrays.toString(arguments), e, target, name, new ArrayList<>(values.values()));
        }
    }

    private int matchValues(@Nonnull List<ParameterValue> values){
        HashMap<String, ParameterValue> valByName = new HashMap<>(values.size());
        values.forEach(pv -> valByName.put(pv.getName(), pv));
        Set<NamedParameter> matches = parameters
                .stream()
                .filter(np -> {
                    ParameterValue pv = valByName.get(np.getName());
                    return pv != null &&
                            (pv.getValue() == null ||
                                    // TODO check here for coersions?
                                    np.getType().isAssignableFrom(pv.getValue().getClass()));
                })
                .collect(Collectors.toSet());



        // Find missing params
        Set<NamedParameter> missing = new HashSet<>(this.parameters);
        missing.removeAll(matches);
        // If a missing param is required, skip it.
        if(missing.stream().filter(NamedParameter::isRequired).findFirst().isPresent()){
            return memoize(values, -1);
        }
        Set<String> extra = new HashSet<>(valByName.keySet());
        extra.removeAll( parameters.stream().map(NamedParameter::getName).collect(Collectors.toSet()));
        if(!extra.isEmpty()){
            return memoize(values, -1);
        }
        missing.clear();
        boolean requiredNotPresent = this.parameters.stream()
                .filter(p-> {
                    ParameterValue value = valByName.get(p.getName());
                    return value != null &&
                            p.isRequired() &&
                            value.getValue() == null;
                })
                .findFirst().isPresent();
        if(requiredNotPresent){
            return memoize(values, -1);
        }
        else return memoize(values, missing.size());
    }

    /**
     * Caches a computed score for a set of meta data around parameter values.
     * @param values the values to check for.
     * @param score The score to return
     * @return the score unmodified.
     */
    private int memoize(List<ParameterValue> values, int score) {
        this.memoizations.put(new Memoization(values), score);
        return score;
    }

    private Optional<Integer> lookupMemoized(List<ParameterValue> values){
        return Optional.ofNullable(memoizations.get(new Memoization(values)));
    }

    @Override
    public String toString() {
        return new StringBuilder("InvokableMethod{")
            .append("\n\tmethod=").append(method)
            .append(",\n\t name='").append(name).append('\'')
            .append(",\n\t parameters=").append(parameters)
            .append(",\n\t requiredParameterCount=").append(requiredParameterCount)
            .append("\n}\n")
            .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InvokableMethod)) return false;

        InvokableMethod that = (InvokableMethod) o;

        return requiredParameterCount == that.requiredParameterCount &&
                method.equals(that.method) &&
                name.equals(that.name) &&
                parameters.equals(that.parameters);

    }

    @Override
    public int hashCode() {
        int result = method.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + parameters.hashCode();
        result = 31 * result + requiredParameterCount;
        return result;
    }

    /**
     * A Comapator implementation that returns the best-fit match score.
     */
     static class Comparator implements java.util.Comparator<InvokableMethod>, Serializable{

        private final String name;
        private final List<ParameterValue> values;

        Comparator(String name, List<ParameterValue> values) {
            this.name = name;
            this.values = values;
        }

        @Override
        public int compare(InvokableMethod o1, InvokableMethod o2) {
            int o1Match = o1.matchValue(name, values);
            int o2Match = o2.matchValue(name, values);
            if(o1Match == 0 && o2Match == 0){
                // If both methods are exact matches with the parameter list, return the one with the
                // strictest set of parameters.
                return Integer.compare(o2.getRequiredParameterCount(), o1.getRequiredParameterCount());
            } else {
                return Integer.compare(o2Match, o1Match);
            }
        }
    }

    /**
     * A data structure for recording the parameter match significance for an invocation.
     */
    private static class Memoization {
        private final MemoizedParameter[] parameters;

        private Memoization(List<ParameterValue> values) {
            this.parameters = values.stream()
                    .map(pv -> new MemoizedParameter(pv.getName(),
                            pv.getValue() == null ? null : pv.getValue().getClass()))
                    .collect(Collectors.toList())
                    .toArray(new MemoizedParameter[values.size()]);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Memoization)) return false;

            Memoization that = (Memoization) o;
            return Arrays.deepEquals(parameters, that.parameters);

        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(parameters);
        }
    }

    /**
     * Metadata for a param for memoization
     */
    private static class MemoizedParameter {
        private final String name;
        private final Class type;

        /**
         *
         * @param name Name of the param
         * @param type Type of the param, or null if the value is null.
         */
        private MemoizedParameter(@Nonnull String name, @Nullable Class type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MemoizedParameter)) return false;

            MemoizedParameter that = (MemoizedParameter) o;

            return name.equals(that.name) &&
                    (type != null ? type.equals(that.type) : that.type == null);

        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + (type != null ? type.hashCode() : 0);
            return result;
        }
    }

}
