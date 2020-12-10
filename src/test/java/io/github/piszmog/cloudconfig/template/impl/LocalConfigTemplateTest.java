package io.github.piszmog.cloudconfig.template.impl;

import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LocalConfigTemplateTest {
    @Test
    public void testCreate_properties() {
        final LocalConfigTemplate template = new LocalConfigTemplate(new ConfigClientProperties(new StandardEnvironment()));
        assertNotNull(template);
        assertThat(template).hasFieldOrPropertyWithValue("readTimeout", 185000);
    }

    @Test
    public void testCreate_timeout() {
        final LocalConfigTemplate template = new LocalConfigTemplate(
                new ConfigClientProperties(new StandardEnvironment()),
                100);
        assertNotNull(template);
        assertThat(template).hasFieldOrPropertyWithValue("readTimeout", 100);
    }

    @Test
    public void testInit() {
        final LocalConfigTemplate template = new LocalConfigTemplate(new ConfigClientProperties(new StandardEnvironment()));
        template.init();
        assertThat(template).extracting("restTemplate").isInstanceOf(RestTemplate.class).isNotNull();
        final AbstractObjectAssert<?, ?> httpClient = assertThat(template).extracting("restTemplate").extracting("requestFactory")
                .extracting("httpClient");
        httpClient.extracting("defaultConfig")
                .hasFieldOrPropertyWithValue("connectionRequestTimeout", 185000)
                .hasFieldOrPropertyWithValue("connectTimeout", 185000)
                .hasFieldOrPropertyWithValue("socketTimeout", 185000);
        httpClient.extracting("connManager")
                .extracting("pool")
                .hasFieldOrPropertyWithValue("defaultMaxPerRoute", 10)
                .hasFieldOrPropertyWithValue("maxTotal", 100);

    }

    @Test
    public void testInit_authHeader() {
        final ConfigClientProperties properties = new ConfigClientProperties(new StandardEnvironment());
        properties.getHeaders().put("authorization", "Bearer 12345");
        final LocalConfigTemplate template = new LocalConfigTemplate(properties);
        template.init();
        assertThat(template).extracting("restTemplate")
                .extracting("interceptors")
                .asList()
                .hasSize(0);
    }

    @Test
    public void testInit_header() {
        final ConfigClientProperties properties = new ConfigClientProperties(new StandardEnvironment());
        properties.getHeaders().put("foo", "Bearer 12345");
        final LocalConfigTemplate template = new LocalConfigTemplate(properties);
        template.init();
        assertThat(template).extracting("restTemplate")
                .extracting("interceptors")
                .asList()
                .hasSize(1)
                .first()
                .isInstanceOf(ConfigServicePropertySourceLocator.GenericRequestHeaderInterceptor.class);
    }
}
