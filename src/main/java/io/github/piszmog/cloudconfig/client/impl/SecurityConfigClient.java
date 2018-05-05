package io.github.piszmog.cloudconfig.client.impl;

import io.github.piszmog.cloudconfig.ConfigException;
import io.github.piszmog.cloudconfig.client.ConfigClient;
import io.github.piszmog.cloudconfig.template.ConfigTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * Handles encryption and decryption using the config server.
 * <p>
 * Created by Piszmog on 5/5/2018
 */
abstract class SecurityConfigClient extends ConfigClient
{
    // ============================================================
    // Class Constants:
    // ============================================================

    private static final String DEFAULT_PROFILE = "default";
    private static final String DEFAULT_APPLICATION = "application";

    // ============================================================
    // Constructors:
    // ============================================================

    /**
     * Creates a new security client.
     *
     * @param configTemplate the template used to call the config server
     */
    SecurityConfigClient( final ConfigTemplate configTemplate )
    {
        super( configTemplate );
    }

    // ============================================================
    // Protected
    // ============================================================

    /**
     * Encrypts or decrypts the provided value.
     *
     * @param path            the path security path to call on the config server
     * @param value           the value to encrypt or decrypt
     * @param applicationName the application's name
     * @param profile         the profile of the application
     * @return The encrypted value.
     * @throws ConfigException when an error occurs when encrypting or decrypting the value
     */
    String encryptOrDecrypt( final String path, final String value, final String applicationName, final String profile ) throws ConfigException
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
        final ResponseEntity<String> responseEntity = configTemplate.sendAndReceive( path,
                HttpMethod.POST,
                value,
                null,
                String.class,
                name,
                applicationProfile );
        return responseEntity.getBody();
    }
}
