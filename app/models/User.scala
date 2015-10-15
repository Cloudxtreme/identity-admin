package models

import org.joda.time.DateTime

case class User (
                id: String,
                registeredDate: DateTime,
                lastActivityDate: DateTime,
                emailValidated: Boolean,
                email: String,
                firstName: String,
                lastName: String,
                displayName: String,
                userName: String,
                vanityURL: String,
                receiveGNMMarketing: Boolean,
                receive3rdPartyMarketing: Boolean,
                gender: String,
                dob: DateTime,
                location: String,
                deliveryLine1: String,
                deliveryLine2: String,
                deliveryTown: String,
                deliveryCountyState: String,
                deliveryPostcodeZip: String,
                deliveryCountry: String,
                billingLine1: String,
                billingLine2: String,
                billingTown: String,
                billingCountyState: String,
                billingPostcodeZip: String,
                billingCountry: String,
                userGroups: String
                  )

object User {

  def mockData(): User = {

    val mockDate = new DateTime(2015, 4, 15, 16, 12, 0)
    val mockDOB = new DateTime(1990, 1, 10, 0, 0, 0)

    User("11111111",
        mockDate,
        mockDate,
        false,
        "email@email.com",
        "first",
        "last",
        "Ms First Last",
        "username",
        "vanity",
        true,
        false,
        "female",
        mockDOB,
        "London",
        "10 Road",
        "Lewisham",
        "London",
        "London",
        "SW12 4ER",
        "UK",
        "10 Road",
        "Lewisham",
        "London",
        "London",
        "SW12 4ER",
        "UK",
        "Teachers"
    )
  }
}