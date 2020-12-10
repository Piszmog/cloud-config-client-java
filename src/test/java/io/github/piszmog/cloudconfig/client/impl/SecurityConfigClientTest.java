package io.github.piszmog.cloudconfig.client.impl;


import io.github.piszmog.cloudconfig.ConfigException;
import io.github.piszmog.cloudconfig.template.ConfigTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SecurityConfigClientTest {
    @Mock
    private ConfigTemplate template;
    private SecurityConfigClient client;

    @BeforeEach
    public void setup() {
        client = new TestSecurityConfigClient(template);
    }

    @Test
    public void testEncryptOrDecrypt() throws ConfigException {
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), anyString(), isNull(), eq(String.class),
                anyString(), anyString()))
                .thenReturn(ResponseEntity.ok("encrypted-value"));
        final String value = client.encryptOrDecrypt("/foo", "bar", "test-app", "cloud");
        assertEquals("encrypted-value", value);
        verify(template).sendAndReceive(eq(HttpMethod.POST), eq("/foo"), eq("bar"), isNull(), eq(String.class),
                eq("test-app"), eq("cloud"));
    }

    @Test
    public void testEncryptOrDecrypt_defaultName() throws ConfigException {
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), anyString(), isNull(), eq(String.class),
                anyString(), anyString()))
                .thenReturn(ResponseEntity.ok("encrypted-value"));
        final String value = client.encryptOrDecrypt("/foo", "bar", null, "cloud");
        assertEquals("encrypted-value", value);
        verify(template).sendAndReceive(eq(HttpMethod.POST), eq("/foo"), eq("bar"), isNull(), eq(String.class),
                eq("application"), eq("cloud"));
    }

    @Test
    public void testEncryptOrDecrypt_defaultProfile() throws ConfigException {
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), anyString(), isNull(), eq(String.class),
                anyString(), anyString()))
                .thenReturn(ResponseEntity.ok("encrypted-value"));
        final String value = client.encryptOrDecrypt("/foo", "bar", "test-app", null);
        assertEquals("encrypted-value", value);
        verify(template).sendAndReceive(eq(HttpMethod.POST), eq("/foo"), eq("bar"), isNull(), eq(String.class),
                eq("test-app"), eq("default"));
    }

    @Test
    public void testEncryptOrDecrypt_nullResponse() throws ConfigException {
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), anyString(), isNull(), eq(String.class),
                anyString(), anyString()))
                .thenReturn(null);
        final String value = client.encryptOrDecrypt("/foo", "bar", "test-app", "cloud");
        assertNull(value);
    }

    private static class TestSecurityConfigClient extends SecurityConfigClient {
        TestSecurityConfigClient(ConfigTemplate configTemplate) {
            super(configTemplate);
        }
    }
}
