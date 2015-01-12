package algebra

sealed trait Expression {
  def +(e: Expression): Expression = Expression.add(this, e)
  def *(e: Expression): Expression = Expression.multi(this, e)

  def -(e: Expression): Expression = Expression.sub(this, e)
}

case object X extends Expression
case class Const(c: Int) extends Expression
case class Neg(e: Expression) extends Expression

sealed trait BinaryExpression extends Expression
private[this] case class Add(left: Expression, right: Expression) extends BinaryExpression
private[this] case class Multi(left: Expression, right: Expression) extends BinaryExpression

private[this] case class Sub(left: Expression, right: Expression) extends BinaryExpression

object Expression{

  implicit def int2Const(i: Int): Const = Const(i)

  def add(left: Expression, right: Expression): Expression = binary(Add)(left, right)
  def multi(left: Expression, right: Expression): Expression = binary(Multi)(left, right)

  def sub(left: Expression, right: Expression): Expression = Sub(left, right)

  protected def binary[T <: Expression](f: (Expression, Expression) => T)
            (left: Expression, right: Expression): T = right match {
    case Const(_) => f(right, left)
    case _ => f(left, right)
  }

  def eval(exp: Expression, x: Int): Int = exp match {
    case X => x
    case Const(i) => i
    case Add(l, r) => eval(l, x) + eval(r, x)
    case Sub(l, r) => eval(l, x) - eval(r, x)
    case Multi(l, r) => eval(l, x) * eval(r, x)
    case Neg(e) => - eval(e, x)
  }

  def clean(exp: Expression): Expression = clean0(X, exp)._2

  private def clean0(old: Expression, now: Expression): (Expression, Expression) = now match {
    case _ if old == now => (now, now)
    case X | Const(_) => (now, now)
    case Neg(ex) => clean0(now, cleanNeg(ex))
    case Add(l, r) => clean0(now, cleanAdd(l, r))
    case Multi(l, r) => clean0(now, cleanMulti(l, r))
  }

  private def cleanNeg(e: Expression) = clean(e) match {
    case Neg(ex) => ex
    case Const(i) => Const(-i)
    case Multi(Const(i), exp) => Multi(Const(-i), exp)
    case ex => Neg(ex)
  }

  private def cleanAdd(l: Expression, r: Expression): Expression = {
    (clean(l), clean(r)) match {
      case (Const(0), ex) => ex
      case (Const(i), Const(j)) => i + j
      case (Neg(cl), cr) if cl == cr => Const(0)
      case (cl, Neg(cr)) if cl == cr => Const(0)
      case (Neg(cl), Neg(cr)) => cleanNeg(add(cl, cr))
      case (Multi(Const(i), lExp), Multi(Const(j), rExp)) if lExp == rExp => Multi(i+j, lExp)
      case (cleanL, cleanR) if cleanL == cleanR => binary(m)(2, cleanR)
      case (cleanL, cleanR) => add(cleanL, cleanR)
    }
  }

  def cleanMulti(l: Expression, r: Expression): Expression = binary(m)(clean(l), clean(r))

  private def m(l: Expression, r: Expression): Expression = (l, r) match {
    case (Neg(cl), cr) => Neg(m(cl, cr))
    case (cl, Neg(cr)) => Neg(binary(Multi)(cl, cr))
    case (Neg(cl), Neg(cr)) => binary(m)(cl, cr)
    case (Const(i), ex) => cleanMultiConst(i, ex)
    case (cl, cr) => Multi(cl, cr)
  }

  private def cleanMultiConst(i: Int, ex: Expression): Expression = (i, ex) match {
    case (0, _) => 0
    case (1, exp) => exp
    case (_, Const(j)) => i * j
    case (_, Multi(Const(j), exp)) => Multi(i * j, exp)
    case _ => Multi(Const(i), ex)
  }
}