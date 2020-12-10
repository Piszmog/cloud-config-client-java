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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DecryptConfigClientTest {
    @Mock
    private ConfigTemplate template;
    private DecryptConfigClient client;

    @BeforeEach
    public void setup() {
        client = new DecryptConfigClient(template);
    }

    @Test
    public void testDecrypt() throws ConfigException {
        when(template.getName()).thenReturn("app");
        when(template.getProfile()).thenReturn("cloud");
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), anyString(), isNull(), eq(String.class),
                anyString(), anyString()))
                .thenReturn(ResponseEntity.ok("decrypted-value"));
        final String decrypt = client.decrypt("encrypted-value");
        assertEquals("decrypted-value", decrypt);
        verify(template).sendAndReceive(eq(HttpMethod.POST), eq("/decrypt/{name}/{profiles}"), eq("encrypted-value"),
                isNull(), eq(String.class), eq("app"), eq("cloud"));
    }

    @Test
    public void testDecrypt_appName() throws ConfigException {
        when(template.getProfile()).thenReturn("cloud");
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), anyString(), isNull(), eq(String.class),
                anyString(), anyString()))
                .thenReturn(ResponseEntity.ok("decrypted-value"));
        final String decrypt = client.decrypt("encrypted-value", "this-app");
        assertEquals("decrypted-value", decrypt);
        verify(template).sendAndReceive(eq(HttpMethod.POST), eq("/decrypt/{name}/{profiles}"), eq("encrypted-value"),
                isNull(), eq(String.class), eq("this-app"), eq("cloud"));
    }

    @Test
    public void testDecrypt_profile() throws ConfigException {
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), anyString(), isNull(), eq(String.class),
                anyString(), anyString()))
                .thenReturn(ResponseEntity.ok("decrypted-value"));
        final String decrypt = client.decrypt("encrypted-value", "this-app", "test");
        assertEquals("decrypted-value", decrypt);
        verify(template).sendAndReceive(eq(HttpMethod.POST), eq("/decrypt/{name}/{profiles}"), eq("encrypted-value"),
                isNull(), eq(String.class), eq("this-app"), eq("test"));
    }
}
