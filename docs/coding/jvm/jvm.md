---
title: JVM Information
---

This page contains links for information on the JVM.

# Useful links

+ Changes from Java version to version: https://javaalmanac.io/

# Child pages

[JVM Flags](jvm-flags.md)

[JVM Troubleshooting](jvm-troubleshooting.md)

[JVM Assembly](jvm-asm.md)

[Heap Dump Analysis Tools](heap-dump-analysis/heap-dump-tool-analysis.md)

[Object Layout](object-layout.md)

[JIT](jit.md)

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
