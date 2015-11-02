package healthcheck

import com.amazonaws.services.sns.model.Topic
import org.scalatest.{Matchers, FlatSpec}

class SNSTest extends FlatSpec with Matchers {

  "retrieveAdminApiTopic" should "find a topic with the correct pattern" in {
    SNS.retrieveAdminApiTopic(
      List(
        new Topic().withTopicArn("arn:aws:sns:eu-west-1:1234:IdentityAdmin-CODE-topicSendEmailToIdentityDev-1234"),
        new Topic().withTopicArn("arn:aws:sns:eu-west-1:1234:Identity-DEV-topicSendEmailToIdentityDev-1234"),
        new Topic().withTopicArn("arn:aws:sns:eu-west-1:1234:IdentityAdmin-DEV-topicSendEmailToIdentityDev-1234")
      ), "IdentityAdmin-DEV-topicSendEmailToIdentityDev"
    ) should be(Some(new Topic().withTopicArn("arn:aws:sns:eu-west-1:1234:IdentityAdmin-DEV-topicSendEmailToIdentityDev-1234")))
  }
}

