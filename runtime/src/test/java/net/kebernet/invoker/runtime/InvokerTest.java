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

import net.kebernet.invoker.runtime.impl.AbstractMatchTest;
import net.kebernet.invoker.runtime.impl.MatchTestClass;
import org.junit.Test;

import static org.junit.Assert.*;

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

}