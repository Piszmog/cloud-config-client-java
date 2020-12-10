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
public class FileConfigClientTest {
    @Mock
    private ConfigTemplate template;
    private FileConfigClient client;

    @BeforeEach
    public void setup() {
        client = new FileConfigClient(template);
    }

    @Test
    public void testGetFileFromMaster() throws ConfigException {
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), isNull(), isNull(), eq(String.class),
                anyString(), anyString(), anyString()))
                .thenReturn(ResponseEntity.ok("hello"));
        final String file = client.getFileFromMaster("test.txt", String.class);
        assertEquals("hello", file);
        verify(template).sendAndReceive(eq(HttpMethod.GET), eq("/{name}/{profile}/master/{file}"),
                isNull(), isNull(), eq(String.class), eq("default"), eq("default"), eq("test.txt"));
    }

    @Test
    public void testGetFileFromBranch() throws ConfigException {
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), isNull(), isNull(), eq(String.class),
                anyString(), anyString(), anyString()))
                .thenReturn(ResponseEntity.ok("hello"));
        final String file = client.getFileFromBranch("test.txt", "develop", String.class);
        assertEquals("hello", file);
        verify(template).sendAndReceive(eq(HttpMethod.GET), eq("/{name}/{profile}/develop/{file}"),
                isNull(), isNull(), eq(String.class), eq("default"), eq("default"), eq("test.txt"));
    }

    @Test
    public void testGetFileFromBranch_directory() throws ConfigException {
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), isNull(), isNull(), eq(String.class),
                anyString(), anyString(), anyString()))
                .thenReturn(ResponseEntity.ok("hello"));
        final String file = client.getFileFromBranch("test.txt", "develop", "/foo", String.class);
        assertEquals("hello", file);
        verify(template).sendAndReceive(eq(HttpMethod.GET), eq("/{name}/{profile}/develop/foo/{file}"),
                isNull(), isNull(), eq(String.class), eq("default"), eq("default"), eq("test.txt"));
    }

    @Test
    public void testGetFileFromBranch_directory_missingSlash() throws ConfigException {
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), isNull(), isNull(), eq(String.class),
                anyString(), anyString(), anyString()))
                .thenReturn(ResponseEntity.ok("hello"));
        final String file = client.getFileFromBranch("test.txt", "develop", "foo", String.class);
        assertEquals("hello", file);
        verify(template).sendAndReceive(eq(HttpMethod.GET), eq("/{name}/{profile}/develop/foo/{file}"),
                isNull(), isNull(), eq(String.class), eq("default"), eq("default"), eq("test.txt"));
    }

    @Test
    public void testGetFileFromMaster_noFile() {
        assertThrows(IllegalArgumentException.class, () -> client.getFileFromMaster(null, String.class));
    }

    @Test
    public void testGetFileFromMaster_appName() throws ConfigException {
        when(template.getName()).thenReturn("this-app");
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), isNull(), isNull(), eq(String.class),
                anyString(), anyString(), anyString()))
                .thenReturn(ResponseEntity.ok("hello"));
        final String file = client.getFileFromMaster("test.txt", String.class);
        assertEquals("hello", file);
        verify(template).sendAndReceive(eq(HttpMethod.GET), eq("/{name}/{profile}/master/{file}"),
                isNull(), isNull(), eq(String.class), eq("this-app"), eq("default"), eq("test.txt"));
    }

    @Test
    public void testGetFileFromMaster_profile() throws ConfigException {
        when(template.getProfile()).thenReturn("test");
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), isNull(), isNull(), eq(String.class),
                anyString(), anyString(), anyString()))
                .thenReturn(ResponseEntity.ok("hello"));
        final String file = client.getFileFromMaster("test.txt", String.class);
        assertEquals("hello", file);
        verify(template).sendAndReceive(eq(HttpMethod.GET), eq("/{name}/{profile}/master/{file}"),
                isNull(), isNull(), eq(String.class), eq("default"), eq("test"), eq("test.txt"));
    }

    @Test
    public void testGetFileFromBranch_noBranch() throws ConfigException {
        when(template.getLabel()).thenReturn("test");
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), isNull(), isNull(), eq(String.class),
                anyString(), anyString(), anyString()))
                .thenReturn(ResponseEntity.ok("hello"));
        final String file = client.getFileFromBranch("test.txt", null, String.class);
        assertEquals("hello", file);
        verify(template).sendAndReceive(eq(HttpMethod.GET), eq("/{name}/{profile}/test/{file}"),
                isNull(), isNull(), eq(String.class), eq("default"), eq("default"), eq("test.txt"));
    }

    @Test
    public void testGetFileFromMaster_nullResponse() throws ConfigException {
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), isNull(), isNull(), eq(String.class),
                anyString(), anyString(), anyString()))
                .thenReturn(null);
        final String file = client.getFileFromMaster("test.txt", String.class);
        assertNull(file);
        verify(template).sendAndReceive(eq(HttpMethod.GET), eq("/{name}/{profile}/master/{file}"),
                isNull(), isNull(), eq(String.class), eq("default"), eq("default"), eq("test.txt"));
    }

    @Test
    public void testGetFileFromBranch_nullBranch() throws ConfigException {
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), isNull(), isNull(), eq(String.class),
                anyString(), anyString(), anyString()))
                .thenReturn(ResponseEntity.ok("hello"));
        final String file = client.getFileFromBranch("test.txt", null, String.class);
        assertEquals("hello", file);
        verify(template).sendAndReceive(eq(HttpMethod.GET), eq("/{name}/{profile}/master/{file}"),
                isNull(), isNull(), eq(String.class), eq("default"), eq("default"), eq("test.txt"));
    }

    @Test
    public void testGetFileFromDefaultBranch() throws ConfigException {
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), isNull(), isNull(), eq(String.class),
                anyString(), anyString(), anyString()))
                .thenReturn(ResponseEntity.ok("hello"));
        final String file = client.getFileFromDefaultBranch("test.txt", "/foo", String.class);
        assertEquals("hello", file);
        verify(template).sendAndReceive(eq(HttpMethod.GET), eq("/{name}/{profile}/foo/{file}?useDefaultLabel=true"),
                isNull(), isNull(), eq(String.class), eq("default"), eq("default"), eq("test.txt"));
    }

    @Test
    public void testGetFileFromDefaultBranch_noLeadingSlash() throws ConfigException {
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), isNull(), isNull(), eq(String.class),
                anyString(), anyString(), anyString()))
                .thenReturn(ResponseEntity.ok("hello"));
        final String file = client.getFileFromDefaultBranch("test.txt", "foo", String.class);
        assertEquals("hello", file);
        verify(template).sendAndReceive(eq(HttpMethod.GET), eq("/{name}/{profile}/foo/{file}?useDefaultLabel=true"),
                isNull(), isNull(), eq(String.class), eq("default"), eq("default"), eq("test.txt"));
    }

    @Test
    public void testGetFileFromDefaultBranch_missingFile() {
        assertThrows(IllegalArgumentException.class, () -> client.getFileFromDefaultBranch(null, "/foo", String.class));
    }

    @Test
    public void testGetFileFromDefaultBranch_missingDirectory() {
        assertThrows(IllegalArgumentException.class, () -> client.getFileFromDefaultBranch("test.txt", null, String.class));
    }

    @Test
    public void testGetFileFromDefaultBranch_nullResponse() throws ConfigException {
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), isNull(), isNull(), eq(String.class),
                anyString(), anyString(), anyString()))
                .thenReturn(null);
        final String file = client.getFileFromDefaultBranch("test.txt", "/foo", String.class);
        assertNull(file);
        verify(template).sendAndReceive(eq(HttpMethod.GET), eq("/{name}/{profile}/foo/{file}?useDefaultLabel=true"),
                isNull(), isNull(), eq(String.class), eq("default"), eq("default"), eq("test.txt"));
    }

    @Test
    public void testGetFileFromDefaultBranch_appName() throws ConfigException {
        when(template.getName()).thenReturn("this-app");
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), isNull(), isNull(), eq(String.class),
                anyString(), anyString(), anyString()))
                .thenReturn(ResponseEntity.ok("hello"));
        final String file = client.getFileFromDefaultBranch("test.txt", "/foo", String.class);
        assertEquals("hello", file);
        verify(template).sendAndReceive(eq(HttpMethod.GET), eq("/{name}/{profile}/foo/{file}?useDefaultLabel=true"),
                isNull(), isNull(), eq(String.class), eq("this-app"), eq("default"), eq("test.txt"));
    }

    @Test
    public void testGetFileFromDefaultBranch_profile() throws ConfigException {
        when(template.getProfile()).thenReturn("test");
        when(template.sendAndReceive(any(HttpMethod.class), anyString(), isNull(), isNull(), eq(String.class),
                anyString(), anyString(), anyString()))
                .thenReturn(ResponseEntity.ok("hello"));
        final String file = client.getFileFromDefaultBranch("test.txt", "/foo", String.class);
        assertEquals("hello", file);
        verify(template).sendAndReceive(eq(HttpMethod.GET), eq("/{name}/{profile}/foo/{file}?useDefaultLabel=true"),
                isNull(), isNull(), eq(String.class), eq("default"), eq("test"), eq("test.txt"));
    }
}
