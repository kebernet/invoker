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

import net.kebernet.invoker.annotation.Parameter;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by rcooper on 10/13/16.
 */
public class NamedParameter {
    private final java.lang.reflect.Parameter parameter;
    private final Class type;
    private final String name;
    private final boolean required;

    public NamedParameter(@Nonnull java.lang.reflect.Parameter parameter, Optional<Function<java.lang.reflect.Parameter, String>> findParameterName) {
        this.parameter = parameter;
        this.type = noPrimitives(parameter.getType());
        Parameter nameAnnotation = parameter.getAnnotation(Parameter.class);
        this.name = findParameterName.orElse((p)->{
            if(nameAnnotation == null){
                return parameter.getName();
            } else {
                return nameAnnotation.value();
            }
        }).apply(parameter);
        if(this.name == null){
            throw new RuntimeException("Could not get name for parameter "+parameter.toString());
        }
        this.required = parameter.getType().isPrimitive() || (nameAnnotation != null && nameAnnotation.required());
    }

    public java.lang.reflect.Parameter getParameter() {
        return parameter;
    }

    public Class getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    @Override
    public String toString() {
        return new StringBuilder("NamedParameter{")
            .append("parameter=").append(parameter)
            .append(", type=").append(type)
            .append(", name='").append(name).append('\'')
            .append(", required=").append(required)
            .append('}')
            .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamedParameter)) return false;

        NamedParameter parameter1 = (NamedParameter) o;

        if (required != parameter1.required) return false;
        if (parameter != null ? !parameter.equals(parameter1.parameter) : parameter1.parameter != null) return false;
        if (type != null ? !type.equals(parameter1.type) : parameter1.type != null) return false;
        return name != null ? name.equals(parameter1.name) : parameter1.name == null;

    }

    @Override
    public int hashCode() {
        int result = parameter != null ? parameter.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (required ? 1 : 0);
        return result;
    }

    private static Class<?> noPrimitives(Class<?> destination) {
        if(!destination.isPrimitive()){
            return destination;
        }
        if(destination == Long.TYPE){
            return Long.class;
        }
        if(destination == Integer.TYPE){
            return Integer.class;
        }
        if(destination == Boolean.TYPE){
            return Boolean.class;
        }
        if(destination == Character.TYPE){
            return Character.class;
        }
        if(destination == Byte.TYPE){
            return Byte.class;
        }
        if(destination == Float.TYPE){
            return Float.class;
        }
        if(destination == Double.TYPE){
            return Double.class;
        }
        throw new RuntimeException("Unhandled primitive type "+destination.getCanonicalName());
    }
}
