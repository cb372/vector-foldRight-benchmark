Benchmarking balanced fold vs foldRight on Vector.

## Variables

* Size of vector: 2^20 vs 2^30 elements
* Cost of the fold operation: finding the minimum of a Vector of ints (cheap) vs concatenating a Vector of strings (expensive)
* Implementation: the standard collection lib's `foldRight`, vs 2 implementations of `balancedFold` (a naive one and a more performant one)

## Results

```
[info] Benchmark                                                   Mode  Cnt     Score      Error  Units

// 2^20 elements, op = integer min
[info] FoldRightBenchmark.stdlibFoldRight_cheapOp                  avgt   20   397.995 ±   18.292  ns/op
[info] FoldRightBenchmark.balancedFold_cheapOp                     avgt   20  1589.875 ±  122.727  ns/op
[info] FoldRightBenchmark.balancedFold_optimised_cheapOp           avgt   20   181.784 ±   30.200  ns/op

// 2^30 elements, op = integer min
[info] FoldRightBenchmark.huge_stdlibFoldRight_cheapOp             avgt   20   602.981 ±   88.752  ns/op
[info] FoldRightBenchmark.huge_balancedFold_cheapOp                avgt   20  1959.538 ±   76.833  ns/op
[info] FoldRightBenchmark.huge_balancedFold_optimised_cheapOp      avgt   20   258.345 ±   69.954  ns/op

// 2^20 elements, op = string concatenation
[info] FoldRightBenchmark.stdlibFoldRight_expensiveOp              avgt   20  1175.649 ±  106.293  ns/op
[info] FoldRightBenchmark.balancedFold_expensiveOp                 avgt   20  4130.584 ±  368.484  ns/op
[info] FoldRightBenchmark.balancedFold_optimised_expensiveOp       avgt   20  2140.168 ±   85.843  ns/op

// 2^30 elements, op = string concatenation
[info] FoldRightBenchmark.huge_stdlibFoldRight_expensiveOp         avgt   20  1494.633 ±   20.967  ns/op
[info] FoldRightBenchmark.huge_balancedFold_expensiveOp            avgt   20  6322.657 ± 4191.283  ns/op
[info] FoldRightBenchmark.huge_balancedFold_optimised_expensiveOp  avgt   20  3707.640 ±  658.963  ns/op
```

And the winner is... inconclusive.
