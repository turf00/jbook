---
title: JVM Information
---

This page contains links for information on the JVM.

# Child pages

[JVM Flags](jvm-flags.md)

# Java native memory

Stats were added in Java 8 when the permgen data was moved off heap.

Enabled by setting the option:

`-XX:NativeMemoryTracking=off|summary|details`

If you enable the summary or detailed information it is then possible to capture
the stats using jcmd:

`jcmd <pid> VM.native_memory_summary`

It is also possible to set a baseline and then produce a diff using jcmd

Start app and capture baseline

`jcmd <pid> VM.native_memory baseline`

Then you can produce a diff

`jcmd <pid> VM.native_memory summary.diff`

You can access the detailed information using jcmd again:

`jcmd <pid> VM.native_memory detail`

# Java object layout

Provides a good guide to the size of the object header in Java and what it is
made up of, for 64bit (compressed oops on and off) and 32 bit JVM.

<https://gist.github.com/arturmkrtchyan/43d6135e8a15798cc46c>

Excellent guide to memory layout:
<http://psy-lob-saw.blogspot.com/2013/05/know-thy-java-object-memory-layout.html>

The tool jol is a library that can be used to identify the expected size of an object.

Here we see the output for testing with jol on a 64bit compressed oops JVM.

```java
public final class MyClassSingleInt {
    public int value;
}

com.bvb.MyClassSingleInt object internals:
 OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
      0    12        (object header)                           N/A
     12     4    int MyClassSingleInt.value                    N/A
Instance size: 16 bytes
Space losses: 0 bytes internal + 0 bytes external = 0 bytes total

public final class MyClassTwoInts {
    public int int1;
    public int int2;
}

com.bvb.MyClassTwoInts object internals:
 OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
      0    12        (object header)                           N/A
     12     4    int MyClassTwoInts.int1                       N/A
     16     4    int MyClassTwoInts.int2                       N/A
     20     4        (loss due to the next object alignment)
Instance size: 24 bytes
Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
```

You can see that the second object with two ints, each of size 4 bytes,
will loose a total of 4 bytes due to the alignment of the object on 8 byte boundaries.

# Compressed Oops

Oops stands for ordinary object pointer and refers to the object references in the JVM.

On 64bit hardware a pointer is sized at 64 bits, whereas in the 32bit world obviously 32bits.

32 bit means the addressable memory is 4GB.

64 bit means the addressable memory is 2^64 bytes, which is a lot.

The use of 64bit pointers in the JVM incurs a performance penalty due to the
extra data that uses up valuable space in the CPU caches.

Java Performance the Definitive Guide lists the penalty as being 5-20% for moving
to 64bit from 32bit.

In order to get round this potential performance issue the JVM uses a trick that
increases performance.

It stores what is really a 35bit pointer in a 32bit memory location/register.
Then when actually using this pointer it shifts it 3 places to the left. This
means obviously that the first three bits are always zero for every pointer.

35 bit means the addressable memory is 32GB.

As the first three places are always zero, the JVM is only able to reference memory
that is divisible by 8.

Objects in the 32bit and 64bit JVM are already aligned on a 8-byte boundary and
therefore this additional overhead should not make any difference.

Heaps <32GB use compressed oops by default since a version of Java 7.

Heaps over 32GB revert to using the 64 bit pointers and therefore are most likely
slower, even if the heap itself is only using (as an example) 500MB extra over 32GB.

# Enable remote debugging

## Java 8 and before

`-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005`

## Java 9+

`-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005`

# Enable JMX without auth

*Warning don't expose this to the world*

Commands to enable Jmx without any authentication

```
-Dcom.sun.management.jmxremote
-Dcom.sun.management.jmxremote.port=[jmx port]
-Dcom.sun.management.jmxremote.local.only=false
-Dcom.sun.management.jmxremote.authenticate=false
-Dcom.sun.management.jmxremote.ssl=false
-Djava.rmi.server.hostname=<hostname or ip used to connect to the remote server>
```

Sometimes it happens that the ip picked up automatically for hostname above is
127.0.0.1 and therefore when connecting remotely it will seem to connect but
will then fail.
