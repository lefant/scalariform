package scalariform.formatter

import scalariform.parser._
import scalariform.formatter._
import scalariform.formatter.preferences._

class TemplateFormatterTest extends AbstractFormatterTest {
// format: OFF

  "case class A" ==> "case class A"

  """class A extends B {
    |  foo()
    |  bar()
    |}""" ==>
  """class A extends B {
    |  foo()
    |  bar()
    |}"""

  """class A {
    |val n: Int
    |}""" ==>
  """class A {
    |  val n: Int
    |}"""

  "class A" ==> "class A"
  "trait A" ==> "trait A"

  "class A private (val b: C)" ==> "class A private (val b: C)"
  "class A [B]" ==> "class A[B]"
  "class A[B]private(val c: D)" ==> "class A[B] private (val c: D)"
  "class A (val b: C) (val d: E) (implicit val f: G)" ==> "class A(val b: C)(val d: E)(implicit val f: G)"
  "class A (implicit val f: G)" ==> "class A(implicit val f: G)"

  "abstract class E [F]private(val h: I) (implicit j: K) extends{} with L(2) with M{}" ==>
    "abstract class E[F] private (val h: I)(implicit j: K) extends {} with L(2) with M {}"

  "class A{}" ==> "class A {}"

  "class A@Deprecated()private (val b: C)" ==> "class A @Deprecated() private (val b: C)"
  "class A@Annotation1()@Annotation2()(val b: C)" ==> "class A @Annotation1() @Annotation2() (val b: C)"
  "class A[B]@Annotation()private(val c: D)" ==> "class A[B] @Annotation() private (val c: D)"

  """@A@B(c = "d")abstract class E [F]@G()private(val h: I) (implicit j: K) extends{} with L(2) with M{}""" ==>
  """@A
    |@B(c = "d")
    |abstract class E[F] @G() private (val h: I)(implicit j: K) extends {} with L(2) with M {}"""

  """@A/*a*/@B
    |/*b*/class E""" =/=>
  """@A/*a*/
    |@B
    |/*b*/
    |class E""" because "of inconsistency between spacing between annotations and comments"

  """/*a*/@A/*b*/@B(c = "d")/*c*/abstract class/*d*/E/*e*/[F]/*f*/@G()/*g*/private/*h*/(val h: I)/*i*/(implicit j: K)/*j*/extends/*k*/{} with/*l*/L(2) with M/*m*/{}""" =/=>
  """/*a*/
    |@A/*b*/
    |@B(c = "d")/*c*/
    |abstract class/*d*/E/*e*/[F]/*f*/@G()/*g*/private/*h*/(val h: I)/*i*/(implicit j: K)/*j*/extends/*k*/{} with/*l*/L(2) with M/*m*/{}""" because "sort out what we want"

  """class A {
    |  
    |  class B
    | 
    |  protected def c
    |  
    |}""" ==>
  """class A {
    |
    |  class B
    |
    |  protected def c
    |
    |}"""

  """class A{
    |( 
    |null match {
    |case b => val c = {d: Int => 1}
    |1.toString
    |}
    |)
    |}""" =/=>
  """class A {
    |  ( 
    |  null match {
    |case b => val c = {d: Int => 1}
    |1.toString
    |}
    |)
    |}""" because "block indent after mid-expr indent, also multiline case indent"

  """class C1492 {
    |
    |  class X
    |
    |  def foo(x: X => X) {}
    |
    |  foo ( implicit x => implicitly[X] )
    |  foo { implicit x => implicitly[X] }
    |}""" ==>
  """class C1492 {
    |
    |  class X
    |
    |  def foo(x: X => X) {}
    |
    |  foo(implicit x => implicitly[X])
    |  foo { implicit x => implicitly[X] }
    |}"""

  """class A {
    | <b/>
    | 42
    |}""" ==>
  """class A {
    |  <b/>
    |  42
    |}"""

  """class A {
    |  val x = new {
    |  def foo = 3
    |  }
    |}""" ==>
  """class A {
    |  val x = new {
    |    def foo = 3
    |  }
    |}"""

  """class A(n: Int){}""" ==>
  """class A(n: Int) {}"""

  """class A {
    |println("B")
    |// Comment
    |}""" ==>
  """class A {
    |  println("B")
    |  // Comment
    |}"""

  """trait A {
    |this: B => 
    |val c
    |}""" ==>
    """trait A { this: B =>
    |  val c
    |}"""

  """trait A { 
    |this: B => 
    |println("foo")
    |}""" ==>
  """trait A { this: B =>
    |  println("foo")
    |}"""

  "object X0 { 0;  (a : Int, b : Int, c : Int) => println(List(a, b)) }" ==>
    "object X0 { 0; (a: Int, b: Int, c: Int) => println(List(a, b)) }" // Case for self type / anon function literal syntax disambig

  "@serializable class A" ==>
    """@serializable
    |class A"""

  """class A extends B[C] {
    |println("foo")
    |}""" ==>
  """class A extends B[C] {
    |  println("foo")
    |}"""

  "class A(private val b: C)" ==> "class A(private val b: C)"
  "class A(protected val b: C)" ==> "class A(protected val b: C)"
  "class A(override val b: C)" ==> "class A(override val b: C)"

  """class C[T <: A {val n: Int
    |val m :Int}]""" ==>
  """class C[T <: A {
    |  val n: Int
    |  val m: Int
    |}]"""

  """class C(n: { val n: Int
    |val m: Int} )""" ==>
  """class C(n: {
    |  val n: Int
    |  val m: Int
    |})"""

  """class C(@annotation(foo = {
    |1 + 2}) n: { val n: Int
    |val m: Int} = { val n: Int
    |val m: Int})""" ==>
  """class C(@annotation(foo = {
    |  1 + 2
    |}) n: {
    |  val n: Int
    |  val m: Int
    |} = {
    |  val n: Int
    |  val m: Int
    |})"""

  """class A(b: C)
    |(d: E) """ ==>
  """class A(b: C)(d: E)""" // Maybe handle multiple ParamClauses in future.

  """class A
    |{
    |println("b")
    |}""" ==>
  """class A {
    |  println("b")
    |}"""

  """class A extends B{
    |c
    |}""" ==>
  """class A extends B {
    |  c
    |}"""

  """class A extends B(() => {
    |  if (a)
    |    b
    |  else
    |    c
    |})""" ==>
    """class A extends B(() => {
    |  if (a)
    |    b
    |  else
    |    c
    |})"""

  "class A { b => println(c) }" ==> "class A { b => println(c) }"

  "class A { @B val c = 4 }" ==> "class A { @B val c = 4 }"

  "trait A[@B C]" ==> "trait A[@B C]"

  "trait T[@specialized -A, @specialized +B]" ==> "trait T[@specialized -A, @specialized +B]"

  """class A { self: Int =>
    |println("foo")
    |}""" ==>
  """class A { self: Int =>
    |  println("foo")
    |}"""

  "class A private[B](c: D)" ==> "class A private[B] (c: D)"

  "class A {\n  b()\n}" ==> "class A {\n  b()\n}"

  "class A {\r\n  b()\r\n}" ==> "class A {\r\n  b()\r\n}"

  """class A(
    |m: Int, 
    |n: { def open(): Unit
    |def close(): Unit}, 
    |o: Int)""" ==>
  """class A(
    |  m: Int,
    |  n: {
    |    def open(): Unit
    |    def close(): Unit
    |  },
    |  o: Int)"""

  """class A(
    |n: Int, m: {def foo(): Int
    |def bar(a: String): Int}, o: Int,
    |p: Int, 
    |m: {def foo(): Int
    |def bar(a: String): Int})""" ==>
  """class A(
    |  n: Int, m: {
    |    def foo(): Int
    |    def bar(a: String): Int
    |  }, o: Int,
    |  p: Int,
    |  m: {
    |    def foo(): Int
    |    def bar(a: String): Int
    |  })"""

  {
    implicit val formattingPreferences = FormattingPreferences.setPreference(AlignParameters, true)
  """class A(n: Int, 
    |z: { val m
    |val n },
    |m: Int)""" ==>
  """class A(n: Int,
    |        z: {
    |          val m
    |          val n
    |        },
    |        m: Int)"""
    
  """class A(m: Int, 
    |n: {
    |val x: String
    |val y : String
    |},
    | o: Int = {
    |  42
    |})""" ==>
  """class A(m: Int,
    |        n: {
    |          val x: String
    |          val y: String
    |        },
    |        o: Int = {
    |          42
    |        })"""

  """class A(n: {
    | def close(): Unit
    | def open(): Unit
    |}, 
    |m: Int)""" ==>
  """class A(n: {
    |          def close(): Unit
    |          def open(): Unit
    |        },
    |        m: Int)"""
   
  """class A(
    |implicit n: {
    |def x: Int
    |def y: Int
    |})""" ==>
  """class A(
    |  implicit n: {
    |    def x: Int
    |    def y: Int
    |  })"""
    
    
  """class A(n: {
    |def x: Int
    |})""" ==>
  """class A(n: {
    |          def x: Int
    |        })"""

  """class A(a: Int, 
    |b: Int)(c: { val d: Int
    |})""" ==>
  """class A(a: Int,
    |        b: Int)(c: {
    |                  val d: Int
    |                })"""   
    
  }
    
  """class A(a: Int, 
    |b: Int)(c: { val d: Int
    |})""" =/=>
  """class A(a: Int,
    |  b: Int)(c: {
    |  val d: Int
    |})""" because "need to thread formatter state between param clauses"

  {
    implicit val formattingPreferences = FormattingPreferences.setPreference(DoubleIndentClassDeclaration, true)
  """class Person(
    |  name: String,
    |  age: Int)
    |    extends Entity
    |    with Logging
    |    with Identifiable
    |    with Serializable""" ==>
  """class Person(
    |  name: String,
    |  age: Int)
    |    extends Entity
    |    with Logging
    |    with Identifiable
    |    with Serializable""" 

  """class Person(
    |    name: String,
    |    age: Int) {
    |  def firstMethod = 42
    |}""" ==>
  """class Person(
    |    name: String,
    |    age: Int) {
    |  def firstMethod = 42
    |}"""

  """class Person(name: String, age: Int, birthdate: Date, astrologicalSign: String, shoeSize: Int, favoriteColor: java.awt.Color) 
    |extends Entity
    |with Logging
    |with Identifiable
    |with Serializable {
    |def firstMethod = 42
    |}""" ==>
  """class Person(name: String, age: Int, birthdate: Date, astrologicalSign: String, shoeSize: Int, favoriteColor: java.awt.Color)
    |    extends Entity
    |    with Logging
    |    with Identifiable
    |    with Serializable {
    |  def firstMethod = 42
    |}"""

  """class Person(
    |name: String,
    |  age: Int) 
    |extends Entity  {
    |def method() = 42
    |}""" ==>
  """class Person(
    |  name: String,
    |  age: Int)
    |    extends Entity {
    |  def method() = 42
    |}"""

  """trait A 
    |extends B 
    |with C {
    |println("d")
    |}""" ==>
  """trait A
    |    extends B
    |    with C {
    |  println("d")
    |}"""

  }
    
  "trait Function1[@specialized(Int, Long, Double) -T1, @specialized(Unit, Int, Long, Double) +R] extends AnyRef" ==>
  "trait Function1[@specialized(Int, Long, Double) -T1, @specialized(Unit, Int, Long, Double) +R] extends AnyRef"
  
 """class C
    |extends {
    |val name = "Bob"
    |}""" ==>
  """class C
    |  extends {
    |    val name = "Bob"
    |  }"""

  
  // format: ON

  override val debug = false

  type Result = FullDefOrDcl

  def getParser(parser: ScalaCombinatorParser): ScalaCombinatorParser#Parser[Result] = parser.phrase(parser.nonLocalDefOrDcl)

  def format(formatter: ScalaFormatter, result: Result) = formatter.format(result)(FormatterState(indentLevel = 0))

}