package io.github.piszmog.cloudconfig.template;

import io.github.piszmog.cloudconfig.ConfigException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigClientStateHolder;
import org.springframework.http.*;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.cloud.config.client.ConfigClientProperties.*;

/**
 * Template used for calling endpoints on the config server.
 * <p>
 * Created by Piszmog on 5/5/2018
 */
public abstract class ConfigTemplate
{
    // ============================================================
    // Class Constants:
    // ============================================================

    private static final String HEADER_AUTHORIZATION = "Authorization";

    // ============================================================
    // Class Attributes:
    // ============================================================

    protected final ConfigClientProperties configClientProperties;
    protected RestTemplate restTemplate;

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
    }

    // ============================================================
    // Public Methods:
    // ============================================================

    /**
     * Send the HTTP request to the Config Server and return the response from the Server.
     *
     * @param <T>          the class type the response is converted to
     * @param method       the operation to perform
     * @param urlPath      the url path to perform the request at -- adds to the config server url
     * @param requestBody  the request body to send in request
     * @param httpHeaders  http headers to send in request
     * @param classType    the class type to convert response to
     * @param urlVariables the URL path variables
     * @return The response entity from the HTTP call. Return null if an error occurred.
     * @throws ConfigException exception occurs when an error occurs when operating on the config server or when the
     *                         status code is not a 2xx
     */
    public <T> ResponseEntity<T> sendAndReceive( final HttpMethod method,
                                                 final String urlPath,
                                                 final Object requestBody,
                                                 final HttpHeaders httpHeaders,
                                                 final Class<T> classType,
                                                 final Object... urlVariables ) throws ConfigException
    {
        final String[] configUris = configClientProperties.getUri();
        final int numberOfUris = configUris.length;
        for ( int i = 0; i < numberOfUris; i++ )
        {
            Credentials credentials = configClientProperties.getCredentials( i );
            final ResponseEntity<T> responseEntity = sendAndReceiveToConfigServer( method,
                    urlPath,
                    urlVariables,
                    httpHeaders,
                    requestBody,
                    credentials,
                    classType );
            if ( responseEntity != null )
            {
                return responseEntity;
            }
        }
        return null;
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

    private <T> ResponseEntity<T> sendAndReceiveToConfigServer( final HttpMethod method,
                                                                final String urlPath,
                                                                final Object[] urlVariables,
                                                                final HttpHeaders httpHeaders,
                                                                final Object requestBody,
                                                                final Credentials credentials,
                                                                final Class<T> classType ) throws ConfigException
    {
        String configUri = credentials.getUri();
        String username = credentials.getUsername();
        String password = credentials.getPassword();
        HttpEntity<Object> entity;
        HttpHeaders headers = addSecurityHeaders( httpHeaders, username, password );
        addStateHeader( headers );
        if ( requestBody == null )
        {
            entity = new HttpEntity<>( headers );
        }
        else
        {
            entity = new HttpEntity<>( requestBody, headers );
        }
        final ResponseEntity<T> responseEntity = sendAndReceive( method, urlPath, urlVariables, entity, configUri, classType );
        if ( responseEntity != null )
        {
            return responseEntity;
        }
        return null;
    }

    private void addStateHeader( final HttpHeaders headers )
    {
        final String state = ConfigClientStateHolder.getState();
        if ( StringUtils.isNotBlank( state ) && configClientProperties.isSendState() )
        {
            headers.add( STATE_HEADER, state );
        }
    }

    private <T> ResponseEntity<T> sendAndReceive( final HttpMethod method,
                                                  final String urlPath,
                                                  final Object[] urlVariables,
                                                  final HttpEntity<Object> entity,
                                                  final String configUri,
                                                  final Class<T> classType ) throws ConfigException
    {
        ResponseEntity<T> responseEntity = null;
        try
        {
            responseEntity = restTemplate.exchange( configUri + urlPath,
                    method,
                    entity,
                    classType,
                    urlVariables );
        }
        catch ( HttpClientErrorException e )
        {
            if ( e.getStatusCode() == HttpStatus.NOT_FOUND )
            {
                throw new ConfigException( "Failed to perform " + method.name() + " at " +
                        expandUrl( urlPath, urlVariables ) + " on the Config Server. " +
                        "Received Status " + e.getStatusCode() );
            }
        }
        catch ( ResourceAccessException e )
        {
            throw new ConfigException( "Failed to access resource at " + expandUrl( urlPath, urlVariables ), e );
        }
        return responseEntity;
    }


    private HttpHeaders addSecurityHeaders( final HttpHeaders httpHeaders, final String username, final String password )
    {
        HttpHeaders headers = httpHeaders;
        if ( headers == null )
        {
            headers = new HttpHeaders();
        }
        String authorization = configClientProperties.getHeaders().get( AUTHORIZATION );
        final String token = configClientProperties.getToken();
        //
        // Set the token header if need to authenticate
        //
        if ( StringUtils.isNotBlank( token ) )
        {
            headers.add( TOKEN_HEADER, token );
        }
        if ( password != null && authorization != null )
        {
            throw new IllegalArgumentException( "You must set either 'password' or 'authorization.' Both cannot be used." );
        }
        if ( password != null )
        {
            byte[] credentialsEncoded = Base64Utils.encode( ( username + ":" + password ).getBytes() );
            httpHeaders.add( HEADER_AUTHORIZATION, "Basic " + new String( credentialsEncoded ) );
        }
        else if ( authorization != null )
        {
            httpHeaders.add( HEADER_AUTHORIZATION, authorization );
        }
        return headers;
    }

    private String expandUrl( final String url, final Object... urlVariables )
    {
        return UriComponentsBuilder.fromPath( url ).buildAndExpand( urlVariables ).toUriString();
    }
}
