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

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by rcooper on 10/14/16.
 */
public class InvokableMethodTest extends AbstractMatchTest {

    @Test
    public void testMatch(){

        assertEquals("testMethod", testMethod1.getName());
        assertEquals("testMethod", testMethod2.getName());
        assertEquals("testMethod", testMethod3.getName());
        long incept = System.currentTimeMillis();
        assertEquals( 0, testMethod1.matchValue("testMethod", values1));
        assertTrue( testMethod1.matchValue("testMethod", values2) < 0);
        assertTrue( testMethod1.matchValue("testMethod", values3) < 0);
        assertTrue( testMethod1.matchValue("testMethod", values4) < 0);

        assertEquals( 0, testMethod2.matchValue("testMethod", values1));
        assertEquals( 0, testMethod2.matchValue("testMethod", values2));
        assertTrue( testMethod2.matchValue("testMethod", values3) < 0);
        assertTrue( testMethod2.matchValue("testMethod", values4) < 0);

        assertEquals( 0, testMethod3.matchValue("testMethod", values1));
        assertEquals( 0, testMethod3.matchValue("testMethod", values2));
        assertEquals( 0, testMethod3.matchValue("testMethod", values3));
        assertTrue( testMethod3.matchValue("testMethod", values4) < 0);
        long firstRun = System.currentTimeMillis() - incept;

        // Run through the set of match ops again to make sure the memoization
        // is increasing performance.
        incept = System.currentTimeMillis();
        assertEquals( 0, testMethod1.matchValue("testMethod", values1));
        assertTrue( testMethod1.matchValue("testMethod", values2) < 0);
        assertTrue( testMethod1.matchValue("testMethod", values3) < 0);
        assertTrue( testMethod1.matchValue("testMethod", values4) < 0);

        assertEquals( 0, testMethod2.matchValue("testMethod", values1));
        assertEquals( 0, testMethod2.matchValue("testMethod", values2));
        assertTrue( testMethod2.matchValue("testMethod", values3) < 0);
        assertTrue( testMethod2.matchValue("testMethod", values4) < 0);

        assertEquals( 0, testMethod3.matchValue("testMethod", values1));
        assertEquals( 0, testMethod3.matchValue("testMethod", values2));
        assertEquals( 0, testMethod3.matchValue("testMethod", values3));
        assertTrue( testMethod3.matchValue("testMethod", values4) < 0);
        long secondRun = System.currentTimeMillis() - incept;

        // Check for performance gain.
        assertTrue(firstRun > secondRun);
    }

    @Test
    public void testSort(){

        List<InvokableMethod> methods = Arrays.asList(testMethod1, testMethod2, testMethod3);
        InvokableMethod.Comparator comparator1 = new InvokableMethod.Comparator("testMethod", values1);
        InvokableMethod.Comparator comparator2 = new InvokableMethod.Comparator("testMethod", values2);
        InvokableMethod.Comparator comparator3 = new InvokableMethod.Comparator("testMethod", values3);
        InvokableMethod.Comparator comparator4 = new InvokableMethod.Comparator("testMethod", values4);


        //Strictest match all values
        Collections.sort(methods, comparator1);
        assertEquals(testMethod1, methods.get(0));

        //Strictest match 2 values
        Collections.sort(methods, comparator2);
        assertEquals(testMethod2, methods.get(0));

        //Strictest match 1 value
        Collections.sort(methods, comparator3);
        assertEquals(testMethod3, methods.get(0));

        //Undefined match 0 values. Just making sure it doesn't throw
        Collections.sort(methods, comparator4);
    }


}