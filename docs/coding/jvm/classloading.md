# Classloading

There is a hierarchy of classloaders

## Built in classloaders

- Bootstrap class loader – built-in class loader, is represented as null.
- Platform class loader – Loads the platform classes, which include the Java SE platform APIs, their implementation classes, and JDK-specific run-time classes. The platform class loader is the parent of the system class loader
- System class loader – Also known as application class loader, loads classes on the application class path, module path, and JDK-specific tools

## Loading a class

- Classloaders typically, but not always, delegate to the parent classloader to find the class initially and then themselves try and load the class, if the parent was unable to.
- The methods and constructors of objects created by a class loader may reference other classes. To determine the class(es) referred to, the Java virtual machine invokes the loadClass method of the class loader that originally created the class.

## When are classes loaded

- import statements are not relevant in any way for classloading
- When a new class is instantiated, or a static method or field is touched, then the class will be loaded.

## Defining a class

- Normally, the Java virtual machine loads classes from the local file system in a platform-dependent manner.
- However, some classes may not originate from a file; they may originate from other sources, such as the network, or they could be constructed by an application. The method defineClass converts an array of bytes into an instance of class Class. Instances of this newly defined class can be created using Class.newInstance.

## Thread ContextClassLoader

- Each Thread has a context classloader that can be fetched or set.
- The context ClassLoader is provided by the creator of the thread for use by code running in this thread when loading classes and resources. If not set, the default is the ClassLoader context of the parent thread. The context ClassLoader of the primordial thread is typically set to the class loader used to load the application.
- Seems that it needs to be explicitly used when loading classes and resources.
