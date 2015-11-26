package monitoring

import com.amazonaws.regions.ServiceAbbreviations
import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsyncClient
import com.amazonaws.services.cloudwatch.model.{Dimension, MetricDatum, PutMetricDataRequest}
import util.Logging
import scala.util.{Success, Failure, Try}

object CloudWatch extends Logging {
  def publishMetric(name : String, count: Double, extraDimensions: Dimension*) = {
    val metric =
      new MetricDatum()
        .withValue(count)
        .withMetricName(name)
        .withUnit("Count")
        .withDimensions((mandatoryDimensions ++ extraDimensions): _*)

    val request = new PutMetricDataRequest().
      withNamespace(application).withMetricData(metric)

    Try(cloudwatch.putMetricDataAsync(request)) match {
      case Success(_) => logger.debug(
        s"Published metric to CloudWatch: name=${name} value=${count}")
      case Failure(e) => logger.error(
        s"Could not publish metric to Cloudwatch: " +
          s"name=${name} value=${count} error=${e.getMessage}}")
    }
  }

  private val application : String = "identity-admin"
  private val stageDimension =
    new Dimension().withName("Stage").withValue(AwsConfig.stage)
  private val mandatoryDimensions:Seq[Dimension] = Seq(stageDimension)

  private val cloudwatch = {
    val client =
      new AmazonCloudWatchAsyncClient(AwsConfig.credentials)
    client.setEndpoint(
      AwsConfig.region.getServiceEndpoint(ServiceAbbreviations.CloudWatch))
    client
  }
}