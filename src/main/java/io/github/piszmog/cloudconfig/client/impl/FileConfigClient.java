package io.github.piszmog.cloudconfig.client.impl;

import io.github.piszmog.cloudconfig.ConfigException;
import io.github.piszmog.cloudconfig.client.ConfigClient;
import io.github.piszmog.cloudconfig.template.ConfigTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * Handles retrieving any file types from the config server.
 * <p>
 * Created by Piszmog on 4/15/2018
 */
public class FileConfigClient extends ConfigClient {
    private static final String PATH_NAME_PROFILE = "/{name}/{profile}/";
    private static final String PATH_FILE = "/{file}";
    private static final String VALUE_DEFAULT = "default";
    private static final String VALUE_DEFAULT_BRANCH = "master";

    /**
     * Creates a new file config client.
     *
     * @param configTemplate the config server template
     */
    public FileConfigClient(final ConfigTemplate configTemplate) {
        super(configTemplate);
    }

    /**
     * Retrieves the file matching the specified file name from the root of the {@value VALUE_DEFAULT_BRANCH} branch.
     *
     * @param fileName  the name of the file to retrieve
     * @param classType the class type the data of the file will be converted to
     * @param <T>       the class type
     * @return The file converted to the specified class type.
     * @throws ConfigException when an error occurs when retrieving the specified file
     */
    public <T> T getFileFromMaster(final String fileName, final Class<T> classType) throws ConfigException {
        //
        // Use the configClientProperties value for label or 'master' if configClientProperties has no label
        //
        return getFileFromBranch(fileName, VALUE_DEFAULT_BRANCH, classType);
    }

    /**
     * Retrieves the file matching the specified file name and on the root of the specified branch.
     *
     * @param fileName  the name of the file to retrieve
     * @param branch    the GIT branch to retrieve the file from
     * @param classType the class type the data of the file will be converted to
     * @param <T>       the class type
     * @return The file converted to the specified class type.
     * @throws ConfigException when an error occurs when retrieving the specified file
     */
    public <T> T getFileFromBranch(final String fileName, final String branch, final Class<T> classType) throws ConfigException {
        return getFileFromBranch(fileName, branch, null, classType);
    }

    /**
     * Retrieves the file matching the specified file name and on the specified branch in the specified directory.
     *
     * @param fileName      the name of the file to retrieve
     * @param branch        the GIT branch to retrieve the file from
     * @param directoryPath the directory to retrieve the file from
     * @param classType     the class type the data of the file will be converted to
     * @param <T>           the class type
     * @return The file converted to the specified class type.
     * @throws ConfigException when an error occurs when retrieving the specified file
     */
    public <T> T getFileFromBranch(final String fileName, final String branch, final String directoryPath, final Class<T> classType) throws ConfigException {
        if (!StringUtils.isNotBlank(fileName)) {
            throw new IllegalArgumentException("No file supplied to look up.");
        }
        String applicationName = configTemplate.getName();
        String profile = configTemplate.getProfile();
        //
        // If the application name is not specified, use 'default'
        //
        if (StringUtils.isBlank(applicationName)) {
            applicationName = VALUE_DEFAULT;
        }
        //
        // If no profile is specified, use 'default'
        //
        if (StringUtils.isBlank(profile)) {
            profile = VALUE_DEFAULT;
        }
        String label = branch;
        if (StringUtils.isBlank(branch)) {
            label = configTemplate.getLabel();
        }
        if (StringUtils.isBlank(label)) {
            label = VALUE_DEFAULT_BRANCH;
        }
        if (StringUtils.isNotBlank(directoryPath)) {
            final String path;
            if (StringUtils.startsWith(directoryPath, "/")) {
                path = directoryPath;
            } else {
                path = "/" + directoryPath;
            }
            label = label + path;
        }
        final String finalPath = PATH_NAME_PROFILE + label + PATH_FILE;
        final ResponseEntity<T> responseEntity = configTemplate.sendAndReceive(HttpMethod.GET, finalPath,
                null,
                null,
                classType,
                applicationName,
                profile,
                fileName);
        if (responseEntity == null) {
            return null;
        }
        return responseEntity.getBody();
    }

    /**
     * Retrieves the file matching the specified file name and in the Config Server's default branch in the specified
     * directory.
     *
     * @param fileName      the name of the file to retrieve
     * @param directoryPath the directory to retrieve the file from
     * @param classType     the class type the data of the file will be converted to
     * @param <T>           the class type
     * @return The file converted to the specified class type.
     * @throws ConfigException when an error occurs when retrieving the specified file
     */
    public <T> T getFileFromDefaultBranch(final String fileName, final String directoryPath, final Class<T> classType) throws ConfigException {
        if (!StringUtils.isNotBlank(fileName)) {
            throw new IllegalArgumentException("No file supplied to look up.");
        }
        if (!StringUtils.isNotBlank(directoryPath)) {
            throw new IllegalArgumentException("Files are unable to be located at the root. A directory path must be specified.");
        }
        String applicationName = configTemplate.getName();
        String profile = configTemplate.getProfile();
        //
        // If the application name is not specified, use 'default'
        //
        if (StringUtils.isBlank(applicationName)) {
            applicationName = VALUE_DEFAULT;
        }
        //
        // If no profile is specified, use 'default'
        //
        if (StringUtils.isBlank(profile)) {
            profile = VALUE_DEFAULT;
        }
        final String path;
        if (StringUtils.startsWith(directoryPath, "/")) {
            path = StringUtils.removeStart(directoryPath, "/");
        } else {
            path = directoryPath;
        }
        final String finalPath = PATH_NAME_PROFILE + path + PATH_FILE;
        final ResponseEntity<T> responseEntity;
        responseEntity = configTemplate.sendAndReceive(HttpMethod.GET, finalPath + "?useDefaultLabel=true",
                null,
                null,
                classType,
                applicationName,
                profile,
                fileName);
        if (responseEntity == null) {
            return null;
        }
        return responseEntity.getBody();
    }
}
