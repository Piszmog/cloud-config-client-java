package io.github.piszmog.cloudconfig.client.impl;

import io.github.piszmog.cloudconfig.ConfigException;
import io.github.piszmog.cloudconfig.template.ConfigTemplate;

/**
 * Handles decryption using the config server.
 * <p>
 * Created by Piszmog on 4/15/2018.
 */
public class DecryptConfigClient extends SecurityConfigClient
{
    // ============================================================
    // Class Constants:
    // ============================================================

    private static final String PATH_DECRYPT = "/decrypt/{name}/{profiles}";

    // ============================================================
    // Constructors:
    // ============================================================

    public DecryptConfigClient( final ConfigTemplate configTemplate )
    {
        super( configTemplate );
    }

    // ============================================================
    // Public Methods:
    // ============================================================

    /**
     * Decrypts the provided value by using the Config Server's Decryption.
     *
     * @param value the value to encrypt
     * @return The decrypted value or null if unable to encrypt.
     * @throws ConfigException when an error occurs when decrypting the value
     */
    public String decrypt( final String value ) throws ConfigException
    {
        return decrypt( value, configTemplate.getName(), configTemplate.getProfile() );
    }

    /**
     * Decrypts the provided value by using the Config Server's Decryption for the specified application and default
     * profile value 'default'.
     *
     * @param value           the value to encrypt
     * @param applicationName the application name
     * @return The decrypted value or null if unable to encrypt.
     * @throws ConfigException when an error occurs when decrypting the value
     */
    public String decrypt( final String value, final String applicationName ) throws ConfigException
    {
        return decrypt( value, applicationName, configTemplate.getProfile() );
    }

    /**
     * Decrypts the provided value by using the Config Server's Decryption for the specified application and profile.
     *
     * @param value           the value to encrypt
     * @param applicationName the application name
     * @param profile         the profile of the application
     * @return The decrypted value or null if unable to encrypt.
     * @throws ConfigException when an error occurs when decrypting the value
     */
    public String decrypt( final String value, final String applicationName, final String profile ) throws ConfigException
    {
        return encryptOrDecrypt( PATH_DECRYPT, value, applicationName, profile );
    }
}
