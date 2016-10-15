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
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

/**
 * Created by rcooper on 10/14/16.
 */
public class IntrospectionDataTest extends AbstractMatchTest {


    @Test
    public void getMethodsSimple() throws Exception {
        IntrospectionData instance = new IntrospectionData(MatchTestClass.class, Optional.empty(), Optional.empty());
        ArrayList<InvokableMethod> methodList = new ArrayList<>(instance.getMethods());
        ArrayList<InvokableMethod> check = new ArrayList<>(Arrays.asList(testMethod1, testMethod2, testMethod3, testMethod4));
        Collections.sort(methodList, (o1, o2) -> o1.getNativeMethod().getName().compareTo(o2.getNativeMethod().getName()));
        Collections.sort(check, (o1, o2) -> o1.getNativeMethod().getName().compareTo(o2.getNativeMethod().getName()));
        assertEquals(check, methodList);
    }

    @Test
    public void bestMatchSimple() throws Exception {
        IntrospectionData instance = new IntrospectionData(MatchTestClass.class, Optional.empty(), Optional.empty());
        assertEquals(testMethod1, instance.bestMatch("testMethod", values1).orElseThrow(RuntimeException::new));
        assertEquals(testMethod2, instance.bestMatch("testMethod", values2).orElseThrow(RuntimeException::new));
        assertEquals(testMethod3, instance.bestMatch("testMethod", values3).orElseThrow(RuntimeException::new));
    }

    @Test
    public void bestMatchOverloaded(){
        List<ParameterValue> values = Arrays.asList(
                new ParameterValue("param1", "value")
        );
        List<ParameterValue> values2 = Arrays.asList(
                new ParameterValue("param1", "value"),
                new ParameterValue("param2", null)
        );
        List<ParameterValue> values3 = Arrays.asList(
                new ParameterValue("param1", "value"),
                new ParameterValue("param2", "value")
        );
        IntrospectionData instance = new IntrospectionData(OverloadWithDifferentLength.class, Optional.empty(), Optional.empty());
        assertEquals("testMethod1", instance.bestMatch("testMethod", values).orElseThrow(RuntimeException::new).getNativeMethod().getName());
        assertEquals("testMethod2", instance.bestMatch("testMethod", values2).orElseThrow(RuntimeException::new).getNativeMethod().getName());
        assertEquals("testMethod3", instance.bestMatch("testMethod", values3).orElseThrow(RuntimeException::new).getNativeMethod().getName());
    }

    @Test
    public void simpleDefaultInvokable(){
        IntrospectionData data = new IntrospectionData(DefaultInvokableTest.AllInvokable.class, Optional.empty(), Optional.empty());
        assertEquals("testMethod", data.getMethods().stream().findFirst().orElseThrow(RuntimeException::new).getNativeMethod().getName());
        NamedParameter param = data.getMethods().stream().findFirst().orElseThrow(RuntimeException::new)
                .getParameters().stream().findFirst().orElseThrow(RuntimeException::new);
        assertEquals("arg0", param.getName());
        assertEquals(String.class, param.getType());
        assertEquals(false, param.isRequired());

    }

    @Test(expected = RuntimeException.class)
    public void noneInvokable() {
        new IntrospectionData(DefaultInvokableTest.NoneInvokable.class, Optional.empty(), Optional.empty());
    }


    @Test(expected = RuntimeException.class)
    public void noneInvokable2() {
        new IntrospectionData(DefaultInvokableTest.NoneInvokable2.class, Optional.empty(), Optional.empty());
    }
}