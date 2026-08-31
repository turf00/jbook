# GC

# Algorithms in latest (21-24) JVMs

- Obviously OpenJ9 is different, we are dealing here mainly with OpenJDK based JVMs.

## G1

- Heap is divided into regions. 
- Humungous allocation, is alloc >= region size/2
  - Allocated directly into old generation
  - Allocation can trigger an early concurrent marking cycle
  G1 must find contiguous regions for the humongous region
  - Internal waste occurs in the object's end region
  - Bursts can fragment available regions and, in severe cases, contribute to evacuation or allocation failure.

### Links

<https://abiasforaction.net/understanding-jvm-garbage-collection-part-9-garbage-first-g1-garbage-collector-gc/#:~:text=Garbage%20First%20(G1)%20Garbage%20Collector%20(G1))&text=The%20young%20generation%20algorithm%20is,algorithm%20with%20an%20incremental%20compacting.>
