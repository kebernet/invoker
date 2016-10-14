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
package net.kebernet.invoker.runtime;

import net.kebernet.invoker.runtime.impl.IntrospectionData;
import net.kebernet.invoker.runtime.impl.InvokableMethod;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This is the top level class to use to invoke method dynamically on a class.
 */
public class Invoker {
    /**
     * A map for keeping caches of the IntrospectionData for a given type around
     * to improve performance.
     */
    private final HashMap<Class, IntrospectionData> introspectionData = new HashMap<>();

    /**
     * Registers a type ahead of time and performs the necessary reflection on the class.
     * This is not necessary, but can help with performance.
     * @param type The type to register for invocation.
     */
    public void registerType(@Nonnull Class type){
        registerAndReturn(type);
    }

    /**
     * Invokes the named method on the target object from list of ParameterValues.
     *
     * @param target The object to invoke on
     * @param methodName The method name to invoke
     * @param values values for the named parameters
     * @param <T> The return type of the method
     * @return The results of the method, or Void.class if the method has not return value.
     * @throws InvokerException is thrown generally if there is a reflection problem or the target method could not be resolved.
     */
    public <T> T invoke(Object target, String methodName, List<ParameterValue> values) throws InvokerException {
        IntrospectionData data = Optional.ofNullable(introspectionData.get(target.getClass()))
                .orElseGet(()-> registerAndReturn(target.getClass()));
        Optional<InvokableMethod> method = data.bestMatch(methodName, values);
        HashMap<String, ParameterValue> valuesMap = new HashMap<>();
        values.forEach(v -> valuesMap.put(v.getName(), v));
        return method.orElseThrow(
                ()-> new InvokerException("Could not resolve method name:" +methodName, null, target, methodName, values)
            ).invoke(target, valuesMap);
    }

    /**
     * Invokes the named method on the target object from a map of values.
     * @param target The object to invoke on
     * @param methodName The method name to invoke
     * @param values values for the named parameters
     * @param <T> The return type of the method
     * @return The results of the method or Void.class if the method has no return value.
     * @throws InvokerException is thrown generally if there is a reflection problem or the target method could not be resolved.
     */
    public <T> T invoke(Object target, String methodName, Map<String, Object> values) throws InvokerException {
        return invoke(target, methodName, values.entrySet().stream().map(e-> new ParameterValue(e.getKey(), e.getValue())).collect(Collectors.toList()));
    }

    private IntrospectionData registerAndReturn(@Nonnull Class type){
        IntrospectionData data = new IntrospectionData(type);
        this.introspectionData.put(type, data);
        return data;
    }
}
