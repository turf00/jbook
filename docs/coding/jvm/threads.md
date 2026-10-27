---
title: Threads
---

## Thread States

- New
- Runnable
- Running
- Terminated
- Blocked: Waiting for access to sync block or re-access after calling `wait()`
- Waiting: `Object.wait()` no timeout, `Thread.join()` no timeout, `LockSupport.park()`
- Timed Waiting: Thread state for a waiting thread with a specified waiting time. A thread is in the timed waiting state due to calling one of the following methods with a specified positive waiting time: `Thread.sleep` `Object.wait` with timeout, `Thread.join` with timeout, `LockSupport.parkNanos`, `LockSupport.parkUntil`

## Virtual Threads

- Benchmarking scenarios: <https://github.com/sandeepmb/jdk-virtual-threads-benchmark> & <https://www.infoq.com/articles/virtual-threads-after-jdk24/>

### Before J24 - Pinning

- <https://netflixtechblog.com/java-21-virtual-threads-dude-wheres-my-lock-3052540e231d>
