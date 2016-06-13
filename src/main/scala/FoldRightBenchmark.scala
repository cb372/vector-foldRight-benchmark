package benchmark

import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
class FoldRightBenchmark {

  val vectorOfRandomInts = Vector.tabulate(2 ^ 20)(_ => util.Random.nextInt)
  val hugeVectorOfRandomInts = Vector.tabulate(2 ^ 30)(_ => util.Random.nextInt)

  val vectorOfRandomStrings = Vector.tabulate(2 ^ 20)(_ => util.Random.nextPrintableChar.toString)
  val hugeVectorOfRandomStrings = Vector.tabulate(2 ^ 30)(_ => util.Random.nextPrintableChar.toString)
  
  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def stdlibFoldRight_cheapOp(bh: Blackhole) = {
    bh.consume(vectorOfRandomInts.foldRight[Int](Int.MaxValue){case (x, acc) => acc min x})
  }
  
  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def huge_stdlibFoldRight_cheapOp(bh: Blackhole) = {
    bh.consume(hugeVectorOfRandomInts.foldRight[Int](Int.MaxValue){case (x, acc) => acc min x})
  }
  
  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def stdlibFoldRight_expensiveOp(bh: Blackhole) = {
    bh.consume(vectorOfRandomStrings.foldRight[String](""){case (x, acc) => x + acc})
  }
  
  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def huge_stdlibFoldRight_expensiveOp(bh: Blackhole) = {
    bh.consume(hugeVectorOfRandomStrings.foldRight[String](""){case (x, acc) => x + acc})
  }
  
  // Specialised for Int to avoid boxing
  def balancedFold_Int(vector: Vector[Int])(z: Int)(op: (Int, Int) => Int): Int = {
    if (vector.isEmpty) z
    else if (vector.size == 1) op(vector(0), z)
    else {
      val (left, right) = vector.splitAt(vector.size / 2)
      op(balancedFold_Int(left)(z)(op), balancedFold_Int(right)(z)(op))
    }
  }
  
  def balancedFold_String(vector: Vector[String])(z: String)(op: (String, String) => String): String = {
    if (vector.isEmpty) z
    else if (vector.size == 1) op(vector(0), z)
    else {
      val (left, right) = vector.splitAt(vector.size / 2)
      op(balancedFold_String(left)(z)(op), balancedFold_String(right)(z)(op))
    }
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def balancedFold_cheapOp(bh: Blackhole) = {
    bh.consume(balancedFold_Int(vectorOfRandomInts)(Int.MaxValue){case (x, acc) => acc min x})
  }
  
  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def huge_balancedFold_cheapOp(bh: Blackhole) = {
    bh.consume(balancedFold_Int(hugeVectorOfRandomInts)(Int.MaxValue){case (x, acc) => acc min x})
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def balancedFold_expensiveOp(bh: Blackhole) = {
    bh.consume(balancedFold_String(vectorOfRandomStrings)(""){case (x, acc) => x + acc})
  }
  
  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def huge_balancedFold_expensiveOp(bh: Blackhole) = {
    bh.consume(balancedFold_String(hugeVectorOfRandomStrings)(""){case (x, acc) => x + acc})
  }

  // Optimised version of balancedFold that re-uses the initial vector instead of calling splitAt
  def balancedFold_optimised_Int(vector: Vector[Int], start: Int, end: Int)(z: Int)(op: (Int, Int) => Int): Int = {
    if (start == end) z
    else if (end - start == 1) op(vector(start), z)
    else {
      val pivot = (start + end) / 2
      op(balancedFold_optimised_Int(vector, start, pivot)(z)(op), balancedFold_optimised_Int(vector, pivot, end)(z)(op))
    }
  }

  def balancedFold_optimised_String(vector: Vector[String], start: Int, end: Int)(z: String)(op: (String, String) => String): String = {
    if (start == end) z
    else if (end - start == 1) op(vector(start), z)
    else {
      val pivot = (start + end) / 2
      op(balancedFold_optimised_String(vector, start, pivot)(z)(op), balancedFold_optimised_String(vector, pivot, end)(z)(op))
    }
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def balancedFold_optimised_cheapOp(bh: Blackhole) = {
    bh.consume(balancedFold_optimised_Int(vectorOfRandomInts, 0, vectorOfRandomInts.size)(Int.MaxValue){case (x, acc) => acc min x})
  }
  
  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def huge_balancedFold_optimised_cheapOp(bh: Blackhole) = {
    bh.consume(balancedFold_optimised_Int(hugeVectorOfRandomInts, 0, hugeVectorOfRandomInts.size)(Int.MaxValue){case (x, acc) => acc min x})
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def balancedFold_optimised_expensiveOp(bh: Blackhole) = {
    bh.consume(balancedFold_optimised_String(vectorOfRandomStrings, 0, vectorOfRandomInts.size)(""){case (x, acc) => x + acc})
  }
  
  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  def huge_balancedFold_optimised_expensiveOp(bh: Blackhole) = {
    bh.consume(balancedFold_optimised_String(hugeVectorOfRandomStrings, 0, hugeVectorOfRandomInts.size)(""){case (x, acc) => x + acc})
  }

}
