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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EncryptConfigClientTest {
    @Mock
    private ConfigTemplate template;
    private EncryptConfigClient client;

    @BeforeEach
    public void setup() {
        client = new EncryptConfigClient(template);
    }

    @Test
    public void testIsEncryptionEnabled() throws ConfigException {
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), isNull(), isNull(), eq(String.class)))
                .thenReturn(ResponseEntity.ok().build());
        final boolean enabled = client.isEncryptionEnabled();
        assertTrue(enabled);
        verify(template).sendAndReceive(eq(HttpMethod.GET), eq("/encrypt/status"), isNull(), isNull(), eq(String.class));
    }

    @Test
    public void testIsEncryptionEnabled_false() throws ConfigException {
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), isNull(), isNull(), eq(String.class)))
                .thenReturn(null);
        final boolean enabled = client.isEncryptionEnabled();
        assertFalse(enabled);
        verify(template).sendAndReceive(eq(HttpMethod.GET), eq("/encrypt/status"), isNull(), isNull(), eq(String.class));
    }

    @Test
    public void testEncrypt() throws ConfigException {
        when(template.getName()).thenReturn("app");
        when(template.getProfile()).thenReturn("cloud");
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), anyString(), isNull(), eq(String.class),
                anyString(), anyString()))
                .thenReturn(ResponseEntity.ok("encrypted-value"));
        final String decrypt = client.encrypt("value");
        assertEquals("encrypted-value", decrypt);
        verify(template).sendAndReceive(eq(HttpMethod.POST), eq("/encrypt/{name}/{profiles}"), eq("value"),
                isNull(), eq(String.class), eq("app"), eq("cloud"));
    }

    @Test
    public void testEncrypt_appName() throws ConfigException {
        when(template.getProfile()).thenReturn("cloud");
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), anyString(), isNull(), eq(String.class),
                anyString(), anyString()))
                .thenReturn(ResponseEntity.ok("encrypted-value"));
        final String decrypt = client.encrypt("value", "this-app");
        assertEquals("encrypted-value", decrypt);
        verify(template).sendAndReceive(eq(HttpMethod.POST), eq("/encrypt/{name}/{profiles}"), eq("value"),
                isNull(), eq(String.class), eq("this-app"), eq("cloud"));
    }

    @Test
    public void testEncrypt_profile() throws ConfigException {
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), anyString(), isNull(), eq(String.class),
                anyString(), anyString()))
                .thenReturn(ResponseEntity.ok("encrypted-value"));
        final String decrypt = client.encrypt("value", "this-app", "test");
        assertEquals("encrypted-value", decrypt);
        verify(template).sendAndReceive(eq(HttpMethod.POST), eq("/encrypt/{name}/{profiles}"), eq("value"),
                isNull(), eq(String.class), eq("this-app"), eq("test"));
    }
}
