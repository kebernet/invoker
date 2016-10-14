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

/**
 * Created by rcooper on 10/13/16.
 */
public class NamedParameter {
    private final java.lang.reflect.Parameter parameter;
    private final Class type;
    private final String name;
    private final boolean required;

    public NamedParameter(@Nonnull java.lang.reflect.Parameter parameter) {
        this.parameter = parameter;
        this.type = parameter.getType();
        Parameter name = parameter.getAnnotation(Parameter.class);
        if(name == null){
            this.name = parameter.getName();
        } else {
            this.name = name.value();
        }
        if(this.name == null){
            throw new RuntimeException("Could not get name for parameter "+parameter.toString());
        }
        this.required = (name != null && name.required()) || parameter.getType().isPrimitive();
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
            .append('}').toString();
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
}
