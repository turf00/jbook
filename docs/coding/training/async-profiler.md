# Async profiler with Andrei Pangin

Source video: <https://www.youtube.com/watch?v=H6glyrKQlg8&list=PLNCLTEx3B8h4Yo_WvKWdLvI9mj1XpTKBr&index=1>

## Types of profiling

### Tracing

* Usually instrumentation of bytecode
* Slow and therefore cannot be used for production

### Sampling

* Periodic snapshots of what the app is doing
* Lighter weight than tracing and can be used in production
* The sampling period will control the amount of overhead
* Andrei uses it in prod for their app

## Sampling in depth

* In Java code: `Thread.getAllStackTraces()` returns a `Map<Thread, StackTraceElement[]>`
* Then there is a JVMTI method with some extra information
* 1000 threads:
  * 50ms latency
  * ~10MB results size
* Advantages are it is simple VisualVM, YourKit

## Demo 1

* VisualVM is safepoint based, therefore it shows the `Thread.currentThread().isAlive()` is where all the time is spent.
* Then he tries it with JFR and states that JMC showed nothing as it only took one sample at 10s.

## Safepoint

* HotSpot JVM stops all threads to take a thread dump but requires somewhere in the code to do this.
* Safepoints allow the JVM to take a thread dump at this point.
* Safepoints are usually at:
  * End of the method
  * Loops
* Long linear piece of code won't appear in the stack trace due to the fact that there is no safepoint to allow this to be captured.

## Native methods

* JVM has no clue when executing native method, as to whether it is sleeping or actually doing real work.
* Busy client versus idle client will show up at taking equal amount of time.
  
## Solving GetAllStackTraces problems

* Avoid SafePoint bias
* Skip idle threads
* Profile native code correctly

## AsyncGetCallTrace API

* This is an internal HotSpot API to get stack traces from current thread.
* Native code to call using signal handler, probably timer handler.
* Honest profiler and async profiler used this
* API is private and not documented.
* Compares with honest profilers against native method example above, which shows the correct values for native methods that aren't doing anything.
* Skips idle threads
* No safepoint bias
* Another JVM option required

## Important 01

* When executing with AsyncGetCallTrace you need to specify `-XX+DebugNonSafepoints -XX:+UnlockDiagnosticVMOptions` as HotSpot generates debug info only at Safepoints.
* This means that for serveral inlined methods, without this flag then you won't see the inlined methods at all.

## AsyncGetCallTrace fails

* There are bugs in teh JVM that prevent this from working each time.
* Async profiler has introduced work arounds to traverse the stack correctly in these cases, tricks as the author called them.
* See <https://bugs.openjdk.java.net/browse/JDK-8178287>

## PMU

* Starting with Pentium all chips have hardware performance monitoring.
* Hundreds of different counters in modern CPUs.
* It can be configured to generate HW interrupt when certain counter overflows.  We can use this to do profiling.
* Counters = cycles, instructions, cache misses, branch misses

## Linux PMU

* perf_event_open (peo)
* Linux syscall, subscribe  to HW/OS events
* peo also allows some profiling in hardware

## perf

* Example

```bash
perf record -F 1009 java
perf report
```

* Takes a sample 1009 times per second, the above is not a typo but uses a prime
  number rather than 1000 to avoid collision with other schedule events in the
  system.
* We don't see anything about java because perf doesn't understand Java, JIT, etc.
* We need to tell perf to understand the mapping
* perf-map-agent can collect this information to provide a mapping file for use by perf.
* It requires a specific flag to be set `-XX:+PreserveFramePointer` flag.
* perf uses FramePointers
* Each stack frame represents function or method.
* Base slot of each frame points to previous frame, therefore we can walk through collecting the stack trace.
* How to find the first frame?  The JVM frame pointer register points to this, but HotSpot uses frame pointer as regular general purpose register by default.
* This flag reverses this optimisation, which costs <5% overhead

## Flamegraph

* y-axis = stack trace, higher = deeper stack
* Length of rectangle on x-axis = total time spent in this method.
* This is sampling so wouldn't be correct to state that this is time, seen more times on sampling.
* You can search and it states at the bottom right the number matches in terms of % times seen.
* Green = Java method
* Yellow = C++ method JVM
* Red = native method
* Brown = Linux Kernel code

## Problems with perf

* Perf sees interpreter as single piece of machine code, it doesn't understand what interpreted method is being executed, therefore in his example its difficult to understand what method is using the CPU.
* Requires special agent
* Requires flag to be set
* Needs some things set on the Linux kernel level
* Perf is limited by default to 127 stack frames
* Perf profiles are very large
* 1000 threads for 60s = GB output file

# Async Profiler part 2

Video: <https://www.youtube.com/watch?v=WnRngFMBe7w&list=PLNCLTEx3B8h4Yo_WvKWdLvI9mj1XpTKBr&index=2>

## Mixed approach

+ Good to mix approach from perf_event_open for kernel, native, use HW counters and then use the AsyncGetCallTrace API for Java.
+ This is the method used in async profiler
+ The tricky part is how to merge the two stacks from both sources.

## How to use flamegraphs

+ Look for leaf flames
+ Look for long leaf flames

## Using Async profiler

* You can attach later but you should use `-XX:+UnlockDiagnosticVMOptions -XX+DebugNonSafepoints` if you intend to do that.

### Demo3

#### FileReader

* He provided a number of options for buffers to use for reading a file to provide the best IO.
* He originally went for 32M as the buffer to use, with the thinking being that we have less system calls, etc.
* In the case he tested 16M was more performant than 32M.
* The page faults in the kernel were caused by malloc using a different implementation for the size of the allocation.  In the case of 32M it involved paging but with a smaller buffer it was ok.

#### nanoTime

* Strange behavioiur of nanoTime
* When is colleague upgraded his PC, all his benchmarks became much slower.
* The reason was nanoTime was much slower on this upgrade.
* There will be no difference with a Java only profiler.
* With async you can see that there are system calls in brown.
* This is related to multiple available clock sources being in Linux.
* I found an issue when testing with J8/J11, with and without the DebugNonSafePoints setting.
* Example local execution on Linux `5.4.0-58-generic`:

![Flamegraph broken](nanotime-profile-broken.svg)

* Here we see `[unknown_Java]` which is obviously the call to gettime.

Slow time on EC2 due to clock source being XEN by default, see here: <https://aws.amazon.com/premiumsupport/knowledge-center/manage-ec2-linux-clock-source/>

### Wall clock profiling

* Can start as an agent
* CPU won't always show why your startup is slow.
* The time is not always spent on CPU intensive work, may be disk IO, netIO (DB, etc), logs etc
* Wall clock profiling handles:
  * Thread.sleep
  * Object.wait / Condition.await
  * Wait to get lock/semaphore
  * Wait for IO

### Lock contention

* Special mode for lock contention

```bash
./profiler.sh -e lock -o flamegraph=total
```

* Counts the amount of time spent acquiring locks
* This applies for reentrant locks, etc
* `flamegraph=total` will then show total time in nanoseconds

PAUSED at 39:00 on the second video.

# Async Profiler part 3

<https://www.youtube.com/watch?v=bTDmpwhwy3E&list=PLNCLTEx3B8h4Yo_WvKWdLvI9mj1XpTKBr&index=3>

## How many objects created?

* Not always obvious how many objects are being created, even with simple code.
* He gives an example about timezones and alloc being out of control.
* The bug was the timezone was unknown and therefore it was creating its rules each time.

## VisualVM Sampler

* Sampler = snapshot of histogram of the objects on the heap at that point.

## Profiler

* Profiler = bytecode instrumentation, added to all places where new objects are allocated. More accurate but slower.
* They optimiste by getting sample each n times

## Dtrace/SystemTap

* One other method that worked for HotSpot is allocation probe.
* This worked in JDK8 but may not work in 
* All allocs are slower, large overhead

## Other tools

* Aprof, instrumenting profiler but very optimised; <https://github.com/devexperts/aprof>
* Allocation instrumenter, actually a framework for writing your own allocation tests. <https://github.com/google/allocation-instrumenter>

## Overhead

* They have a large overhead apart from JMC.

## JFR

* JFR has very low overhead < 5% overhead for capturing allocation.
* Now OS and backported to 8u262
* Gives you the stack trace to where the objects are allocated from.

## TLAB (Thread Local Allocation Buffer)

* If enough space in TLAB, use simple TLAB alloc
* If not enough space in TLAB, 2 options:
  * Allocate directly in EDEN
  * Or a new TLAB is allocated and the existing discarded

### Simple TLAB

* Fast: inlined
* Allocation is handled here first if possible
* The allocation requires no synch
* Alloc involves simple pointer increment

### Slower

* Call to VM Runtime
* Outside TLAB
* Allocation of new TLAB
### JFR, Async Profiler

* They are both concerned only with slow pass allocation, i.e. outside of TLAB
* This is tact taken by JFR and Async Profiler
* Intercept slow pass alloc, record stack and object allocated
* Works in OpenJDK 7u40+
* Do commercial features required.

## JDK 11

* JEP-331 low overhead heap profiling
* SampledObjectAlloc and SetHeapSamplingInterval
* Additional boundary added to TLAB
* Virtual boundary can be set to specific size to pick up allocations? Not entirely sure about its operation

## demo7

* Stand -e alloc is the number of allocations but we can also profile for the size of the allocations.
* `-o flamegraph=total` will do this for alloc mode

## Alloc profiling colours

Blue = Alloc in new TLAB
Brown = Alloc outside TLAB

* Outside TLAB means usually the object is too large for a TLAB
* 100KB-2/3MB TLAB size

## G1

* G1GC allows humongous allocations, objects that cover 1 or more G1 regions.
* They usually affect perf badly
* Async profiler can profile any native func
* He uses it to pick up G1GC humongous allocs via: `-e G1CollectedHeap::humongous_obj_allocate`
* Then start the app with `-XX:+UseG1GC`

## Setting TLAB size

* You can set TLAB size with `-XX:TLABSize=10k` 10KB TLAB size, smaller than default
* Special hack to measure every single alloc in app: `-XX:-UseTLAB` which will turn off TLAB.
* The above trick only works with G1GC, other GCs won't show this as they won't go through the VMruntime.

## Capturing native allocs

* These are disabled by default as most alloc is done in Java code and it would clutter the flamegraphs.
* You can capture this information by using cstack option. i.e. `cstack=fp`
* Sometimes you cannot get native traces because native library is compiled without maintaining fp calling convention
* gcc will skip maintaining the linked list of frames, doesn't preserve fps.
* You will then see truncated native stacks
* Modern hw, from Intel Nehelen CPUs support HW stack walking by using Last Branch Record mechanism.
* Processor keeps track of all jumps in the code, function or conditional branch. This allows the ability to match and return associated instructions.
* From Linux 4.2~?, there is option to make perf events API to use this to get HW stacks.
* Using LBR mechanism with OpenSSL we get specific good functions in the flamegraph from the library

## demo 8

* Async profiler has Java API
* We can profile app using Java API rather than JVMTI or attach script.
* This is used with async-profiler.jar

## Profiling in Idea Ultimate

* Similar but has nice feature that it shows the profiling by separate threads or as all grouped together.
* Also the colours are different for the Idea
* It has a nice tree view, etc.
* You can also attach the IDEA profiler to app that is running.
* You can open snapshots by external tool, in JFR or collapsed stack format into Idea.

## demo10 Non-trivial native mem leaks

+ Simple servlet that provides an image by id
+ Gets the image from the resource stream
+ Sends as byte array
+ Requires deps
+ He uses the `malloc` even to see where the data is getting allocated.
+ Profiling malloc is not always the best for the leak as the memory may be released after being allocated.
+ Instead he uses `mprotect` profiling, malloc uses big chunks from memmap.  It calls mprotect to commit more native memory.
+ This is specifically related to the growth of the RSS Resident Set Size.
+ The resource is compressed so the inflator is created.
+ The inflator has a finalize method, therefore the problem would be fixed by the gc kicking in but there are no guarantees.

### FileTest

* Tracepoints in the linux kernel that can be hooked
* Its the logger causing the issue, the log location wasnt right and wasn't rotated correctly.
* He identified that the page cache was growing, it is OS based.
* Therefore he looked from the tracepoints in `perf list` and found one related to page cache, which he used to debug the issue.

## Problems with Flamegraphs

* Flamegraph is aggregated
* Async profiler can write events, all samples if required

## Demo12

* Worried about CPU spikes that happen every 5s
* Because it happens infrequently you cannot see why these spikes happen
* Async profiler can do jfr recording

## Flamescope

* Events in heatmap style
* Flamescope is not installed
* Convert from jfr to flamescope using

The converter is built in the async profiler project:

`java -cp build/converter.jar jfr2nflx <input.jfr> <out.nflx>`

## demo9

How to do HW performance counters

* Example starts two threads, both are incrementing random elements of the array
* Array is one million elements
* 1st thread only touches small amount of array
* 2nd thread touches all of the array
* Normal CPU profiling won't show much
* False sharing will show up using llc-load-misses

## async-profiler events

* Profile `VMThread::execute` for stop the world
* Profile `Deoptimization::uncommon_trap` for
* Profile `java_lang_Throwable::fill_in_stack_trace`
* You can profile any java method, or even any java method in a class

## demo 11

* Scenario is exceptions thrown in the JVM, no sure of the source
* We can use fully qualified method name as the classname

`./profiler.sh -d 5 -f out.svg -e java.lang.NullPointerException.<init> jps`

## Things to look further

* Flamescope

## Tasks

1. Raise issue about nanotime, even when connecting directly as agent it didn't work as expected.
2. demo6 doesn't work as expected due to the classpath failure
3. Second part of the presentation doesn't seem to be there on the site.
4. Find out about mprotect event?

## Questions

* Does Async profiler rely on some Linux signals?
* Async profiler works with what JVMs?
    * It works with: HotSpot, Azul (no IBM :<)
    * YourKit supports Async traces via AsyncGetCallTrace
* Can you profile specific Java threads to decrease overhead
* Do you need sudo permissions to run Profiler?
    * You don't need root to run the image
* Can Async profiler show threads waiting in native code? (real logic question!)
    * Profiling in wall clock mode will show this, though you need to search for wait, specifically in the output and it can be hard to see.
* Is there a tool that builds flamegraph from multiple instances?
    * It is possible with JFR, collapsed stack traces
    * These can then be concatenated with simple file concat
    * Can be opened in IDEA
    * Or use async converter to convert collapsed stack traces to flamegraph
* Common problem, small functions which runs very fast mostly, 100 microseconds
    * Option 1 reduce the profiling interval `-i 100us`, high overhead in production
    * Option 2: include, exclude to filter interesting stack traces
    * Option 3: turn on/off profiling at specific points
    * Option 4: `--begin --end` when particular native function is reached