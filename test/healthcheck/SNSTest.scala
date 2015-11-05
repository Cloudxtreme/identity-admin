package healthcheck

import com.amazonaws.services.sns.model.Topic
import org.scalatest.mock.MockitoSugar
import org.scalatest.{OneInstancePerTest, Matchers, FlatSpec}
import org.mockito.Matchers._
import org.mockito.Mockito._

class SNSTest extends FlatSpec with Matchers with MockitoSugar with OneInstancePerTest {
  val mockSNSClient = mock[NotificationClient]
  val sns = new SNS(mockSNSClient)

  val pattern = "IdentityAdmin-DEV-topicSendEmailToIdentityDev"
  val topicARN = "arn:aws:sns:eu-west-1:1234:IdentityAdmin-DEV-topicSendEmailToIdentityDev-1234"
  val validTopic = new Topic().withTopicArn(topicARN)

  val topics = List(
    new Topic().withTopicArn("arn:aws:sns:eu-west-1:1234:IdentityAdmin-CODE-topicSendEmailToIdentityDev-1234"),
    new Topic().withTopicArn("arn:aws:sns:eu-west-1:1234:Identity-DEV-topicSendEmailToIdentityDev-1234"),
    new Topic().withTopicArn("arn:aws:sns:eu-west-1:1234:IdentityAdmin-DEV-topicSendEmailToIdentityDev-1234")
  )

  "retrieveAdminApiTopic" should "find a topic with the correct pattern" in {
    class FakeSNSClient extends NotificationClient {
      override val topicPattern: String = ""
      override private[healthcheck] def listTopics: Either[SNSError, List[Topic]] = ???
      override private[healthcheck] def publishAsync(topic: Topic): Either[SNSError, Topic] = ???
    }

    val fakeSNSClient = new FakeSNSClient
    fakeSNSClient.findTopicARN(topics, pattern) should be(Right(validTopic))
  }

  "notifyAdminApiUnhealthy" should "send notification to a topic" in {
    when(mockSNSClient.listTopics).thenReturn(Right(topics))
    when(mockSNSClient.findTopicARN(anyObject(), anyString)).thenReturn(Right(validTopic))
    sns.notifyAdminApiUnhealthy()
    verify(mockSNSClient).publishAsync(validTopic)
  }

  it should "not send a notification if topics cannot be retrieved" in {
    when(mockSNSClient.listTopics).thenReturn(Left(SNSError("Did not work")))
    sns.notifyAdminApiUnhealthy()
    verify(mockSNSClient, times(0)).publishAsync(validTopic)
  }

  it should "not send a notification if a topic ARN cannot be found" in {
    when(mockSNSClient.listTopics).thenReturn(Right(topics))
    when(mockSNSClient.findTopicARN(anyObject(), anyString)).thenReturn(Left(SNSError("Did not work")))
    sns.notifyAdminApiUnhealthy()
    verify(mockSNSClient, times(0)).publishAsync(validTopic)
  }
}

