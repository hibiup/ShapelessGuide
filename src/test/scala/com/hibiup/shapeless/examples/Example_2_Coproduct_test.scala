package com.hibiup.shapeless.examples

import org.scalatest.FlatSpec
import shapeless._

class Example_2_Coproduct_test extends FlatSpec{
    "Coproduct :+: option" should "" in {
        case class Red()
        case class Amber()
        case class Green()

        /** 一般而言，coproducts采用 A:+:B:+:C:+:CNil 的形式,其中 :+: 可以松散地解释为 Either（or）,也就是说这个 Light 可以是三色中的任意一色。 */
        type Light = Red :+: Amber :+: Green :+: CNil

        /** Inl 相当于 Left(Red()), 由于运算是从 Left 到 Right 嵌套执行的，因此第一个元素的赋值只需要一层嵌套. */
        val red: Light = Inl(Red())
        println(red)

        /** 计算始终以 Inl 开始. 然后按 Left -> Right 顺序得到下一个值. 因此取得最右边的 Green 相当于 Right(Right(Left(Green())))： */
        val green: Light = Inr(Inr(Inl(Green())))
        println(green)

        /**
          * 因为计算以嵌套方式执行,所以获得的 type 在赋值得时候也要注意值的计算顺序. 比如 Green 在最右边第三个元素,因此要赋予: Inr(Inr(Inl(Green())))
          * 以下都会得到类型错误:
          *
                val green: Light = Inr(Inl(Green()))        // 嵌套次数不对
                val green: Light = Inr(Inl(Inl(Green())))   // 计算顺序不对
                val green: Light = Inl(Inr(Inl(Green())))
                val green: Light = Inl(Inl(Inl(Green())))

          * 但是可以用括弧改变运算顺序,例如以下定义:
          * Light1 相当于 Red.:+:(Green.:+:(Amber)): [_,[Green,_]]
          * */
        val amber: Light = Inr(Inl(Amber()))    // 同理, 如果值因处于中间位, 要做偏移后再赋予.
        println(amber)

        /** 除非使用自动类型匹配, 但是匹配出的 amber 也就不是 Light 类型了, 所以不会出错. */
        val amber1 = Inr(Inl(Inl(Amber())))
        println(amber1)
    }

    "Use Generic with Coporduct" should "" in {
        import shapeless.Generic

        /** sealed trait 就是 Coproduct */
        sealed trait Shape
        /** ！！Shape 的 子类（case class）必须在对 Shape 泛化之前定义！！ */
        final case class Rectangle(width: Double, height: Double) extends Shape
        final case class Circle(radius: Double) extends Shape

        /** Shapeless 利用 Macro 在编译阶段找到所有子类，并依此编译出 Coproduct */
        val gen = Generic[Shape]       // Rectangle :+: Circle :+: CNil
        println(Typeable[gen.Repr].describe)

        val rec = gen.to(Rectangle(3.0, 4.0))
        println(rec)        // Inr(Inl(Rectangle(3.0,4.0)))  Inr 相当于 Right,但是由于 :+: 不能直接获得 Right, 因此要先获得 Inl(Rectangle(3.0,4.0)

        val circle = gen.to(Circle(1.0))
        println(circle)     // Inl(Circle(1.0))              Inl 相当于 Left
    }
}

