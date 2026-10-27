---
title: JVM Information
---

This page contains links for information on the JVM.

# Useful links

+ Changes from Java version to version: https://javaalmanac.io/

# Child pages

[Async Profiler](async.md)

[Classloading](classloading.md)

[GC](gc.md)

[Generics](generics.md)

[Heap Dump Analysis Tools](heap-dump-analysis/heap-dump-tool-analysis.md)

[Java Libs](libs.md)

[JIT](jit.md)

[JFR](jfr.md)

[JVM Assembly](jvm-asm.md)

[JVM Flags](jvm-flags.md)

[JVM K8](jmv-k8.md)

[JVM Memory Model](memory-model.md)

[JVM Troubleshooting](jvm-troubleshooting.md)

[Libs](libs.md)

[Metaspace](metaspace.md)

[Object Layout](object-layout.md)

[Questions](questions.md)

[Threads](threads.md)

[Tools](tools.md)

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

## Java 25 and up

- There exists a mode for J25 and up that uses Compressed Object Headers, i.e. Project Lilliput

# Safepoints

A JVM safepoint is a point where the JVM knows a Java thread is in a well-defined state, so the JVM can safely inspect or manipulate that thread and its memory.

The important idea is that the JVM cannot necessarily stop a thread at an arbitrary machine instruction. JIT-compiled code may have values in registers, partially updated stack frames, or optimized-away variables. At a safepoint, the JVM has enough metadata to understand the thread's state.

Typical safepoint locations include loop back-edges, method calls/returns, and other places inserted by the JIT where checking for a safepoint is relatively cheap.

The JVM may request a global safepoint. Threads then run until they reach a safepoint, stop there, the JVM performs the operation, and afterward the threads continue.

Common reasons include:

Garbage collection. Especially stop-the-world GC phases. The JVM needs a stable view of object references so it can determine roots and safely move/update objects.
Deoptimization. If assumptions made by the JIT become invalid, optimized machine code may need to be converted back into less-optimized/interpreted execution state.
Class redefinition/retransformation. For example, when instrumentation or debugging changes loaded classes.
Biased-lock revocation in older JVMs. Older HotSpot versions could require safepoints when revoking biased locks. Biased locking is no longer relevant in modern Java versions.
Thread dumps. Operations such as obtaining stack traces may require threads to reach states where their stacks can be examined reliably.
JVMTI/debugging operations. Debuggers and profiling/instrumentation agents can require coordinated JVM state.
Certain JVM housekeeping operations. For example, some code-cache, class-unloading, or runtime data-structure maintenance.

A useful distinction is:

Safepoint ≠ garbage collection.

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
