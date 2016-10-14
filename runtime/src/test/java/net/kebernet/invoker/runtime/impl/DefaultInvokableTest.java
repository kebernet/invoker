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

/**
 * Created by rcooper on 10/14/16.
 */
public class DefaultInvokableTest {

    @DefaultInvokable(true)
    public static class AllInvokable {

        public String testMethod(String hello) {
            return "testMethod";
        }
    }

    @DefaultInvokable(true)
    public static class NoneInvokable {

         String testMethod(String hello) {
            return "testMethod";
        }
    }
    @DefaultInvokable(false)
    public static class NoneInvokable2 {

        public String testMethod(String hello) {
            return "testMethod";
        }
    }
}
