package io.github.piszmog.cloudconfig.template.impl;

import io.github.piszmog.cloudconfig.template.ConfigTemplate;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

/**
 * Config template used when OAuth2 authentication is required with the config server.
 * <p>
 * Created by Piszmog on 5/5/2018
 */
public class OAuth2ConfigTemplate extends ConfigTemplate
{
    // ============================================================
    // Class Attributes:
    // ============================================================

    private final OAuth2ProtectedResourceDetails oAuth2ProtectedResourceDetails;

    // ============================================================
    // Constructors:
    // ============================================================

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
     * Creates a config template using the provided rest template.
     * <pre>
     *     The RestTemplate is usually built using,
     *     <code>restTemplate = new OAuth2RestTemplate( oAuth2ProtectedResourceDetails );</code>
     * </pre>
     * <p>
     * This constructor is primary used for unit testing.
     *
     * @param configClientProperties the config server properties
     * @param restTemplate           the rest temp[late to use when calling the config server
     */
    public OAuth2ConfigTemplate( final ConfigClientProperties configClientProperties,
                                 final RestTemplate restTemplate )
    {
        super( configClientProperties );
        oAuth2ProtectedResourceDetails = null;
        this.restTemplate = restTemplate;
    }

    // ============================================================
    // Initializer Methods:
    // ============================================================

    /**
     * Initializes the template with the OAuth2 credentials if the template is null.
     */
    @PostConstruct
    public void init()
    {
        if ( restTemplate == null )
        {
            restTemplate = new OAuth2RestTemplate( oAuth2ProtectedResourceDetails );
        }
    }
}
