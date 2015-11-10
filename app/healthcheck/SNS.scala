
package healthcheck

import javax.inject.Inject

import com.amazonaws.regions.{Regions, Region}
import com.amazonaws.services.sns.AmazonSNSAsyncClient
import com.amazonaws.services.sns.model.{Topic, PublishRequest}
import com.google.inject.ImplementedBy
import play.api.Play.current
import util.Logging
import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}

case class SNSError(message: String)

@ImplementedBy(classOf[SNSClient])
trait NotificationClient {
  val topicPattern: String

  private[healthcheck] def listTopics: Either[SNSError, List[Topic]]

  private[healthcheck] def publishAsync(topic: Topic): Either[SNSError, Topic]

  private[healthcheck] def findTopicARN(topics: List[Topic], topicPattern: String): Either[SNSError, Topic] = {
    topics.find { topic => topic.getTopicArn.contains(topicPattern) } match {
      case Some(t) => Right(t)
      case None => Left(SNSError(s"Could not find SNS topic with ARN pattern: $topicPattern"))
    }
  }
}

class SNSClient extends NotificationClient with Logging {
  override val topicPattern = AWSConfig.topicPattern

  private val sns = {
    logger.info("Setting up SNS client")
    val client = new AmazonSNSAsyncClient(AWSConfig.credentials)
    client.setEndpoint(Region.getRegion(Regions.EU_WEST_1).getServiceEndpoint(com.amazonaws.regions.ServiceAbbreviations.SNS))
    client
  }

  override private[healthcheck] def listTopics: Either[SNSError, List[Topic]] = {
    Try(sns.listTopics) match {
      case Success(list) => Right(list.getTopics.toList)
      case Failure(e) => Left(SNSError(s"Could not list SNS topics: ${e.getMessage}"))
    }
  }

  override private[healthcheck] def publishAsync(topic: Topic): Either[SNSError, Topic] = {
    logger.error("Admin Api Unhealthy... About to send notification to SNS topic")
    val topicMessage = "The admin app just reported that the Admin API service is unavailable. Please check the logs for more details"
    Try(sns.publishAsync(new PublishRequest(topic.getTopicArn, topicMessage))) match {
      case Success(_) => Right(topic)
      case Failure(e) => Left(SNSError(s"Could not publish notification to SNS topic: ${e.getMessage}"))
    }
  }
}

class SNS @Inject() (notificationClient: NotificationClient) extends Logging {
  def notifyAdminApiUnhealthy(): Unit = {
    val publishedTopic = for {
      topics <- notificationClient.listTopics.right
      topic <- notificationClient.findTopicARN(topics, notificationClient.topicPattern).right
    } yield notificationClient.publishAsync(topic)

    publishedTopic match {
      case Right(t) =>
        logger.error("Published notification to SNS topic {}", t)
      case Left(error) =>
        logger.error(error.message)
    }
  }
}
