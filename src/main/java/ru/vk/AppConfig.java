package ru.vk;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {
    private static final Logger LOG = LoggerFactory.getLogger(AppConfig.class);

    @Bean
    public VkApiClient vkApiClient() {
        LOG.info("Creating the transport client");
        return new VkApiClient(new HttpTransportClient());
    }

    @Bean
    public UserAuthResponse userAuthResponse(
                                 @Value("${user.app-id}") int appId,
                                 @Value("${user.client-secret}") String clientSecret,
                                 @Value("${uri.redirect}") String redirectUri) {
        UserAuthResponse authResponse = null;
        try {
            authResponse = vkApiClient().oAuth()
                    .userAuthorizationCodeFlow(appId, clientSecret, redirectUri, getCode())
                    .execute();
        } catch (Exception exception) {
            LOG.warn("API Server error");
            throw new RuntimeException(exception);
        }
        LOG.info("Authorization Code Flow for userId: " + authResponse.getUserId() + " successfully done");
        return authResponse;
    }


    /**
     * For Authorization Code Flow you must have code
     * @return code for your authorization
     */
    private String getCode() {
        return "d09ebd918ac84ad75e";
    }


}
