# Generics/Arrays wildcard types

## Arrays

Arrays in java are covariant.

Means:

An Array of type T[]

1. Arrays can contain T and its subtypes
2. An array of type T[] is a subtype of type S[], if S is a subtype of T

```java
@Test
  void arrays() {
    final int size = 5;

    final Number[] numbers = new Number[size];
    final Number[] numbers2 = new Integer[size];

    // Invalid as numbers2 is actually of type Integer[]
    numbers2[0] = 1.23;
  }
```

## Generics

- Generics are invariant due to type erasure at compile time
- List<CharSequence> becomes simply List

```java
final ArrayList<Integer> intArrList = new ArrayList<>();
// INVALID final ArrayList<Number> numArrList = intArrList;
final ArrayList<Integer> anotherIntArrList = intArrList;
```

With wildcards we can make generics covariant or contravariant
PECS = Producer extends, Consumer super

Covariant are read only, PRODUCER extends
Covariant <? extends T> can contains anything that extends from T

Contravariant are write only, CONSUMER super
Contravariant <? super T> can contain anything that is T or its super types

You can force conversion between them by doing

```java
   void generics_02_covariance() {
    final ArrayList<Integer> intArrList = new ArrayList<>();

    // Any type that is Number or that extends Number
    final ArrayList<? extends Number> numArrList = intArrList;
    // Allowed for reading, of type Number
    final Number number = numArrList.get(0);

    // DENIED numArrList.add(123);
    final Number n = Integer.valueOf(123);
    // DENIED, compile error numArrList.add(n);
  }

  @Test
  void generics_03_contravariance() {
    final ArrayList<Integer> intArrList = new ArrayList<>();

    // Any type that is Number or that extends Number
    final ArrayList<? super Number> numArrList = new ArrayList<>();
    // DENIED for reading final Number number = numArrList.get(0);

    numArrList.add(123);
    numArrList.add(1.23);
    final Number n = Integer.valueOf(123);
    numArrList.add(n);

    final ArrayList<? extends Number> numRead = (ArrayList<? extends Number>) numArrList;
    for (int i = 0; i < numRead.size(); i++) {
      System.out.println(i + " -> " + numRead.get(i));
    }
  }
```
