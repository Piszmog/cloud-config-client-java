package io.github.piszmog.cloudconfig.client.impl;

import io.github.piszmog.cloudconfig.ConfigException;
import io.github.piszmog.cloudconfig.template.ConfigTemplate;
import io.github.piszmog.cloudconfig.client.ConfigClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * Handles retrieving any file types from the config server.
 * <p>
 * Created by Piszmog on 4/15/2018
 */
public class FileConfigClient extends ConfigClient
{
    // ============================================================
    // Class Constants:
    // ============================================================

    private static final String PATH = "/{name}/{profile}/{label}/{file}";
    private static final String VALUE_DEFAULT = "default";
    private static final String VALUE_DEFAULT_LABEL = "label";

    // ============================================================
    // Constructors:
    // ============================================================

    /**
     * Creates a new file config client.
     *
     * @param configTemplate the config server template
     */
    public FileConfigClient( final ConfigTemplate configTemplate )
    {
        super( configTemplate );
    }

    // ============================================================
    // Public Methods:
    // ============================================================

    /**
     * Retrieves the file matching the specified file name from the Config Server using {@link ConfigClientProperties}
     * values for the GIT branch.
     *
     * @param fileName  the name of the file to retrieve
     * @param classType the class type the data of the file will be converted to
     * @param <T>       the class type
     * @return The file converted to the specified class type.
     * @throws ConfigException when an error occurs when retrieving the specified file
     */
    public <T> T getFile( final String fileName, final Class<T> classType ) throws ConfigException
    {
        //
        // Use the configClientProperties value for label or 'master' if configClientProperties has no label
        //
        return getFile( fileName, null, classType );
    }

    /**
     * Retrieves the file matching the specified file name and on the specified branch.
     *
     * @param fileName  the name of the file to retrieve
     * @param branch    the GIT branch to retrieve the file from
     * @param classType the class type the data of the file will be converted to
     * @param <T>       the class type
     * @return The file converted to the specified class type.
     * @throws ConfigException when an error occurs when retrieving the specified file
     */
    public <T> T getFile( final String fileName, final String branch, final Class<T> classType ) throws ConfigException
    {
        if ( !StringUtils.isNotBlank( fileName ) )
        {
            throw new IllegalArgumentException( "No file supplied to look up." );
        }
        String applicationName = configTemplate.getName();
        String label = branch;
        if ( StringUtils.isBlank( branch ) )
        {
            label = configTemplate.getLabel();
        }
        String profile = configTemplate.getProfile();
        //
        // If the application name is not specified, use 'default'
        //
        if ( StringUtils.isBlank( applicationName ) )
        {
            applicationName = VALUE_DEFAULT;
        }
        //
        // If no profile is specified, use 'default'
        //
        if ( StringUtils.isBlank( profile ) )
        {
            profile = VALUE_DEFAULT;
        }
        //
        // If no specific branch is used, default to label -- maybe the config server knows
        //
        if ( StringUtils.isBlank( label ) )
        {
            label = VALUE_DEFAULT_LABEL;
        }
        ResponseEntity<T> responseEntity = configTemplate.sendAndReceive( PATH,
                HttpMethod.GET,
                null,
                null,
                classType,
                applicationName,
                profile,
                label,
                fileName );
        return responseEntity.getBody();
    }
}
