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

import net.kebernet.invoker.annotation.DefaultInvokable;
import net.kebernet.invoker.annotation.Invokable;
import net.kebernet.invoker.runtime.ParameterValue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 *  Data for a particular type derived from reflection.
 */
public class IntrospectionData {

    private final Class clazz;
    private final String name;
    private final LinkedHashSet<InvokableMethod> methods = new LinkedHashSet<>();

    /**
     * Constructor
     * @param clazz the class we are creating introspection data for.
     */
    public IntrospectionData(Class clazz) {
        this.clazz = clazz;
        this.name = clazz.getCanonicalName();
        DefaultInvokable defaultInvokable = (DefaultInvokable) clazz.getAnnotation(DefaultInvokable.class);
        final boolean isDefaultInvokable = defaultInvokable != null && defaultInvokable.value();
        Arrays.stream(clazz.getMethods())
                .filter( m -> (m.getModifiers() & Modifier.PUBLIC) > 0)
                .filter( m -> isInvokable(isDefaultInvokable, m))
                .map(InvokableMethod::new)
                .forEach(methods::add);
    }

    /**
     * The type this is for.
     * @return The type this is for
     */
    public Class getType() {
        return clazz;
    }

    /**
     * The name of the class.
     * @return The name fo the class
     */
    public String getName() {
        return name;
    }

    /** A set of InvokableMethods contained in this class.
     *
     * @return Read-only set of methods.
     */
    public Set<InvokableMethod> getMethods() {
        return Collections.unmodifiableSet(methods);
    }

    private boolean isInvokable(boolean isDefaultInvokable, Method m) {
        Invokable invokable = m.getAnnotation(Invokable.class);
        return invokable != null ? invokable.value() : isDefaultInvokable;
    }

    /** Finds the best match for the given method name and parameter values.
     *
     * @param name The invocation name of the method.
     * @param parameters The parameter values you want to pass in.
     * @return An optional of an InvokableMethod.
     */
    public Optional<InvokableMethod> bestMatch(String name, List<ParameterValue> parameters){
        return this.methods.stream()
                .sorted(new InvokableMethod.Comparator(name, parameters))
                .findFirst()
                .filter(im -> im.matchValue(name, parameters) == 0);
    }
}
