# Flight Recorder Info

The info below was testing specifically for Java version 8.

As of Java version 11, the Flight recorder is now part of the standard OpenJDK
and does not require enabling of commerical features.  This also means that it
can be used in production.

As of OpenJDK 8u262 JFR is now also available.

# Enabling JVM app for Flight Recorder

## Oracle JVM

For Oracle there are specific flags that need to be set for the app.

Obviously the changes below are only allowed on non-production systems unless
you have paid for licenses from Oracle.

```
-XX:+UnlockCommercialFeatures -XX:+FlightRecorder
```

It is also recommended to enable the two flags below.

Out of the box the method profiler will use safepoint boundaries when profiling
methods, which may skew results. When enabling the flags below when necessary,
you will get more accurate profiling results on where the time is spent in your
app. This is why Flight recorder is preferred over other profilers such as
YourKit, VisualVM.

```
-XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints
```

# Capturing a recording

## Flight Recorder - Start with app

```
-XX:StartFlightRecording=duration=20s,delay=5s,settings=profile,filename=recording.jfr
```

## Flight Recorder - Start Recording on running JVM

Start a recording with a time limit, specifying location to save to and compression.

```
jcmd <process id> JFR.start name=Test-01 filename=/app/record-test-01.jfr settings=/somelocation/default-with-alloc duration=30s stackdepth=1024
```

## Flight Recorder - Start recording with no duration defined

Start a recording without a time limit, a future command will be executed to dump manually to a file.

```
jcmd <process id> JFR.start name=Test-07 settings=profile
```

## Flight Recorder - Dump recording that is running

Dump the recording when enough data has been captured.

```
jcmd <process id> JFR.dump name=Test-08 filename=/app/test-08.jfr
```

# Flight Recorder - Enable Allocations capture

Copy default.jfr and edit the file in <jvm>/jre/lib/jfr/
Edit the file (or better yet a copy of it) and change the following to enable allocation tracking.
The profile.jfr also includes allocation capture.

```xml
<flag name="allocation-profiling-enabled" label="Allocation Profiling">true</flag>

<event path="java/object_alloc_in_new_TLAB">
  <setting name="enabled" control="allocation-profiling-enabled">true</setting>
  <setting name="stackTrace">true</setting>
</event>

<event path="java/object_alloc_outside_TLAB">
  <setting name="enabled" control="allocation-profiling-enabled">true</setting>
  <setting name="stackTrace">true</setting>
</event>
```

# Enable Physical Memory capture more frequently

As default it seems to be every chunk, if you want it more frequently override the default.

```xml
<!-- Default -->
<event name="jdk.PhysicalMemory">
  <setting name="enabled">true</setting>
  <setting name="period">everyChunk</setting>
</event>
<!-- every 15s -->
<event name="jdk.PhysicalMemory">
  <setting name="enabled">true</setting>
  <setting name="period">15s</setting>
</event>
```

# Links

<https://developers.redhat.com/blog/2020/08/25/get-started-with-jdk-flight-recorder-in-openjdk-8u/>
