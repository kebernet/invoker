Invoker
=======

[![Build Status](https://travis-ci.org/kebernet/invoker.svg?branch=master)](https://travis-ci.org/kebernet/invoker)

This is a simple Java library for assisting in dynamically invoking methods on classes.

Reasoning
---------

We a love JAX-RS and other APIs that let us deal with network formatted data through interacting with
simple annotated POJO type classes. Invoker is a simple library that handles the plumbing for you to
create your own framework that works in a similar way.

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

It is broken into two libraries, "annotation" and "runtime". The annotation library just has the 
annotations you need on your pojos. This can be included around any project without too much weight. 
The runtime library contains the classes need to actually invoke methods on your annotated objects.

Usage
-----

