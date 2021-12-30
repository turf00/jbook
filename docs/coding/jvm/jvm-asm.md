# JVM Asm output

Or how to output assembly for Java code. The following is true as of Dec 2021 and was tested on Ubuntu 21.10.

This covers both output via standard Java execution and JMH.

The intial setup required is for the library to generate the assembly from Java code.

## Setting up fcml

Docs for fcml can be seen here: <http://www.fcml-lib.com/manual.html#examples-hsdis>

1. Download the disassembler from: <https://github.com/swojtasiak/fcml-lib/releases>
2. Build everything as per the docs for that project
3. cd examples/hdis
4. make
5. cp .libs/libhsdis.so.0.0.0 to ${JAVA_HOME}/jre/lib/amd64/hsdis-amd64.so 

Now everything should be in place to execute an app with disassembly.

## Testing for standard execution

See info here: <https://mechanical-sympathy.blogspot.com/2013/06/printing-generated-assembly-code-from.html>
And here: <https://wiki.openjdk.java.net/display/HotSpot/PrintAssembly>

In this example we use the following code:

```java

public class App {
    public static void main(final String[] args) {
        int total = 1;
        for (int i = 1; i <= 200000; i++) {
            total = calc(total, i);
        }
        System.out.println("Total is: " + total);
    }

    public static int calc(final int current, final int add) {
        return current + add;
    }
}
```

Firstly compile via: `javac App.java`

```sh
# All code can be output via but it is hard to follow:
java -XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly App

# This focuses on the method we are interested in:
java -XX:+UnlockDiagnosticVMOptions '-XX:CompileCommand=print,*.main' App
```

## Executing with jmh

Jmh includes a profiler that can do disassembly output.

```sh
java -jar build/libs/myjar.jar 'MyBenchmark' -wi 3 -i 1 -f 1 -prof perfasm
```
