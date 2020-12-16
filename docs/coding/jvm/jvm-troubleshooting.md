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
