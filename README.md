Invoker
=======

[![Build Status](https://travis-ci.org/kebernet/invoker.svg?branch=master)](https://travis-ci.org/kebernet/invoker)

This is a simple Java library for assisting in dynamically invoking methods on classes.

Reasoning
---------

We a love JAX-RS and other APIs that let us deal with network formatted data through interacting with
simple annotated POJO type classes. Sometimes you are confronted with a framework where you would
like to have that kind of easy testability and comprehension, but you are nailed to another framework
with an intrusive implementation API. "Boy," you think to yourself, "I'd love to put together a 
thin little layer to make this easier."

Invoker is a simple library that handles the plumbing for you create your own framework that 
works in a similar way.

JAX-RS might let you do something like: 

```
      @GET
      public MyObject findTheThing(@PathParam("id") String id) {

```

Invoker's more generic declaration for a JAX-RS type framework might look like this:

```
    @Invokable(value = true, invocationName = "GET")
    public MyObject findTheThing(@Parameter("id") String id) {

```

But you can also provide your on strategies and use your own or third party annotations to drive
the invoker.

It is broken into two libraries, "annotation" and "runtime". The annotation library just has the 
annotations you need on your POJOs. This can be included around any project without too much weight. 
The runtime library contains the classes need to actually invoke methods on your annotated objects.

Usage
-----

Add the repository...

Gradle:

```
    repositories {
        maven {
            url  "http://dl.bintray.com/kebernet/maven" 
        }
    }
```

Maven:

```
    <repository>
        <id>bintray-kebernet-maven</id>
        <name>bintray</name>
        <url>http://dl.bintray.com/kebernet/maven</url>
    </repository>
```

Add the dependencies...
 
```net.kebernet.invoker:annotation:0.0.1``` or 
```net.kebernet.invoker:runtime:0.0.1```, depending on your project needs. Most likely you
want ```runtime```.

Annotate your Java class...

``` 
    @DefaultInvokable(true) // <- this optional. If you set this to true, then every public method
                            // on the class with be invokable by name
    public class MyPOJO {
    
        @Invokable // <- makes the method invokable if you haven't set a default of "true" for the 
                   // class
        public String myMethod(
        @Parameter("intParam") int i,  // <- You can set parameters as required = false, but 
                                       // if you take a primitive as a parameter, it is always 
                                       // required.
        @Parameter("stringParam") String s){
            return s+" "+i;
        }
    }
```

Invoke your method...

```
    Invoker invoker = new Invoker(); // <- The invoker keeps internal caches for performance. 
                                     // it is best to use a single instance for all your 
                                     // invocations.
    
    
    invoker.register(MyPOJO.class); // <- this is optional as well, however introspecting the 
                                    // class data can be a heavy-weight operation, so if you 
                                    // register your classes in advance, you can improve the 
                                    // first-call performance.
    List<ParameterValue> arguments = Arrays.asList(
        new ParameterValue("stringParam", "The answer is: "),
        new ParameterValue("intParam", 42) // <- Order doesn't matter here. Just the names.
    );
    
    MyPOJO instance = new MyPOJO();
    String result = invoker.invoke(target, "myMethod", arguments);
    
```
Overloaded methods/names...

If you have an overloaded method, or you overload an invocation name, the Invoker will try to 
find the best-fit method for the parameters you are passing. Exact matches to the supplied 
parameters are given preference, after that, it will look at what are the non-required parameters
and look for something that can handle the request.


Providing custom annotation support...

If, for example, you wanted to support the JAX-RS annotations with the Invoker, you can provide
function references to extract the invocation names and parameter names when you construct it:

```
    private static Invoker createJaxRsInvoker(){
        return new Invoker(InvokerTest::methodName, InvokerTest::paramName);
    }

    private static String paramName(Parameter p){
            PathParam pp = p.getAnnotation(PathParam.class);
            return pp == null ? null : pp.value();
        }
    
        private static String methodName(Method m){
            if(m.getAnnotation(GET.class) != null)
                return "GET";
            else if (m.getAnnotation(POST.class) != null)
                return "POST";
            else return null; // <- returning null here means that
                              // a method will not be invokable unless you put a 
                              // @DefaultInvokable annotation on the class as well.      
        }
```