package io.github.piszmog.cloudconfig.template;

import io.github.piszmog.cloudconfig.ConfigException;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigClientStateHolder;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConfigTemplateTest {
    @Mock
    private RestTemplate restTemplate;
    private ConfigClientProperties properties;
    private TestConfigTemplate template;
    @Captor
    private ArgumentCaptor<HttpEntity> entityCaptor;

    @BeforeEach
    public void setup() {
        ConfigClientStateHolder.resetState();
        properties = new ConfigClientProperties(new StandardEnvironment());
        properties.setUri(new String[]{"http://localhost:8888"});
        template = new TestConfigTemplate(properties, restTemplate);
    }

    @Test
    public void testSendAndReceive() throws ConfigException {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), entityCaptor.capture(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("foo bar"));
        final ResponseEntity<String> responseEntity = template.sendAndReceive(HttpMethod.GET, "/foo", "hello world",
                null, String.class);
        assertNotNull(responseEntity);
        verify(restTemplate).exchange(eq("http://localhost:8888/foo"), eq(HttpMethod.GET), any(), eq(String.class));
        final HttpEntity req = entityCaptor.getValue();
        assertEquals("hello world", req.getBody());
        assertEquals(MediaType.APPLICATION_JSON, req.getHeaders().getAccept().get(0));
    }

    @Test
    public void testSendAndReceive_nullResp() throws ConfigException {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), entityCaptor.capture(), eq(String.class)))
                .thenReturn(null);
        final ResponseEntity<String> responseEntity = template.sendAndReceive(HttpMethod.GET, "/foo", "hello world",
                null, String.class);
        assertNull(responseEntity);
        verify(restTemplate).exchange(eq("http://localhost:8888/foo"), eq(HttpMethod.GET), any(), eq(String.class));
    }

    @Test
    public void testSendAndReceive_serverErrorException() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), entityCaptor.capture(), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThrows(ConfigException.class,
                () -> template.sendAndReceive(HttpMethod.GET, "/foo", "hello world", null, String.class));
    }

    @Test
    public void testSendAndReceive_resourceAccessException() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), entityCaptor.capture(), eq(String.class)))
                .thenThrow(new ResourceAccessException("Failed"));
        assertThrows(ConfigException.class,
                () -> template.sendAndReceive(HttpMethod.GET, "/foo", "hello world", null, String.class));
    }

    @Test
    public void testSendAndReceive_noURIs() throws ConfigException {
        final ConfigClientProperties properties = new ConfigClientProperties(new StandardEnvironment());
        properties.setUri(new String[]{});
        final TestConfigTemplate template = new TestConfigTemplate(this.properties, restTemplate);
        final ResponseEntity<String> resp = template.sendAndReceive(HttpMethod.GET, "/foo", "hello world",
                null, String.class);
        assertNull(resp);
    }

    @Test
    public void testSendAndReceive_noRequestBody() throws ConfigException {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), entityCaptor.capture(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("foo bar"));
        template.sendAndReceive(HttpMethod.GET, "/foo", null,
                null, String.class);
        verify(restTemplate).exchange(eq("http://localhost:8888/foo"), eq(HttpMethod.GET), any(), eq(String.class));
        final HttpEntity req = entityCaptor.getValue();
        assertNull(req.getBody());
        assertEquals(MediaType.APPLICATION_JSON, req.getHeaders().getAccept().get(0));
    }

    @Test
    public void testSendAndReceive_authorization() throws ConfigException {
        properties.setHeaders(Collections.singletonMap("authorization", "Bearer 12345"));
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), entityCaptor.capture(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("foo bar"));
        template.sendAndReceive(HttpMethod.GET, "/foo", "hello world",
                null, String.class);
        verify(restTemplate).exchange(eq("http://localhost:8888/foo"), eq(HttpMethod.GET), any(), eq(String.class));
        final HttpEntity req = entityCaptor.getValue();
        assertEquals("Bearer 12345", req.getHeaders().get("Authorization").get(0));
    }

    @Test
    public void testSendAndReceive_token() throws ConfigException {
        properties.setToken("abcd");
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), entityCaptor.capture(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("foo bar"));
        template.sendAndReceive(HttpMethod.GET, "/foo", "hello world",
                null, String.class);
        verify(restTemplate).exchange(eq("http://localhost:8888/foo"), eq(HttpMethod.GET), any(), eq(String.class));
        final HttpEntity req = entityCaptor.getValue();
        assertEquals("abcd", req.getHeaders().get("X-Config-Token").get(0));
    }

    @Test
    public void testSendAndReceive_password() throws ConfigException {
        properties.setUsername("user");
        properties.setPassword("password");
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), entityCaptor.capture(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("foo bar"));
        template.sendAndReceive(HttpMethod.GET, "/foo", "hello world",
                null, String.class);
        verify(restTemplate).exchange(eq("http://localhost:8888/foo"), eq(HttpMethod.GET), any(), eq(String.class));
        final HttpEntity req = entityCaptor.getValue();
        assertEquals("Basic dXNlcjpwYXNzd29yZA==", req.getHeaders().get("Authorization").get(0));
    }

    @Test
    public void testSendAndReceive_passwordAndAuth() {
        properties.setPassword("password");
        properties.setHeaders(Collections.singletonMap("authorization", "Bearer 12345"));
        assertThrows(IllegalArgumentException.class, () -> template.sendAndReceive(HttpMethod.GET, "/foo",
                "hello world", null, String.class));
    }

    @Test
    public void testSendAndReceive_stateHeader() throws ConfigException {
        ConfigClientStateHolder.setState("state");
        properties.setSendState(true);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), entityCaptor.capture(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("foo bar"));
        template.sendAndReceive(HttpMethod.GET, "/foo", "hello world",
                null, String.class);
        verify(restTemplate).exchange(eq("http://localhost:8888/foo"), eq(HttpMethod.GET), any(), eq(String.class));
        final HttpEntity req = entityCaptor.getValue();
        assertEquals("state", req.getHeaders().get("X-Config-State").get(0));
    }

    @Test
    public void testSendAndReceive_noStateHeader_notSet() throws ConfigException {
        properties.setSendState(true);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), entityCaptor.capture(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("foo bar"));
        template.sendAndReceive(HttpMethod.GET, "/foo", "hello world",
                null, String.class);
        verify(restTemplate).exchange(eq("http://localhost:8888/foo"), eq(HttpMethod.GET), any(), eq(String.class));
        final HttpEntity req = entityCaptor.getValue();
        assertNull(req.getHeaders().get("X-Config-State"));
    }

    @Test
    public void testSendAndReceive_noStateHeader_notTrue() throws ConfigException {
        ConfigClientStateHolder.setState("state");
        properties.setSendState(false);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), entityCaptor.capture(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("foo bar"));
        template.sendAndReceive(HttpMethod.GET, "/foo", "hello world",
                null, String.class);
        verify(restTemplate).exchange(eq("http://localhost:8888/foo"), eq(HttpMethod.GET), any(), eq(String.class));
        final HttpEntity req = entityCaptor.getValue();
        assertNull(req.getHeaders().get("X-Config-State"));
    }

    @Test
    public void testGetName() {
        properties.setName("fooBar");
        assertEquals("fooBar", template.getName());
    }

    @Test
    public void testGetProfile() {
        properties.setProfile("fooBar");
        assertEquals("fooBar", template.getProfile());
    }

    @Test
    public void testGetLabel() {
        properties.setLabel("fooBar");
        assertEquals("fooBar", template.getLabel());
    }

    @Test
    public void testCreateHttpClientFactory() {
        final ClientHttpRequestFactory factory = template.createFactory(10);
        final AbstractObjectAssert<?, ?> httpClient = assertThat(factory).extracting("httpClient");
        httpClient.extracting("defaultConfig")
                .hasFieldOrPropertyWithValue("connectionRequestTimeout", 10)
                .hasFieldOrPropertyWithValue("connectTimeout", 10)
                .hasFieldOrPropertyWithValue("socketTimeout", 10);
        httpClient.extracting("connManager")
                .extracting("pool")
                .hasFieldOrPropertyWithValue("defaultMaxPerRoute", 10)
                .hasFieldOrPropertyWithValue("maxTotal", 100);
    }

    private static class TestConfigTemplate extends ConfigTemplate {
        public TestConfigTemplate(ConfigClientProperties configClientProperties, RestTemplate restTemplate) {
            super(configClientProperties);
            this.restTemplate = restTemplate;
        }

        public ClientHttpRequestFactory createFactory(int timeout) {
            return this.createHttpClientFactory(timeout);
        }
    }
}
