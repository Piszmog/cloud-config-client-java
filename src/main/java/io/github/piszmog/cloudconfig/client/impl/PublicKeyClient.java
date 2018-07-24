package io.github.piszmog.cloudconfig.client.impl;

import io.github.piszmog.cloudconfig.ConfigException;
import io.github.piszmog.cloudconfig.template.ConfigTemplate;
import io.github.piszmog.cloudconfig.client.ConfigClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * Handles retrieving the public key from the config server.
 * <p>
 * Created by Piszmog on 5/5/2018.
 */
public class PublicKeyClient extends ConfigClient
{
    // ============================================================
    // Class Constants:
    // ============================================================

    private static final String DEFAULT_PROFILE = "default";
    private static final String DEFAULT_APPLICATION = "application";
    private static final String PATH_PUBLIC_KEY = "/key/{name}/{profile}";

    // ============================================================
    // Constructors:
    // ============================================================

    public PublicKeyClient( final ConfigTemplate configTemplate )
    {
        super( configTemplate );
    }

    // ============================================================
    // Public Methods:
    // ============================================================

    /**
     * Retrieves the public key for the specified application and profile.
     *
     * @return The public key from the config server.
     * @throws ConfigException when an error occurs when getting the the public key
     */
    public String getPublicKey() throws ConfigException
    {
        return getPublicKey( configTemplate.getName() );
    }

    /**
     * Retrieves the public key for the specified application and using the default profile 'default'.
     *
     * @param applicationName the application name
     * @return The public key from the config server.
     * @throws ConfigException occurs when an error occurs when getting the the public key
     */
    public String getPublicKey( final String applicationName ) throws ConfigException
    {
        return getPublicKey( applicationName, configTemplate.getProfile() );
    }

    /**
     * Retrieves the public key for the specified application and profile.
     *
     * @param applicationName the application name
     * @param profile         the profile of the application
     * @return The public key from the config server.
     * @throws ConfigException when an error occurs when getting the the public key
     */
    public String getPublicKey( final String applicationName, final String profile ) throws ConfigException
    {
        String name = applicationName;
        String applicationProfile = profile;
        if ( StringUtils.isBlank( name ) )
        {
            name = DEFAULT_APPLICATION;
        }
        if ( StringUtils.isBlank( applicationProfile ) )
        {
            applicationProfile = DEFAULT_PROFILE;
        }
        final ResponseEntity<String> responseEntity = configTemplate.sendAndReceive( HttpMethod.GET, PATH_PUBLIC_KEY,
                null,
                null,
                String.class,
                name,
                applicationProfile );
        return responseEntity.getBody();
    }
}
