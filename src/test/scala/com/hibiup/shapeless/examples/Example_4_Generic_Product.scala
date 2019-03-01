package com.hibiup.shapeless.examples

import org.scalatest.FlatSpec
import shapeless.{HList, ::, HNil}

object Example_4_Generic_Product {
    /** 定义 ADT */
    case class Employee(name: String, number: Int, manager: Boolean)
    case class IceCream(name: String, numCherries: Int, inCone: Boolean)

    /** Type class：分离 ADT 方法 */
    trait CsvEncoder[A] {
        def encode(value: A): List[String]
    }


    def instance[A](func: A => List[String]): CsvEncoder[A] = (value: A) => func(value)

    implicit val stringEncoder: CsvEncoder[String] = instance(str => List(str))
    implicit val intEncoder: CsvEncoder[Int] = instance(num => List(num.toString))
    implicit val booleanEncoder: CsvEncoder[Boolean] = instance(bool => List(if (bool) "yes" else "no"))
    implicit val hnilEncoder: CsvEncoder[HNil] = instance(hnil => Nil)

    // Combination
    implicit def hlistEncoder[H, T <: HList](implicit hEncoder: CsvEncoder[H],
                                             tEncoder: CsvEncoder[T]
                                            ): CsvEncoder[H :: T] = instance {
        case h :: t =>
            hEncoder.encode(h) ++ tEncoder.encode(t)
    }
}

class Example_4_Generic_Product extends FlatSpec {
    import Example_4_Generic_Product._

    "encode HList" should "" in {
        // 等价于： val reprEncoder = implicitly[CsvEncoder[String :: Int :: Boolean :: HNil]]
        val reprEncoder: CsvEncoder[String :: Int :: Boolean :: HNil] = implicitly

        println(
            reprEncoder.encode("abc" :: 123 :: true :: HNil)
        )
    }

    "use Generic" should "" in {
        import shapeless.Generic

        /** 生成 Generic 的 encoder:
          *
          * 技巧：因为 CsvEncoder 需要一个类型参数，这个参数值是 Generic[A].Repr。但是在声明函数时这个值不存在，因此将它定义为 R，
          *      但是这样在执行 enc.encode 的时候会报告 found: gen.Repr，而不是期待的 R，所以我们需要在声明参数 gen 的时候，将
          *      gen.Repr = R，如此一来 gen.Repr 就是 R，enc.encode 就能够正常执行了。
          *
          * */
        def genericEncoder[A, R]( gen: Generic[A] { type Repr = R },   // gen: Generic.Aux[A, R]
                                  enc: CsvEncoder[R]                   //        ^
                                ): CsvEncoder[A] = {                   //        |
            /** 上面的小技巧不仅晦涩而且导致 Intellij 产生误报，为此 Shapeless 提供了一个类型别名来取代它，定义如下：
              *
              * type Aux[A, R] = Generic[A] { type Repr = R }
              *
              * */
            instance(a => enc.encode(gen.to(a)))
        }

        /** 实例化 IceCream encoder */
        implicit val iceCreamEncoder = genericEncoder( Generic[IceCream],
                hlistEncoder(stringEncoder,
                    hlistEncoder(intEncoder,
                        hlistEncoder(booleanEncoder, hnilEncoder)))
        )

        // 测试
        def writeCsv[A](values: List[A])(implicit enc: CsvEncoder[A]): String = {
            values.map(value => enc.encode(value).mkString(",")).mkString("\n")
        }

        val iceCreams: List[IceCream] = List(
            IceCream("Sundae", 1, false),
            IceCream("Cornetto", 0, true),
            IceCream("Banana Split", 0, false)
        )

        println(writeCsv(iceCreams))
    }
}
