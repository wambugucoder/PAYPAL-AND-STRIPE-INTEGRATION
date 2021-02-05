package com.gofundme.server.configs

import com.paypal.base.rest.APIContext
import com.paypal.base.rest.OAuthTokenCredential
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PaypalConfig {
    @Value("\${paypal.client.id}")
   lateinit var clientId:String

   @Value("\${paypal.mode}")
   lateinit var mode:String

   @Value("\${paypal.client.secret}")
   lateinit var clientSecret:String

    @Bean
    fun sdkConfig(): Map<String, String> {
        return hashMapOf(
            "mode" to mode,
        )

    }
    @Bean
    fun oauthTokenCredentials(): OAuthTokenCredential {

        return  OAuthTokenCredential(clientId,clientSecret,sdkConfig())
    }

    @Bean
    fun apiContext():APIContext{
        val context:APIContext = APIContext(oauthTokenCredentials().accessToken)
        context.configurationMap=sdkConfig()
        return context
    }
}