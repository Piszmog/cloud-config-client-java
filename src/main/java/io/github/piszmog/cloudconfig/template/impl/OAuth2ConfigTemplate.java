package io.github.piszmog.cloudconfig.template.impl;

import io.github.piszmog.cloudconfig.template.ConfigTemplate;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;

import javax.annotation.PostConstruct;

/**
 * Config template used when OAuth2 authentication is required with the config server.
 * <p>
 * Created by Piszmog on 5/5/2018
 */
public class OAuth2ConfigTemplate extends ConfigTemplate
{
    private final OAuth2ProtectedResourceDetails oAuth2ProtectedResourceDetails;

    /**
     * Creates a config template using OAuth2 credentials.
     *
     * @param configClientProperties         the config server properties
     * @param oAuth2ProtectedResourceDetails the OAuth2 resource details
     */
    public OAuth2ConfigTemplate( final ConfigClientProperties configClientProperties,
                                 final OAuth2ProtectedResourceDetails oAuth2ProtectedResourceDetails )
    {
        super( configClientProperties );
        this.oAuth2ProtectedResourceDetails = oAuth2ProtectedResourceDetails;
    }

    /**
     * Initializes the template with the OAuth2 credentials if the template is null.
     */
    @PostConstruct
    public void init()
    {
        restTemplate = new OAuth2RestTemplate( oAuth2ProtectedResourceDetails );
        restTemplate.setRequestFactory( super.createHttpClientFactory( DEFAULT_READ_TIMEOUT ) );
    }
}
