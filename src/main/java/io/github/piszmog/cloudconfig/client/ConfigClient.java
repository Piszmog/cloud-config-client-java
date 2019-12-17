package io.github.piszmog.cloudconfig.client;

import io.github.piszmog.cloudconfig.template.ConfigTemplate;

/**
 * The client for the config server.
 * <p>
 * Created by Piszmog on 5/5/2018
 */
public abstract class ConfigClient {
    protected final ConfigTemplate configTemplate;

    /**
     * Creates a new client using the config template to connect to the config server.
     *
     * @param configTemplate the template used to call the server
     */
    public ConfigClient(final ConfigTemplate configTemplate) {
        this.configTemplate = configTemplate;
    }
}
