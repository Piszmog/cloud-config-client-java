package io.github.piszmog.cloudconfig.client.impl;

import io.github.piszmog.cloudconfig.ConfigException;
import io.github.piszmog.cloudconfig.template.ConfigTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * Handles encryption using the config server.
 * <p>
 * Created by Piszmog on 4/15/2018.
 */
public class EncryptConfigClient extends SecurityConfigClient
{
    // ============================================================
    // Class Constants:
    // ============================================================

    private static final String PATH_ENCRYPT_STATUS = "/encrypt/status";
    private static final String PATH_ENCRYPT = "/encrypt/{name}/{profiles}";

    // ============================================================
    // Constructors:
    // ============================================================

    public EncryptConfigClient( final ConfigTemplate configTemplate )
    {
        super( configTemplate );
    }

    // ============================================================
    // Public Methods:
    // ============================================================

    /**
     * Checks if encryption is enabled on the Config Server.
     *
     * @return True if encryption is enabled or false if it is not.
     * @throws ConfigException when an error occurs when determining if encryption is enabled
     */
    public boolean isEncryptionEnabled() throws ConfigException
    {
        final ResponseEntity<String> responseEntity = configTemplate.sendAndReceive( PATH_ENCRYPT_STATUS,
                HttpMethod.GET,
                null,
                null,
                String.class );
        //
        // If null, then encryption is not enabled. It is not considered an error
        //
        return responseEntity != null;
    }

    /**
     * Encrypts the provided value by using the Config Server's Encryption.
     *
     * @param value the value to encrypt
     * @return The encrypted value or null if unable to encrypt.
     * @throws ConfigException when an error occurs when encrypting the value
     */
    public String encrypt( final String value ) throws ConfigException
    {
        return encrypt( value, configTemplate.getName(), configTemplate.getProfile() );
    }

    /**
     * Encrypts the provided value by using the Config Server's Encryption for the specified application.
     *
     * @param value           the value to encrypt
     * @param applicationName the application name
     * @return The encrypted value or null if unable to encrypt.
     * @throws ConfigException when an error occurs when encrypting the value
     */
    public String encrypt( final String value, final String applicationName ) throws ConfigException
    {
        return encrypt( value, applicationName, configTemplate.getProfile() );
    }

    /**
     * Encrypts the provided value by using the Config Server's Encryption for the specified application and profile.
     *
     * @param value           the value to encrypt
     * @param applicationName the application name
     * @param profile         the profile of the application
     * @return The encrypted value or null if unable to encrypt.
     * @throws ConfigException when an error occurs when encrypting the value
     */
    public String encrypt( final String value, final String applicationName, final String profile ) throws ConfigException
    {
        return encryptOrDecrypt( PATH_ENCRYPT, value, applicationName, profile );
    }
}
