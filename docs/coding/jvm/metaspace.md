# Metaspace

Info and links on Metaspace for Java, which was added in J8 to remove PermGen.

+ <https://poonamparhar.github.io/understanding-metaspace-gc-logs/>
+ <https://stuefe.de/posts/metaspace/metaspace-architecture/>
+ <https://stuefe.de/posts/metaspace/what-is-compressed-class-space/>
+ <https://stuefe.de/posts/metaspace/analyze-metaspace-with-jcmd/>
+ <https://stackoverflow.com/questions/54250637/is-compressedclassspacesize-area-contains-maxmetaspacesize-area>
+ <https://blog.gceasy.io/2022/08/23/inspect-the-contents-of-the-java-metaspace-region/>
+ <https://blog.ycrash.io/2022/07/29/inspect-the-contents-of-the-java-metaspace-region/>
+ <https://blog.gceasy.io/2022/07/27/troubleshooting-microservices-outofmemoryerror-metaspace/>
+ <https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/considerations.html>

## Metaspace Parts

+ Metaspace is divided into two regions a Klass part and a non-class part.

## Flags

*Warning MetaspaceSize* is not actually anything to do with the initial size but instead a guide as
to when GC should be applied to the metaspace.

## Why is my metaspace always larger than 1GB

This can be explained here: <https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/considerations.html#typical_heap_printout>

From the document:

> If UseCompressedOops is turned on and UseCompressedClassesPointers is used, then two logically different areas of native memory are used for class metadata. UseCompressedClassPointers uses a 32-bit offset to represent the class pointer in a 64-bit process as does UseCompressedOops for Java object references. A region is allocated for these compressed class pointers (the 32-bit offsets). The size of the region can be set with CompressedClassSpaceSize and is 1 gigabyte (GB) by default. The space for the compressed class pointers is reserved as space allocated by mmap at initialization and committed as needed. The MaxMetaspaceSize applies to the sum of the committed compressed class space and the space for the other class metadata.

So the size is reserved but not committed on startup, however this could prove confusing without understanding the above and when reading the output from various tools.