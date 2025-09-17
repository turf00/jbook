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
