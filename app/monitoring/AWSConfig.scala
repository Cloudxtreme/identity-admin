package monitoring

import com.amazonaws.auth._
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.{Region, Regions}
import play.api.Play._

object AwsConfig {
  val stage = current.configuration.getString("identity-admin.stage").get
  val topicPattern = s"IdentityAdmin-$stage-topicSendEmailToIdentityDev"
  val region: Region = Region.getRegion(Regions.EU_WEST_1)

  val credentials: AWSCredentialsProvider = {
    val provider = new AWSCredentialsProviderChain(
      new EnvironmentVariableCredentialsProvider(),
      new SystemPropertiesCredentialsProvider(),
      new ProfileCredentialsProvider("identity"),
      new InstanceProfileCredentialsProvider
    )
    provider.getCredentials
    provider
  }

}
