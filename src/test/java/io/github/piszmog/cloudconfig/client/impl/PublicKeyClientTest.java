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
public class PublicKeyClientTest {
    @Mock
    private ConfigTemplate template;
    private PublicKeyClient client;

    @BeforeEach
    public void setup() {
        client = new PublicKeyClient(template);
    }

    @Test
    public void testGetPublicKey() throws ConfigException {
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), isNull(), isNull(), eq(String.class),
                anyString(), anyString()))
                .thenReturn(ResponseEntity.ok("public-key"));
        final String publicKey = client.getPublicKey();
        assertEquals("public-key", publicKey);
        verify(template).sendAndReceive(eq(HttpMethod.GET), eq("/key/{name}/{profile}"), isNull(), isNull(), eq(String.class),
                eq("application"), eq("default"));
    }

    @Test
    public void testGetPublicKey_appName() throws ConfigException {
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), isNull(), isNull(), eq(String.class),
                anyString(), anyString()))
                .thenReturn(ResponseEntity.ok("public-key"));
        final String publicKey = client.getPublicKey("this-app");
        assertEquals("public-key", publicKey);
        verify(template).sendAndReceive(eq(HttpMethod.GET), eq("/key/{name}/{profile}"), isNull(), isNull(), eq(String.class),
                eq("this-app"), eq("default"));
    }

    @Test
    public void testGetPublicKey_profile() throws ConfigException {
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), isNull(), isNull(), eq(String.class),
                anyString(), anyString()))
                .thenReturn(ResponseEntity.ok("public-key"));
        final String publicKey = client.getPublicKey("this-app", "test");
        assertEquals("public-key", publicKey);
        verify(template).sendAndReceive(eq(HttpMethod.GET), eq("/key/{name}/{profile}"), isNull(), isNull(), eq(String.class),
                eq("this-app"), eq("test"));
    }

    @Test
    public void testGetPublicKey_null() throws ConfigException {
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), isNull(), isNull(), eq(String.class),
                anyString(), anyString()))
                .thenReturn(null);
        final String publicKey = client.getPublicKey();
        assertNull(publicKey);
        verify(template).sendAndReceive(eq(HttpMethod.GET), eq("/key/{name}/{profile}"), isNull(), isNull(), eq(String.class),
                eq("application"), eq("default"));
    }
}
