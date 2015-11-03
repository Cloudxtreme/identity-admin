package healthcheck

import com.amazonaws.regions.{Regions, Region}
import com.amazonaws.services.sns.AmazonSNSAsyncClient
import com.amazonaws.services.sns.model.{Topic, PublishRequest}
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import util.Logging
import scala.collection.JavaConversions._

object SNS extends Logging {

  private lazy val sns = {
    val client = new AmazonSNSAsyncClient(AWSConfig.credentials)
    client.setEndpoint(Region.getRegion(Regions.EU_WEST_1).getServiceEndpoint(com.amazonaws.regions.ServiceAbbreviations.SNS))
    client
  }
  private[healthcheck] def retrieveAdminApiTopic(topics: List[Topic], topicPattern: String): Option[Topic] = {
    topics.find { topic => topic.getTopicArn.contains(topicPattern) }
  }

  def notifyAdminApiUnhealthy(): Unit = {
    logger.info("Admin Api Unhealthy... About to send notification to SNS topic")
    val topics = sns.listTopics.getTopics.toList
    val topicPattern = AWSConfig.topicPattern
    val topic = retrieveAdminApiTopic(topics, topicPattern)

    topic.map( t => sns.publishAsync(new PublishRequest(t.getTopicArn, Messages("healthcheck.adminApiUnhealthy"))))
  }
}
