package projectname
import spinal.core._
import spinal.core._
import spinal.core._



// 全加器模块
class FullAdder extends Component {
val io = new Bundle {
  val a = in Bool()     // 输入 A
  val b = in Bool()     // 输入 B
  val cin = in Bool()   // 前一级的进位
  val sum = out Bool()  // 和输出
  val cout = out Bool() // 进位输出
}

  // 逻辑表达式
  io.sum := io.a ^ io.b ^ io.cin
  io.cout := (io.a & io.b) | (io.cin & (io.a ^ io.b))
}

// 4位加法器模块
class Adder4Bit extends Component {
val io = new Bundle {
  val a = in UInt(4 bits)   // 4位输入 A
  val b = in UInt(4 bits)   // 4位输入 B
  val cin = in Bool()       // 初始进位
  val sum = out UInt(4 bits) // 4位和输出
  val cout = out Bool()     // 最终进位输出
}

  // 创建4个全加器
  val fas = Array.fill(4)(new FullAdder)

  // 连接进位
  for (i <- 0 until 3) {
    fas(i+1).io.cin := fas(i).io.cout
  }

  // 初始进位
  fas(0).io.cin := io.cin

  // 连接输入
  for (i <- 0 until 4) {
    fas(i).io.a := io.a(i)
    fas(i).io.b := io.b(i)
  }

  // 使用位拼接方式重构输出
  io.sum := U(
    fas(3).io.sum.asUInt ## 
    fas(2).io.sum.asUInt ## 
    fas(1).io.sum.asUInt ## 
    fas(0).io.sum.asUInt
  )

  io.cout := fas(3).io.cout
}

object MyTopLevelVerilog extends App {
  Config.spinal.generateVerilog(new Adder4Bit())
}