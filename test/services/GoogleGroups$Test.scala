package services

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.time.{Seconds, Span, Millis}

class GoogleGroups$Test extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  "isAuthorised" should "print out details" in {
    val f = GoogleGroups.isAuthorised("mark.butler@guardian.co.uk")

    whenReady(f) {
      res => res should be(Right)
    }
  }

  /*
  {
      f onSuccess {
        case Right(s) => {
          println("Right")
          s.foreach(println(_))
        }
        case Left(error) => {
          println("Left")
          println(error)
        }
      }
      f onFailure {
        case _ => println("Failed")
      }
    }
   */
}
