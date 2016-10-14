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

/**
 * Created by rcooper on 10/13/16.
 */
public class NamedParameter {
    private final java.lang.reflect.Parameter parameter;
    private final Class type;
    private final String name;
    private final boolean required;

    public NamedParameter(java.lang.reflect.Parameter parameter) {
        this.parameter = parameter;
        this.type = parameter.getType();
        Parameter name = parameter.getAnnotation(Parameter.class);
        if(name == null){
            this.name = parameter.getName();
        } else {
            this.name = name.value();
        }
        this.required = name.required();
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
}
