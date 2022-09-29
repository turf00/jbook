# Handling Interrupt on Blocking Threads

A good document here on the topic: <https://web.archive.org/web/20130304152434/http://www.ibm.com/developerworks/java/library/j-jtp05236/index.html>.
Basically if the thread is blocked on a method such as `object.wait();` then interrupting it will throw an `InterruptedException`.
If you interrupt the thread and its running all that gets done is the flag Thread.isInterrupted is set.
Therefore, handle for instance in this case consuming from a `BlockingQueue`:

```java

void do() {
    final Thread current = Thread.currentThread();
    // With this check both when blocking and when running, if another thread interrupts us we will exit
    while(!current.isInterrupted()) {
        // blocks waiting for something
        try {
            final Object some = blockingQueue.take();
        } catch (final InterruptedException e) {
            current.interrupt();
        }
    }
}
```
