# Collections

## ConcurrentHashMap

- Difficult to judge the entire size of the number of buckets created.
- No upper bound in JRE provided collection.
- The method `addCount(long, int)` seem to be what bumps up the number of buckets in the array

```java
// Initial capacity is 100, we added 4090 items
Initial size changed, was: [0], now: [128], when adding: [0]
Initial size changed, was: [128], now: [256], when adding: [95]
Initial size changed, was: [256], now: [512], when adding: [191]
Initial size changed, was: [512], now: [1024], when adding: [383]
Initial size changed, was: [1024], now: [2048], when adding: [767]
Initial size changed, was: [2048], now: [4096], when adding: [1535]
Size: 2046
Initial size changed, was: [4096], now: [8192], when adding: [3071]
Size: 4090
Total added: 4090
```

- When we hit 3071, then we double the number of buckets, there seems to be no way to deal with keeping it under this limit, no matter the load factor and I guess thats a feature of the HashMap, to prevent too many collisions and poor lookup performance.



- The code in CHMP.addCount(x, check) x being how many elements are added

```java
if (check >= 0) {
            Node<K,V>[] tab, nt; int n, sc;
            while (s >= (long)(sc = sizeCtl) && (tab = table) != null &&
                   (n = tab.length) < MAXIMUM_CAPACITY) {
                int rs = resizeStamp(n) << RESIZE_STAMP_SHIFT;
                if (sc < 0) {
                    if (sc == rs + MAX_RESIZERS || sc == rs + 1 ||
                        (nt = nextTable) == null || transferIndex <= 0)
                        break;
                    if (U.compareAndSetInt(this, SIZECTL, sc, sc + 1))
                        transfer(tab, nt);
                }
                else if (U.compareAndSetInt(this, SIZECTL, sc, rs + 2))
                    transfer(tab, null);
                s = sumCount();
            }
        }
```

- sizeCtl is what controls when we jump and allocate a net array of buckets.

```java
private final Node<K,V>[] initTable() {
        Node<K,V>[] tab; int sc;
        while ((tab = table) == null || tab.length == 0) {
            if ((sc = sizeCtl) < 0)
                Thread.yield(); // lost initialization race; just spin
            else if (U.compareAndSetInt(this, SIZECTL, sc, -1)) {
                try {
                    if ((tab = table) == null || tab.length == 0) {
                        int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
                        @SuppressWarnings("unchecked")
                        Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n];
                        table = tab = nt;
                        sc = n - (n >>> 2);
                    }
                } finally {
                    sizeCtl = sc;
                }
                break;
            }
        }
        return tab;
    }
```

- The most important part here is sc = n - (n >>> 2) so n-n/4 gives us the value when it would be grown.
- In our case at 3071 we hit the limit of sizeCtl and therefore have to grow the buckets.
- All sizes are powers of two for easy calculations
- Thereof

## KeySet View

- When it is traversing via an interator and it encounters a ForwardingNode, it will jump to the next table and put the item at the same index.
- It will return this item and then move back to the initial table.

## Design of Randomised iterator

- State = FROM_RANDOM, FROM_ZERO
- We advance in the same manner as the current Traverser
