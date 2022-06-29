# Linux Monitoring

## Memory

```bash
free -m
```

List of availble memory and used memory before/after buffers and caches are taken into consideration.

Does not include the memory used for the kernel caches, i.e. Slab.  This memory will contribute to the overall used memory but will not show against the process.

Show resident set size (RSS) and virtual  memory size (VMZ) for processes, also lists the command.

```bash
ps -eo pid,rss,vsz,cmd
```

If you take the sum of RSS + Slab -Shared then it is roughly equivalent to used memory from free (-buffers, caches).

List the current memory usage with breakdowns:

```bash
cat /proc/meminfo
```

List the slab usage

```bash
cat /proc/slabinfo
```

For the above the size (Bytes) can be calculated by multiplying `<num_objs> * <objsize>`.

Also slabtop provides info on the used memory for slab.

```bash
slabtop
```

### References

Droping kernel caches: <https://linux-mm.org/Drop_Caches>

Where is my memory: <https://www.dedoimedo.com/computers/slabinfo.html>

Redhat6 memory info: <https://access.redhat.com/solutions/406773>

Slabs: <https://medium.com/@dhelios/memory-caches-and-slab-objects-c1de113ce235>

# pidstat

Show stats for a particular process including voluntary and non-volunatary context switches

```bash
pidstat -w -p <pid> <interval secs>
```

e.g. `pidstat -w -p 1345 1`

# strace

Capture system calls for app

```bash
strace -c -f -p <pid>
```

-c = capture count
-f = trace child processes

To exit after a specific time period

```bash
timeout <secs> strace -c -f -p <pid>
```

# count threads

Count the number of threads for a process

```bash
cat /proc/<pid>/status
```

There is the list is threads or

```bash
cat /proc/<pid>/status | grep -i threads
```

# disk io latency

`iostat -dxt 1 sdb`

# Check env for processes

Find the process id then

`strings /proc/<pid>/environ`

or

`cat /proc/28818/environ | tr '\0' '\n'`
