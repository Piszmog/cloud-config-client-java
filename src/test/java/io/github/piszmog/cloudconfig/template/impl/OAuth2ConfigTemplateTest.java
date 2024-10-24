package io.github.piszmog.cloudconfig.template.impl;

import io.pivotal.spring.cloud.config.client.ConfigClientOAuth2Properties;
import io.pivotal.spring.cloud.config.client.OAuth2AuthorizedClientHttpRequestInterceptor;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import static org.assertj.core.api.Assertions.assertThat;

public class OAuth2ConfigTemplateTest {
    @Test
    public void testInit() {
        final ConfigClientProperties properties = new ConfigClientProperties(new StandardEnvironment());
        final ConfigClientOAuth2Properties oauth2Properties = new ConfigClientOAuth2Properties();
        oauth2Properties.setClientId("clientId");
        oauth2Properties.setClientSecret("secret");
        oauth2Properties.setAccessTokenUri("http://localhost:888");
        final OAuth2ConfigTemplate template = new OAuth2ConfigTemplate(properties, oauth2Properties);

        template.init();
        assertThat(template).extracting("restTemplate")
                .extracting("interceptors")
                .asInstanceOf(InstanceOfAssertFactories.LIST)
                .hasSize(1)
                .first()
                .isInstanceOf(OAuth2AuthorizedClientHttpRequestInterceptor.class)
                .extracting("authorizedManager")
                .extracting("authorizedClientService")
                .extracting("clientRegistrationRepository")
                .extracting("registrations")
                .extracting("cloud-config-client")
                .hasFieldOrPropertyWithValue("registrationId", "cloud-config-client")
                .hasFieldOrPropertyWithValue("clientId", "clientId")
                .hasFieldOrPropertyWithValue("clientSecret", "secret")
                .hasFieldOrPropertyWithValue("clientName", "cloud-config-client")
                .hasFieldOrPropertyWithValue("authorizationGrantType", AuthorizationGrantType.CLIENT_CREDENTIALS);
    }
}
