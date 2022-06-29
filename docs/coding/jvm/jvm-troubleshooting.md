# JVM Troubleshooting

This lists useful commands and options for troubleshooting performance or other
issues with the JVM.

# Find JVMs available to connect through jcmd

`jcmd`

This will list all the JVM processes it can find with their pids.

# List all commands available via jcmd in the JVM

`jcmd <pid> help`

This will for the particular JVM output the commands available.

# Capturing a heap dump from JVM

Take a dump of all objects, not just live objects in the binary format.  This does not force a full GC and may include dead objects.

```bash
jmap -dump:format=b,file=<filename.hprof> <pid> 
```

Heap dump with forced full GC before dump:

```bash
jmap -dump:live,format=b,file=<filename.hprof> <pid> 
```

<https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jmap.html>

Using jcmd (note does not seem to support live objects only)

```bash
jcmd <pid> GC.heap_dump filename=<file>
```

# Heap Histogram

Live objects without forcing a full GC.

```bash
jcmd <pid> GC.class_histogram
```

Includes dead objects.

```bash
jmap -histo <pid>
```

Forces a full gc before outputting the histogram.

```bash
jmap -histo:live <pid>
```

# Dump Native Memory Summary

Must be enabled with JVM flag `-XX:NativeMemoryTracking=summary`

Dumps a summary of the usage of the native memory by the JVM.

```bash
jcmd <pid> VM.native_memory summary
```

You can also take a baseline and do a comparison:

```bash
jcmd <pid> VM.native_memory baseline
```

You can then compare the baseline to the current:

```bash
jcmd <pid> VM.native_memory summary.diff
```

# Dump class stats (>=J8)

Must be enabled with JVM flag `-XX:+UnlockDiagnosticVMOptions`

```bash
jcmd <pid> GC.class_stats
```

# Take thread dump

```bash
kill -3 <pid>
```

The output for `kill -3` goes to the stdout for the app in question, therefore do not expect it to be output at the shell where you execute the command.

Or using jcmd

```bash
jcmd <pid> Thread.print
```

+ jcmd will output to the shell where you execute the command and can be piped to a file.
+ jcmd requires the JDK be installed it is not part of the JRE.

In Java 11+ the dump also includes how long the thread has been running as well as the CPU time for the thread.
Example

```bash
"http-nio-8080-ClientPoller" #42 daemon prio=5 os_prio=31 cpu=7.57ms elapsed=102.11s tid=0x00007f7bfc97d800 nid=0x14103 runnable  [0x000070000da89000]                              │
   java.lang.Thread.State: RUNNABLE                                                                                                                                                 │
        at sun.nio.ch.KQueue.poll(java.base@11.0.11/Native Method)                                                                                                                  │
        at sun.nio.ch.KQueueSelectorImpl.doSelect(java.base@11.0.11/KQueueSelectorImpl.java:122)                                                                                    │
        at sun.nio.ch.SelectorImpl.lockAndDoSelect(java.base@11.0.11/SelectorImpl.java:124)                                                                                         │
        - locked <0x00000007f8247760> (a sun.nio.ch.Util$2)                                                                                                                         │
        - locked <0x00000007f8247608> (a sun.nio.ch.KQueueSelectorImpl)                                                                                                             │
        at sun.nio.ch.SelectorImpl.select(java.base@11.0.11/SelectorImpl.java:136)                                                                                                  │
        at org.apache.tomcat.util.net.NioEndpoint$Poller.run(NioEndpoint.java:709)                                                                                                  │
        at java.lang.Thread.run(java.base@11.0.11/Thread.java:829)
```

We can see here the `cpu=<how much time spent>` and `elapsed=<time running>`
