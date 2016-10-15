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

import net.kebernet.invoker.runtime.annotations.AnnTestClass;
import net.kebernet.invoker.runtime.annotations.GET;
import net.kebernet.invoker.runtime.annotations.Name;
import net.kebernet.invoker.runtime.annotations.POST;
import net.kebernet.invoker.runtime.impl.AbstractMatchTest;
import net.kebernet.invoker.runtime.impl.MatchTestClass;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by rcooper on 10/14/16.
 */
public class InvokerTest extends AbstractMatchTest {

    @Test
    public void simpleTest() throws InvokerException {
        MatchTestClass testClass = new MatchTestClass();
        Invoker instance = new Invoker();
        // make call
        long incept = System.currentTimeMillis();
        String result = instance.invoke(testClass, "testMethod", values1);
        assertEquals("testMethod1", result);
        long firstTime = System.currentTimeMillis() - incept;
        // repeat call
        incept = System.currentTimeMillis();
        result = instance.invoke(testClass, "testMethod", values1);
        assertEquals("testMethod1", result);
        long secondTime = System.currentTimeMillis() - incept;
        // test memoization
        assertTrue(firstTime > secondTime);
        result = instance.invoke(testClass, "testMethod", values2);
        assertEquals("testMethod2", result);

    }


    @Test
    public void testCustomAnnotations() throws InvokerException {
        Invoker instance = new Invoker(InvokerTest::methodName, InvokerTest::paramName);
        AnnTestClass target = new AnnTestClass();
        String get = instance.invoke(target, "GET", Arrays.asList(new ParameterValue("path", "foo")));
        String post = instance.invoke(target, "POST", Arrays.asList(new ParameterValue("path", "bar")));
        assertEquals("GET foo", get);
        assertEquals("POST bar", post);
    }

    private static String paramName(Parameter p){
        Name n = p.getAnnotation(Name.class);
        return n.value();
    }

    private static String methodName(Method m){
        if(m.getAnnotation(GET.class) != null)
            return "GET";
        else if (m.getAnnotation(POST.class) != null)
            return "POST";
        else return null;
    }

}