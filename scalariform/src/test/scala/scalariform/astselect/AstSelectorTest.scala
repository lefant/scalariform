package scalariform.astselect

import org.scalatest._
import org.scalatest.matchers._

import scalariform.utils.Range

// format: OFF
class AstSelectorTest extends FlatSpec with ShouldMatchers {

  // Legend:
  //
  // "|" denotes a zero-width selection before the position
  // "$" denotes a single-character-width selection at the position
  // "$$$$$$" denotes a multiple-character-width selection at the given positions

  " wibble  " ~
  "   |     " ~
  " $$$$$$  "

  " wibble  " ~
  " |       " ~
  " $$$$$$  "

  " wibble  " ~
  "      |  " ~
  " $$$$$$  "

  " wibble  " ~
  "       | " ~
  " $$$$$$  "

  " /* foo */ " ~
  "    $      " ~
  " $$$$$$$$$ "

  " /* foo */ /* bar */ " ~
  "     $               " ~
  " $$$$$$$$$           "

  " class A(n: Int) " ~ 
  "         $$$$$$  " ~ 
  " $$$$$$$$$$$$$$$ "

  " foo(42) " ~
  "     $$  " ~
  " $$$$$$$ "

  " object A { } " ~
  "           |  " ~
  " $$$$$$$$$$$$ "

  " private def foo = 42 " ~
  "           $$$$$$$$$  " ~
  " $$$$$$$$$$$$$$$$$$$$ "

  " if (a) b else c " ~
  "             $$$ "
  " $$$$$$$$$$$$$$$ "

  " aa(bb + cc, dd * ee) " ~
  "              $$$$$   " ~
  "             $$$$$$$  "

  " class A[B] " ~
  "         $  " ~
  " $$$$$$$$$$ "

  " new Wibble " ~
  "     $$$$$$ " ~
  " $$$$$$$$$$ "
  
  " new Wibble() " ~
  "         $$$  " ~
  " $$$$$$$$$$$$ "

  " a + b + c " ~
  "   $       " ~
  " $$$$$     " ~
  " $$$$$$$$$ " 

  " a + b + c " ~
  "      $$$  " ~
  " $$$$$$$$$ " 

  " x + y * z " ~
  "      $$$  " ~
  "     $$$$$ " ~
  " $$$$$$$$$ "

  " a :: b :: c :: Nil " ~
  "             $$     " ~
  "           $$$$$$$$ " ~
  "      $$$$$$$$$$$$$ " ~
  " $$$$$$$$$$$$$$$$$$ "

  " a :: b :: Nil ++ Nil " ~ 
  "                  $$$ " ~ 
  "           $$$$$$$$$$ " ~ 
  "      $$$$$$$$$$$$$$$ " ~ 
  " $$$$$$$$$$$$$$$$$$$$ "

  " a + b :: b + c :: Nil ++ Nil " ~ 
  "              $               " ~ 
  "          $$$$$               " ~ 
  "          $$$$$$$$$$$$$$$$$$$ " ~ 
  " $$$$$$$$$$$$$$$$$$$$$$$$$$$$ " 

  " i += 10 + 2 " ~
  " $           " ~
  " $$$$$$$$$$$ "

  " i += 10 + 2 " ~
  "      $$     " ~
  "      $$$$$$ " ~
  " $$$$$$$$$$$ "

  " 'a'.bar[X](foo).bizzle(a, b).baz.buzz[T].bozz(12)(15).foo _ " ~
  " $$$                                                         " ~
  " $$$$$$$$$$$$$$$                                             " ~
  " $$$$$$$$$$$$$$$$$$$$$$$$$$$$                                " ~
  " $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$                            " ~
  " $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$                    " ~
  " $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$       " ~
  " $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ " 

  " a.foo(b).bar(c) " ~
  " $               " ~
  " $$$$$$$$        " ~
  " $$$$$$$$$$$$$$$ "

  " def x = 42 " ~
  "         $$ " ~
  " $$$$$$$$$$ "
 
  " x: Int " ~
  " $      " ~
  " $$$$$$ "

  " x = a + b " ~
  " $         " ~
  " $$$$$$$$$ "

  " a match { case b => } " ~
  " $                     " ~
  " $$$$$$$$$$$$$$$$$$$$$ "

  " a match { case b => c } " ~
  "           $$$$$$$$$$$   " ~
  " $$$$$$$$$$$$$$$$$$$$$$$ "

  " (a, b) " ~
  "   $$$  " ~
  " $$$$$$ "

  " for { a <- b; c <- d } yield e " ~
  "       $$$$$$$                  " ~
  " $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ "

  " { case b ⇒ c } " ~
  "        $       " ~
  "   $$$$$$$$$$   "

  " for (a ← b) { c } " ~
  "               $   " ~
  " $$$$$$$$$$$$$$$$$ "

  " for (a <- b if c) {} " ~
  "                $     " ~ 
  "      $$$$$$$$$$$     "

  " def a = { b } " ~
  "           $   " ~
  " $$$$$$$$$$$$$ "

  " def a { b } " ~
  "         $   " ~
  " $$$$$$$$$$$ "

  " { case b :: c :: d => } " ~
  "                  $      " ~
  "             $$$$$$      " ~
  "        $$$$$$$$$$$      "

  if (false) { 
  }

  private def findSelectionRange(s: String): Range = { 
    val barLocation = s indexOf '|'
    if (barLocation >= 0)
      Range(barLocation, 0)
    else { 
      val firstDollarLocation = s indexOf '$'
      require(firstDollarLocation >= 0, "No selection marker: " + s)
      val dollars = s.drop(firstDollarLocation).takeWhile(_ == '$')
      Range(firstDollarLocation, dollars.length)
    }
  }

  implicit def stringToTestString(source: String): TestString = new TestString(source)
  class TestString(source: String) { 
    def ~(initialSelectionDiagram: String) = IntermediateTest(source, initialSelectionDiagram)
  }

  case class IntermediateTest(source: String, initialSelectionDiagram: String) { 
    def ~(finalSelectionDiagram: String): IntermediateTest = { 
       val initialSelection = findSelectionRange(initialSelectionDiagram)      
       val actualFinalSelection = new AstSelector(source).expandSelection(initialSelection) getOrElse initialSelection
       val expectedFinalSelection = findSelectionRange(finalSelectionDiagram)
       ("source\n>>>" + source + "<<<\n") should "expand\n>>>" + (initialSelectionDiagram + "<<<\n to \n>>>" + finalSelectionDiagram + "<<<\n") in {
         actualFinalSelection should equal (expectedFinalSelection)
       }
       IntermediateTest(source, initialSelectionDiagram = finalSelectionDiagram)
    }
  }
}
