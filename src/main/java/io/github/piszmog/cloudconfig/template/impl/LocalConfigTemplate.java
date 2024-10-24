package io.github.piszmog.cloudconfig.template.impl;

import io.github.piszmog.cloudconfig.template.ConfigTemplate;
import jakarta.annotation.PostConstruct;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.cloud.config.client.ConfigClientProperties.AUTHORIZATION;

/**
 * Config template used when the config server is a local application.
 * <p>
 * Created by Piszmog on 5/5/2018
 */
public class LocalConfigTemplate extends ConfigTemplate {
    /**
     * Creates a local config template using the default read timeout.
     *
     * @param configClientProperties the config server properties.
     */
    public LocalConfigTemplate(final ConfigClientProperties configClientProperties) {
        super(configClientProperties);
    }

    /**
     * Initializes the template with the config server properties.
     */
    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(createHttpClientFactory());
        final Map<String, String> headers = new HashMap<>(configClientProperties.getHeaders());
        headers.remove(AUTHORIZATION);
        if (!headers.isEmpty()) {
            this.restTemplate.setInterceptors(Collections.singletonList(new ConfigServicePropertySourceLocator.GenericRequestHeaderInterceptor(headers)));
        }
    }
}
