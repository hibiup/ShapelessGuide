package com.hibiup.shapeless.examples

import org.scalatest.FlatSpec
import shapeless._

class Example_1_HList extends FlatSpec{
    "Directly use HList as type" should "" in {

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

        /** Shapeless 提供了一个名为 Generic 的类型类，它允许我们在具体的 ADT 和它的范化表示之间来回切换。*/
        val iceCreamGen = Generic[IceCream]
        println(Typeable[iceCreamGen.Repr].describe)

        /** 用 iceCreamGen 将 case class 转换成 HList */
        val iceCream1 = IceCream("Sundae", 1, false)
        val genericIceCream1 = iceCreamGen.to(iceCream1)  // genericIceCream: String :: Int :: Boolean :: shapeless.HNil = Sundae :: 1 :: false :: HNil
        assert(genericIceCream1.isInstanceOf[HList])

        /** 也可以转换回来 */
        val iceCream2 = iceCreamGen.from(genericIceCream1)
        assert(iceCream1 === iceCream2)

        /** 直接使用 Generic[T] */
        val genericEmployee1 = Generic[Employee].to(Employee("Dave", 123, false ))  // genericEmployee: String :: Int :: Boolean :: shapeless.HNil = Dave:: 123 :: false :: HNil

        /** Generic Employee 和 IceCream 具有相同的类型特征（泛化）了 */
        assert(genericEmployee1.getClass === genericIceCream1.getClass)

        /** 甚至如果两个 Generic 具有相同的表达式（Generic Repr），我们可以将它们互相兑换！ */
        val genericEmployee2 = Generic[Employee].from(genericIceCream1)
        println(genericEmployee2)
        assert(genericEmployee2.isInstanceOf[Employee])    // 一个 IceCream 变成了 Employee!!
    }

    "Scala Tuple is a Product" should "be compatible to Shapeless Generic" in {
        /** 获得一个 Tuple */
        val t1 = ("Hello", 123, true)

        /** 定义一个具有相同元素结构的 Generic 实例 */
        val tupleGen = Generic[(String, Int, Boolean)]

        /** 泛化后得到 HList */
        val genericTuple = tupleGen.to(t1)
        assert(genericTuple === "Hello":: 123::true::HNil)

        /** 将泛化后的 Tuple 还原回来得到的依然是一个 Tuple */
        assert(tupleGen.from(genericTuple) === t1)
    }
}
