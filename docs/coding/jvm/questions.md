# Questions

# Question 1

`Map<Classloader, Map<String, MyObject>>`

- If Classloader (WEAKREF) --> String (Strong) -> (Strong) MyObject
- MyObject will have a reference to its class and also to its ClassLoader, does this mean we cannot collect it, if it disappears
- Will be able to be collected, as the root is gone for this object, no matter the cycles, etc or Object A that refers to Object B and vice versa

## Question 02: How does HashMap work

- HashMap creates a series of buckets for keys to be stored in.
- a `Node`

```java
 static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next;
...
```
