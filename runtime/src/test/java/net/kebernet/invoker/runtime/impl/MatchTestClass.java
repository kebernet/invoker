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
import net.kebernet.invoker.annotation.Parameter;

/**
 * Created by rcooper on 10/14/16.
 */
public class MatchTestClass {

    @Invokable(value = true, invocationName = "testMethod")
    public String testMethod1(
            @Parameter("param1") CharSequence param1,
            @Parameter("param2") CharSequence param2,
            @Parameter("param3") CharSequence param3
    ) {
       return "testMethod1";
    }

    @Invokable(value = true, invocationName = "testMethod")
    public String testMethod2(
            @Parameter("param1") CharSequence param1,
            @Parameter("param2") CharSequence param2,
            @Parameter(value = "param3", required = false) CharSequence param3
    ) {
        return "testMethod2";
    }

    @Invokable(value = true, invocationName = "testMethod")
    public String testMethod3(
            @Parameter("param1") String param1,
            @Parameter(value = "param2", required = false) String param2,
            @Parameter(value = "param3", required = false) String param3
    ) {
        return "testMethod3";
    }

    @Invokable(value = true, invocationName = "testMethod")
    public String testMethod4(
            @Parameter(value = "param1", required = false) String param1,
            @Parameter(value = "param2", required = false) String param2,
            @Parameter(value = "param3", required = false) String param3
    ) {
        return "testMethod4";
    }
}
