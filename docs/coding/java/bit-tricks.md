# Bit Tricks

## Restricting lookup to array of n

- To restrict an array lookup to the valid range [0, n] without conditionals, you can use bitwise masking if the array length is a power of two. This technique is common in performance-critical code (e.g., ring buffers).
- If your array length is n = 2^k, then index & (n - 1) will always yield a value in [0, n-1].
- For example, for an array of length 256:
- `lookupTbl[index & 0xFF] will always be in range.`

```java
// Option 01: with conditionals
// Explanation:
// - If index < n, result is index
// - If index >= n, result is n-1
// - Works for index >= 0
int safeIndex = Math.min(index, n - 1);

// Option 02: Still with conditionals
// Only works for index >= 0
int mask = -(index >= n ? 1 : 0); // mask is 0 if in range, -1 if out of range
int safeIndex = (index & ~mask) | ((n - 1) & mask);


// Option 03: Pure arithmetic (no conditionals, for positive indices):
int safeIndex = index + ((n - 1 - index) >> 31 & (n - 1 - index));
// If index < n, (n - 1 - index) is positive, so >> 31 is 0, so safeIndex = index.
// If index >= n, (n - 1 - index) is negative, so >> 31 is -1, so safeIndex = index + (n - 1 - index) = n - 1.
```
