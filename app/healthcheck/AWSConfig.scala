package healthcheck

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.auth._
import play.api.Play._

object AWSConfig {
  lazy val stage = current.configuration.getString("identity-admin.stage").get
  lazy val topicPattern = s"IdentityAdmin-$stage-topicSendEmailToIdentityDev"

  lazy val credentials: AWSCredentialsProvider = {
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
