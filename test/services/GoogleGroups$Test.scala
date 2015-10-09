package services

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class GoogleGroups$Test extends FlatSpec with Matchers with ScalaFutures  {

  "isAuthorised" should "print out details" in {
    val f = GoogleGroups.isAuthorised("mark.butler@guardian.co.uk")
    f onSuccess {
        case Right(s) => s.foreach(println(_))
        case Left(error) => println(error)
    }
  }
}
