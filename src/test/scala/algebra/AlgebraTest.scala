package algebra

import org.scalatest.{ShouldMatchers, FlatSpec}
import Expression._

class AlgebraTest extends FlatSpec with ShouldMatchers{

  val xSq = X * X
  val twoX = X * 2
  val xPlus5 = X + 5
  val xSqMinus2xPlus3 = (xSq + Neg(twoX)) + 3

  "Algebra" should "evaluate expressions correctly with eval" in {
    eval(X, 0) should be(0)
    eval(X, 5) should be(5)

    eval(twoX, 0) should be(0)
    eval(twoX, 5) should be(10)

    eval(xSq, 0) should be(0)
    eval(xSq, 5) should be(25)

    eval(xPlus5, 0) should be(5)
    eval(xPlus5, 5) should be(10)

    eval(xSqMinus2xPlus3, 0) should be(3)
    eval(xSqMinus2xPlus3, 5) should be(18)

    eval(X - 3, 5) should be(2)
    eval(X - 5, 5) should be(0)
    eval(X - 7, 5) should be(-2)
    eval(X - Neg(3), 5) should be(8)
    eval(twoX - (3 * xSq), 5) should be(-65)
  }

  it should "simplify expressions with clean" in {
    clean(2 * twoX) should equal(4 * X)
    clean(xSqMinus2xPlus3 * 0) should equal(Const(0))

    clean(Neg(twoX * Neg(1) * 3)) should be(6 * X)

    clean(twoX + twoX) should be(4 * X)
    clean(Neg(twoX) + twoX) should be(Const(0))
    clean(xPlus5 + Neg(xPlus5)) should be(Const(0))
    clean(Neg(xPlus5) + Neg(xPlus5)) should be(-2 * (5 + X))
  }
}
