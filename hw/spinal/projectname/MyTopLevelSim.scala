package projectname
import spinal.core._
import spinal.core.sim._


object MyTopLevelSim extends App {
  SimConfig.withWave.compile(new Adder4Bit()).doSim { dut =>
    // Fork a process to generate the reset and the clock on the dut
    dut.clockDomain.forkStimulus(period = 10)

// 软件参考模型函数
  def softwareAdder(a: Int, b: Int, cin: Boolean): (Int, Boolean) = {
    val sum = a + b + (if (cin) 1 else 0)
    (sum & 0xF, sum > 15)
  }

    // 随机数生成器
    val random = new scala.util.Random(42)

    // 测试100次随机加法
    for (idx <- 0 to 99) {
      // 生成随机输入
      val a = random.nextInt(16)      // 0-15范围
      val b = random.nextInt(16)      // 0-15范围
      val cin = random.nextBoolean()  // 随机进位

      // 设置输入
      dut.io.a #= a
      dut.io.b #= b
      dut.io.cin #= cin

      // 等待时钟上升沿
      dut.clockDomain.waitRisingEdge()

      // 软件参考计算
      val (expectedSum, expectedCout) = softwareAdder(a, b, cin)

      // 断言检查
      assert(dut.io.sum.toInt == expectedSum, s"Test $idx: Sum mismatch. a=$a, b=$b, cin=$cin, Expected: $expectedSum, Got: ${dut.io.sum.toInt}")
      
      assert(dut.io.cout.toBoolean == expectedCout, s"Test $idx: Carry mismatch. a=$a, b=$b, cin=$cin, Expected: $expectedCout, Got: ${dut.io.cout.toBoolean}")

      // 可选：打印详细信息
      println(f"Test $idx: $a + $b (cin=$cin) = ${dut.io.sum.toInt} (cout=${dut.io.cout.toBoolean})")
    }

      // 额外的边界条件测试
    val boundaryTests = List(
      (0, 0, false),     // 全零
      (15, 0, false),    // 最大单一输入
      (15, 15, false),   // 两个最大输入
      (15, 15, true)     // 最大输入 + 进位
    )

    boundaryTests.zipWithIndex.foreach { case ((a, b, cin), idx) =>
      dut.io.a #= a
      dut.io.b #= b
      dut.io.cin #= cin

      dut.clockDomain.waitRisingEdge()

      val (expectedSum, expectedCout) = softwareAdder(a, b, cin)

      assert(dut.io.sum.toInt == expectedSum, s"Boundary Test $idx: Sum mismatch. a=$a, b=$b, cin=$cin, Expected: $expectedSum, Got: ${dut.io.sum.toInt}")
      
      assert(dut.io.cout.toBoolean == expectedCout, s"Boundary Test $idx: Carry mismatch. a=$a, b=$b, cin=$cin, Expected: $expectedCout, Got: ${dut.io.cout.toBoolean}")
    }
  }
}
