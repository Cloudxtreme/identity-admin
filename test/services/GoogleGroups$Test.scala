package services

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class GoogleGroups$Test extends FlatSpec with Matchers {

  "isAuthorised" should "print out details" in {
    GoogleGroups.isAuthorised("mark.butler@guardian.co.uk")
  }

}
