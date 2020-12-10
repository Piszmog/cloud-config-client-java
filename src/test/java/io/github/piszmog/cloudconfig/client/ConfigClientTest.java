package io.github.piszmog.cloudconfig.client;

import io.github.piszmog.cloudconfig.template.ConfigTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.core.env.StandardEnvironment;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConfigClientTest {
    @Test
    public void testCreate() {
        final TestConfigClient client = new TestConfigClient(new TestConfigTemplate(new ConfigClientProperties(new StandardEnvironment())));
        assertNotNull(client);
    }

    private static class TestConfigClient extends ConfigClient {
        public TestConfigClient(ConfigTemplate configTemplate) {
            super(configTemplate);
        }
    }

    private static class TestConfigTemplate extends ConfigTemplate {
        public TestConfigTemplate(ConfigClientProperties configClientProperties) {
            super(configClientProperties);
        }
    }
}
