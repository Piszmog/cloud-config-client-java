package io.github.piszmog.cloudconfig.template.impl;

import io.github.piszmog.cloudconfig.template.ConfigTemplate;
import io.pivotal.spring.cloud.config.client.ConfigClientOAuth2Properties;
import io.pivotal.spring.cloud.config.client.OAuth2AuthorizedClientHttpRequestInterceptor;
import jakarta.annotation.PostConstruct;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.client.RestTemplate;

/**
 * Config template used when OAuth2 authentication is required with the config server.
 * <p>
 * Created by Piszmog on 5/5/2018
 */
public class OAuth2ConfigTemplate extends ConfigTemplate {
    private final ConfigClientOAuth2Properties configClientOAuth2Properties;

    /**
     * Creates a config template using OAuth2 credentials.
     *
     * @param configClientProperties       the config server properties
     * @param configClientOAuth2Properties the OAuth2 resource details
     */
    public OAuth2ConfigTemplate(final ConfigClientProperties configClientProperties,
                                final ConfigClientOAuth2Properties configClientOAuth2Properties) {
        super(configClientProperties);
        this.configClientOAuth2Properties = configClientOAuth2Properties;
    }

    /**
     * Initializes the template with the OAuth2 credentials if the template is null.
     */
    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("cloud-config-client")
                .clientId(configClientOAuth2Properties.getClientId())
                .clientSecret(configClientOAuth2Properties.getClientSecret())
                .tokenUri(configClientOAuth2Properties.getAccessTokenUri())
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .build();
        restTemplate.getInterceptors().add(new OAuth2AuthorizedClientHttpRequestInterceptor(clientRegistration));
    }
}
