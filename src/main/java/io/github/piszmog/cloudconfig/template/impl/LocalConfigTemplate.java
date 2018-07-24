package io.github.piszmog.cloudconfig.template.impl;

import io.github.piszmog.cloudconfig.template.ConfigTemplate;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.cloud.config.client.ConfigClientProperties.AUTHORIZATION;

/**
 * Config template used when the config server is a local application.
 * <p>
 * Created by Piszmog on 5/5/2018
 */
public class LocalConfigTemplate extends ConfigTemplate
{
    // ============================================================
    // Class Constants:
    // ============================================================

    private static final int DEFAULT_READ_TIMEOUT = ( 60 * 1000 * 3 ) + 5000;

    // ============================================================
    // Class Attributes:
    // ============================================================

    private final int readTimeout;

    // ============================================================
    // Constructors:
    // ============================================================

    /**
     * Creates a local config template using the default read timeout.
     *
     * @param configClientProperties the config server properties.
     */
    public LocalConfigTemplate( final ConfigClientProperties configClientProperties )
    {
        super( configClientProperties );
        this.readTimeout = DEFAULT_READ_TIMEOUT;
    }

    /**
     * Creates a local config template using the provided read timeout.
     *
     * @param configClientProperties the config server properties
     * @param readTimeout            the read timeout
     */
    public LocalConfigTemplate( final ConfigClientProperties configClientProperties, final int readTimeout )
    {
        super( configClientProperties );
        this.readTimeout = readTimeout;
    }

    // ============================================================
    // Initializer Methods:
    // ============================================================

    /**
     * Initializes the template with the config server properties.
     */
    @PostConstruct
    public void init()
    {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout( readTimeout );
        restTemplate = new RestTemplate();
        restTemplate.setRequestFactory( requestFactory );
        final Map<String, String> headers = new HashMap<>( configClientProperties.getHeaders() );
        headers.remove( AUTHORIZATION );
        if ( !headers.isEmpty() )
        {
            this.restTemplate.setInterceptors( Collections.singletonList( new ConfigServicePropertySourceLocator.GenericRequestHeaderInterceptor( headers ) ) );
        }
    }
}
