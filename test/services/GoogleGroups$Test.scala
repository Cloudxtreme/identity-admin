package services

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class GoogleGroups$Test extends FlatSpec with Matchers {

  "credientials" should "load" in {
    val creds = GoogleGroups.credential()
    println(creds.getServiceAccountId)
  }

}
