package healthcheck

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.auth._
import play.api.Play._

object AWSConfig {
  val stage = current.configuration.getString("identity-admin.stage").get
  val topicPattern = s"IdentityAdmin-$stage-topicSendEmailToIdentityDev"

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
