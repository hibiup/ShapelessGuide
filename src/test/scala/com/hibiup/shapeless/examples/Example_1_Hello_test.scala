package com.hibiup.shapeless.examples

import com.hibiup.shapeless.examples.Example_1_Hello.{Employee, IceCream}
import org.scalatest.FlatSpec

object Example_1_Hello {
    case class Employee(name: String, number: Int, manager: Boolean)
    case class IceCream(name: String, numCherries: Int, inCone: Boolean)

    import shapeless._
    def genericCsv(gen: String :: Int :: Boolean :: HNil): List[String] =
}

class Example_1_Hello extends FlatSpec{
    "Generic type" should "" in  {
        import shapeless._
        import Example_1_Hello._

        val genericEmployee = Generic[Employee].to(Employee("Dave", 123, false ))  // genericEmployee: String :: Int :: Boolean :: shapeless.HNil = Dave:: 123 :: false :: HNil
        val genericIceCream = Generic[IceCream].to(IceCream("Sundae", 1, false ))  // genericIceCream: String :: Int :: Boolean :: shapeless.HNil = Sundae :: 1 :: false :: HNil

        import shapeless.HList
        assert(genericEmployee.getClass === genericIceCream.getClass)
        assert(genericEmployee.isInstanceOf[HList])
    }
}
