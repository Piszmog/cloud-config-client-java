package io.github.piszmog.cloudconfig.template;

import io.github.piszmog.cloudconfig.ConfigException;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigClientStateHolder;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Base64;
import java.util.Collections;

import static org.springframework.cloud.config.client.ConfigClientProperties.*;

/**
 * Template used for calling endpoints on the config server.
 * <p>
 * Created by Piszmog on 5/5/2018
 */
public abstract class ConfigTemplate {
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final int DEFAULT_MAX_PER_ROUTE = 10;
    private static final int DEFAULT_TOTAL_CONNECTIONS = 100;

    private final ThreadLocal<HttpHeaders> httpHeadersThreadLocal = new ThreadLocal<>();
    protected final ConfigClientProperties configClientProperties;
    protected RestTemplate restTemplate;

    /**
     * creates a new template using the properties provided.
     *
     * @param configClientProperties the config server properties
     */
    public ConfigTemplate(final ConfigClientProperties configClientProperties) {
        this.configClientProperties = configClientProperties;
    }

    /**
     * Send the HTTP request to the Config Server and return the response from the Server.
     *
     * @param <T>          the class type the response is converted to
     * @param method       the operation to perform
     * @param urlPath      the url path to perform the request at -- adds to the config server url
     * @param requestBody  the request body to send in request
     * @param httpHeaders  http headers to send in request
     * @param classType    the class type to convert response to
     * @param urlVariables the URL path variables
     * @return The response entity from the HTTP call. Return null if an error occurred.
     * @throws ConfigException exception occurs when an error occurs when operating on the config server or when the
     *                         status code is not a 2xx
     */
    public <T> ResponseEntity<T> sendAndReceive(final HttpMethod method,
                                                final String urlPath,
                                                final Object requestBody,
                                                final HttpHeaders httpHeaders,
                                                final Class<T> classType,
                                                final Object... urlVariables) throws ConfigException {
        final String[] configUris = configClientProperties.getUri();
        final int numberOfUris = configUris.length;
        for (int i = 0; i < numberOfUris; i++) {
            Credentials credentials = configClientProperties.getCredentials(i);
            final ResponseEntity<T> responseEntity = sendAndReceiveToConfigServer(method,
                    urlPath,
                    urlVariables,
                    httpHeaders,
                    requestBody,
                    credentials,
                    classType);
            if (responseEntity != null) {
                return responseEntity;
            }
        }
        return null;
    }

    private <T> ResponseEntity<T> sendAndReceiveToConfigServer(final HttpMethod method,
                                                               final String urlPath,
                                                               final Object[] urlVariables,
                                                               final HttpHeaders httpHeaders,
                                                               final Object requestBody,
                                                               final Credentials credentials,
                                                               final Class<T> classType) throws ConfigException {
        String configUri = credentials.getUri();
        String username = credentials.getUsername();
        String password = credentials.getPassword();
        HttpEntity<Object> entity;
        HttpHeaders headers = addSecurityHeaders(httpHeaders, username, password);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        addStateHeader(headers);
        if (requestBody == null) {
            entity = new HttpEntity<>(headers);
        } else {
            entity = new HttpEntity<>(requestBody, headers);
        }
        return sendAndReceive(method, urlPath, urlVariables, entity, configUri, classType);
    }

    private HttpHeaders addSecurityHeaders(final HttpHeaders httpHeaders, final String username, final String password) {
        HttpHeaders headers = httpHeaders;
        if (headers == null) {
            HttpHeaders localHttpHeaders = httpHeadersThreadLocal.get();
            if (localHttpHeaders == null) {
                localHttpHeaders = new HttpHeaders();
                httpHeadersThreadLocal.set(localHttpHeaders);
            }
            localHttpHeaders.clear();
            headers = localHttpHeaders;
        }
        String authorization = configClientProperties.getHeaders().get(AUTHORIZATION);
        final String token = configClientProperties.getToken();
        //
        // Set the token header if need to authenticate
        //
        if (StringUtils.isNotBlank(token)) {
            headers.add(TOKEN_HEADER, token);
        }
        if (password != null && authorization != null) {
            throw new IllegalArgumentException("You must set either 'password' or 'authorization.' Both cannot be used.");
        }
        if (password != null) {
            byte[] credentialsEncoded = Base64.getEncoder().encode((username + ":" + password).getBytes());
            headers.add(HEADER_AUTHORIZATION, "Basic " + new String(credentialsEncoded));
        } else if (authorization != null) {
            headers.add(HEADER_AUTHORIZATION, authorization);
        }
        return headers;
    }

    private void addStateHeader(final HttpHeaders headers) {
        final String state = ConfigClientStateHolder.getState();
        if (StringUtils.isNotBlank(state) && configClientProperties.isSendState()) {
            headers.add(STATE_HEADER, state);
        }
    }

    private <T> ResponseEntity<T> sendAndReceive(final HttpMethod method,
                                                 final String urlPath,
                                                 final Object[] urlVariables,
                                                 final HttpEntity<Object> entity,
                                                 final String configUri,
                                                 final Class<T> classType) throws ConfigException {
        ResponseEntity<T> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(configUri + urlPath,
                    method,
                    entity,
                    classType,
                    urlVariables);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
                throw new ConfigException("Failed to perform " + method.name() + " at " +
                        expandUrl(urlPath, urlVariables) + " on the Config Server. " +
                        "Received Status " + e.getStatusCode(), e);
            }
        } catch (ResourceAccessException e) {
            throw new ConfigException("Failed to access resource at " + expandUrl(urlPath, urlVariables), e);
        }
        return responseEntity;
    }

    private String expandUrl(final String url, final Object... urlVariables) {
        return UriComponentsBuilder.fromPath(url).buildAndExpand(urlVariables).toUriString();
    }

    /**
     * Retrieves the name of the application.
     *
     * @return The name of the application.
     */
    public String getName() {
        return configClientProperties.getName();
    }

    /**
     * Retrieves the profile of the application.
     *
     * @return The profile.
     */
    public String getProfile() {
        return configClientProperties.getProfile();
    }

    /**
     * Retrieves the label being used by the config server.
     *
     * @return The label of the config server.
     */
    public String getLabel() {
        return configClientProperties.getLabel();
    }

    /**
     * Creates a pooling request factory with the provided timeout. The pool of connections allow for 10 max connections
     * per route with a total of 100 connections.
     *
     * @return The client http request factory.
     */
    protected ClientHttpRequestFactory createHttpClientFactory() {
        final RequestConfig requestConfig = buildRequestConfig();
        final HttpClientConnectionManager connectionManager = createConnectionManager();
        final HttpClient httpClient = buildHttpClient(requestConfig, connectionManager);
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    private RequestConfig buildRequestConfig() {
        return RequestConfig.custom().build();
    }

    private PoolingHttpClientConnectionManager createConnectionManager() {
        final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
        connectionManager.setMaxTotal(DEFAULT_TOTAL_CONNECTIONS);
        return connectionManager;
    }

    private CloseableHttpClient buildHttpClient(final RequestConfig requestConfig, final HttpClientConnectionManager connectionManager) {
        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }
}
