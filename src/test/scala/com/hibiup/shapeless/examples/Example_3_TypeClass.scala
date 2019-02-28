package com.hibiup.shapeless.examples

import org.scalatest.FlatSpec

/** 定义 ADT */
case class Employee(name: String, number: Int, manager: Boolean)
case class IceCream(name: String, numCherries: Int, inCone: Boolean)

/** Type class：分离 ADT 方法 */
trait CsvEncoder[A] {
    def encode(value: A): List[String]
}

/** （p21） 传统的实现 type class 的方式 */
object Traditional_Style {
    /** 实现 type class */
    implicit val employeeEncoder: CsvEncoder[Employee] =
        (e: Employee) => List(
            e.name,
            e.number.toString,
            if (e.manager) "yes" else "no"
        )

    implicit val iceCreamEncoder: CsvEncoder[IceCream] =
        (i: IceCream) => List(
            i.name,
            i.numCherries.toString,
            if (i.inCone) "yes" else "no"
        )
}

/** (p25) Shapeless 提供的标准解决方案是定义一个伴随类来包括这些工作，并且规定这个伴随类必须实现 Summoner
  * 和 instance 这两个方法。 其实和传统方法并没有什么区别，只是多了两个接口。*/
object Commonly_Idiomatic_Style_Companion_Object {
    // "Summoner" method
    def apply[A](implicit enc: CsvEncoder[A]): CsvEncoder[A] = enc

    // "Constructor" method
    def instance[A](func: A => List[String]): CsvEncoder[A] = (value: A) => func(value)

    // 调用 instance，传入函数体，实现 type class 实例。
    implicit val employeeEncoder: CsvEncoder[Employee] = instance {
        (e: Employee) =>
            List(
                e.name,
                e.number.toString,
                if (e.manager) "yes" else "no"
            )
    }

    implicit val iceCreamEncoder: CsvEncoder[IceCream] = instance {
        (i: IceCream) =>
            List(
                i.name,
                i.numCherries.toString,
                if (i.inCone) "yes" else "no"
            )
    }
}

class Example_3_TypeClass extends FlatSpec {
    "General style" should "" in {
        import Traditional_Style._

        /** */
        def writeCsv[A](values: List[A])(implicit enc: CsvEncoder[A]): String =
            values.map(value => enc.encode(value).mkString(",")).mkString("\n")

        // 测试_1
        val employees: List[Employee] = List(
            Employee("Bill", 1, true),
            Employee("Peter", 2, false),
            Employee("Milton", 3, false)
        )
        println(writeCsv(employees))

        // 测试_2
        val iceCreams: List[IceCream] = List(
            IceCream("Sundae", 1, false),
            IceCream("Cornetto", 0, true),
            IceCream("Banana Split", 0, false)
        )
        println(writeCsv(iceCreams))
    }

    "Shapeless Style" should "" in {
        /** 引入 Object 中定义的 Encoders */
        import Commonly_Idiomatic_Style_Companion_Object.{employeeEncoder, iceCreamEncoder}

        def writeCsv[A](values: List[A])(implicit e: CsvEncoder[A]): String = {
            /** 通过“召唤术”将它设置为当前环境中的 materializer （在本例中，这条其实是多余的）*/
            val env = Commonly_Idiomatic_Style_Companion_Object[A]

            /** 使用 materializer 对数据 encode */
            values.map(value => env.encode(value).mkString(",")).mkString("\n")
        }

        def writeCsv_CB[A : CsvEncoder](values: List[A]): String = {
            /** 也可以利用 shapleless 提供的 the 函数，implicitly 召唤所需实例 */
            import shapeless._
            val env = the[CsvEncoder[A]]   // 等价于 implicitly

            /** 使用 materializer 对数据 encode */
            values.map(value => env.encode(value).mkString(",")).mkString("\n")
        }

        // 测试_1
        val employees: List[Employee] = List(
            Employee("Bill", 1, true),
            Employee("Peter", 2, false),
            Employee("Milton", 3, false)
        )
        println(writeCsv(employees))

        // 测试_2
        val iceCreams: List[IceCream] = List(
            IceCream("Sundae", 1, false),
            IceCream("Cornetto", 0, true),
            IceCream("Banana Split", 0, false)
        )
        println(writeCsv_CB(iceCreams))
    }
}


