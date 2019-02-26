package com.hibiup.shapeless.examples

import org.scalatest.FlatSpec

class Example_1_HList extends FlatSpec{
    "Directly use HList as type" should "" in {
        import shapeless.{HList, ::, HNil}

        /** 类型定义一个 HList */
        val product1: String :: Int :: Boolean :: HNil = "Sunday" :: 1 :: false :: HNil
        println(product1)

        /** 类型推断一个 HList */
        val product2 = "Sunday" :: 1 :: false :: HNil
        assert(product2 === product1)

        /** HList 满足 match...case，并且也具有 unapply 能力。（Product 的特征之一） */
        product1 match {
            case (head::middle::tail) => println(s"Head: $head + Middle: $middle + Tail: ${tail}")
        }

        /** 前插操作 */
        val product3 = 42L :: product1
        assert(product3.head === 42L)
    }

    "Shapeless Generic type class allows us to switch back and forth between ADT and its generic representation" should "" in  {
        case class Employee(name: String, number: Int, manager: Boolean)
        case class IceCream(name: String, numCherries: Int, inCone: Boolean)

        import shapeless._
        /** Shapeless提供了一个名为Generic的类型类，它允许我们在具体的ADT和它的范化表示之间来回切换。*/
        println(Generic[IceCream])

        /** 将 case class 转换成 HList */
        val genericEmployee = Generic[Employee].to(Employee("Dave", 123, false ))  // genericEmployee: String :: Int :: Boolean :: shapeless.HNil = Dave:: 123 :: false :: HNil
        assert(genericEmployee.isInstanceOf[HList])

        val genericIceCream: HList = Generic[IceCream].to(IceCream("Sundae", 1, false ))  // genericIceCream: String :: Int :: Boolean :: shapeless.HNil = Sundae :: 1 :: false :: HNil
        assert(genericEmployee.getClass === genericIceCream.getClass)
    }
}
