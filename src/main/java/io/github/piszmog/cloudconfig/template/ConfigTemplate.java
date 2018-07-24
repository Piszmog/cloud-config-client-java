package io.github.piszmog.cloudconfig.template;

import io.github.piszmog.cloudconfig.ConfigException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.cloud.config.client.ConfigClientProperties.TOKEN_HEADER;

/**
 * Template used for calling endpoints on the config server.
 * <p>
 * Created by Piszmog on 5/5/2018
 */
public abstract class ConfigTemplate
{
    // ============================================================
    // Class Attributes:
    // ============================================================

    protected final ConfigClientProperties configClientProperties;
    protected RestTemplate restTemplate;
    private final String configUri;

    // ============================================================
    // Public Methods:
    // ============================================================

    /**
     * creates a new template using the properties provided.
     *
     * @param configClientProperties the config server properties
     */
    public ConfigTemplate( final ConfigClientProperties configClientProperties )
    {
        this.configClientProperties = configClientProperties;
        configUri = configClientProperties.getRawUri();
    }

    // ============================================================
    // Public Methods:
    // ============================================================

    /**
     * Send the HTTP request to the Config Server and return the response from the Server.
     *
     * @param urlPath      the url path to perform the request at -- adds to the config server url
     * @param method       the operation to perform
     * @param requestBody  the request body to send in request
     * @param httpHeaders  http headers to send in request
     * @param classType    the class type to convert response to
     * @param urlVariables the URL path variables
     * @param <T>          the class type the response is converted to
     * @return The response entity from the HTTP call. Return null if an error occurred.
     * @throws ConfigException exception occurs when an error occurs when operating on the config server or when the
     *                         status code is not a 2xx
     */
    public <T> ResponseEntity<T> sendAndReceive( final String urlPath,
                                                 final HttpMethod method,
                                                 final Object requestBody,
                                                 final HttpHeaders httpHeaders,
                                                 final Class<T> classType,
                                                 final Object... urlVariables ) throws ConfigException
    {
        HttpEntity<Object> entity;
        HttpHeaders headers = addTokenHeader( httpHeaders );
        if ( requestBody == null )
        {
            entity = new HttpEntity<>( headers );
        }
        else
        {
            entity = new HttpEntity<>( requestBody, headers );
        }
        ResponseEntity<T> responseEntity;
        try
        {
            responseEntity = restTemplate.exchange( configUri + urlPath,
                    method,
                    entity,
                    classType,
                    urlVariables );
        }
        catch ( RestClientException e )
        {
            throw new ConfigException( "Failed to perform " + method.name() + " at " +
                    expandUrl( urlPath, urlVariables ) + " on the Config Server.", e );
        }
        if ( !responseEntity.getStatusCode().is2xxSuccessful() )
        {
            throw new ConfigException( "Failed to perform " + method.name() + " at " +
                    expandUrl( urlPath, urlVariables ) + " on the Config Server. " +
                    "Received Status " + responseEntity.getStatusCode() );
        }
        return responseEntity;
    }

    /**
     * Retrieves the name of the application.
     *
     * @return The name of the application.
     */
    public String getName()
    {
        return configClientProperties.getName();
    }

    /**
     * Retrieves the profile of the application.
     *
     * @return The profile.
     */
    public String getProfile()
    {
        return configClientProperties.getProfile();
    }

    /**
     * Retrieves the label being used by the config server.
     *
     * @return The label of the config server.
     */
    public String getLabel()
    {
        return configClientProperties.getLabel();
    }

    // ============================================================
    // Private Methods:
    // ============================================================

    private HttpHeaders addTokenHeader( final HttpHeaders httpHeaders )
    {
        HttpHeaders headers = httpHeaders;
        if ( headers == null )
        {
            headers = new HttpHeaders();
        }
        final String token = configClientProperties.getToken();
        //
        // Set the token header if need to authenticate
        //
        if ( StringUtils.isNotBlank( token ) )
        {
            headers.add( TOKEN_HEADER, token );
        }
        return headers;
    }

    private String expandUrl( final String url, final Object... urlVariables )
    {
        return UriComponentsBuilder.fromPath( url ).buildAndExpand( urlVariables ).toUriString();
    }
}
