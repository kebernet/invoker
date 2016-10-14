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

import net.kebernet.invoker.runtime.ParameterValue;
import org.junit.Before;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created by rcooper on 10/14/16.
 */
public abstract class AbstractMatchTest {

    protected InvokableMethod testMethod1;
    protected InvokableMethod testMethod2;
    protected InvokableMethod testMethod3;
    protected InvokableMethod testMethod4;
    protected List<ParameterValue> values1;
    protected List<ParameterValue> values2;
    protected List<ParameterValue> values3;
    protected List<ParameterValue> values4;

    @Before
    public void setupData(){
        values1 = Arrays.asList(
                new ParameterValue("param1", "value"),
                new ParameterValue("param2", "value"),
                new ParameterValue("param3", "value"));
        values2 = Arrays.asList(
                new ParameterValue("param1", "value"),
                new ParameterValue("param2", "value"),
                new ParameterValue("param3", null));
        values3 = Arrays.asList(
                new ParameterValue("param1", "value"),
                new ParameterValue("param2", null),
                new ParameterValue("param3", null));
        values4 = Arrays.asList(
                new ParameterValue("param1", null),
                new ParameterValue("param2", null),
                new ParameterValue("param3", null));

        values4 = Arrays.asList(
                new ParameterValue("param1", 1),
                new ParameterValue("param2", null),
                new ParameterValue("param3", null));

        testMethod1 = null;
        testMethod2 = null;
        testMethod3 = null;

        for(Method m : MatchTestClass.class.getMethods() ){
            switch(m.getName()){
                case "testMethod1":
                    testMethod1 = new InvokableMethod(m);
                    break;
                case "testMethod2":
                    testMethod2 = new InvokableMethod(m);
                    break;
                case "testMethod3":
                    testMethod3 = new InvokableMethod(m);
                    break;
                case "testMethod4":
                    testMethod4 = new InvokableMethod(m);
                    break;
                default:
                    break;
            }
        }
    }
}
