package io.github.piszmog.cloudconfigclient.template

import io.github.piszmog.cloudconfig.ConfigException
import io.github.piszmog.cloudconfig.template.ConfigTemplate
import org.springframework.cloud.config.client.ConfigClientProperties
import org.springframework.core.env.StandardEnvironment
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

/**
 * Unit tests for {@link ConfigTemplate}
 *
 * Created by Piszmog on 5/5/2018
 */
class ConfigTemplateSpec extends Specification
{
    // ============================================================
    // Class Attributes:
    // ============================================================

    private ConfigClientProperties configClientProperties

    // ============================================================
    // Setup:
    // ============================================================

    def setup()
    {
        configClientProperties = new ConfigClientProperties( new StandardEnvironment() )
    }

    // ============================================================
    // Tests:
    // ============================================================

    def "configTemplate retrieves name"()
    {
        given: "configClientProperties"
        ConfigClientProperties mockConfigClientProperties = Mock( ConfigClientProperties )

        and: "a restTemplate"
        RestTemplate restTemplate = Mock( RestTemplate )

        and: "a configTemplate"
        def testConfigTemplate = new TestConfigTemplate( mockConfigClientProperties, restTemplate )

        when: "name is retrieved from the client properties"
        def name = testConfigTemplate.getName()

        then: "name is returned"
        mockConfigClientProperties.getName() >> "name"

        then: "name is expected"
        name == "name"
    }

    def "configTemplate retrieves profile"()
    {
        given: "configClientProperties"
        ConfigClientProperties mockConfigClientProperties = Mock( ConfigClientProperties )

        and: "a restTemplate"
        RestTemplate restTemplate = Mock( RestTemplate )

        and: "a configTemplate"
        def testConfigTemplate = new TestConfigTemplate( mockConfigClientProperties, restTemplate )

        when: "profile is retrieved from the client properties"
        def profile = testConfigTemplate.getProfile()

        then: "name is returned"
        mockConfigClientProperties.getProfile() >> "profile"

        then: "profile is expected"
        profile == "profile"
    }

    def "configTemplate retrieves label"()
    {
        given: "configClientProperties"
        ConfigClientProperties mockConfigClientProperties = Mock( ConfigClientProperties )

        and: "a restTemplate"
        RestTemplate restTemplate = Mock( RestTemplate )

        and: "a configTemplate"
        def testConfigTemplate = new TestConfigTemplate( mockConfigClientProperties, restTemplate )

        when: "label is retrieved from the client properties"
        def profile = testConfigTemplate.getLabel()

        then: "label is returned"
        mockConfigClientProperties.getLabel() >> "label"

        then: "label is expected"
        profile == "label"
    }

    def "configTemplate performs a GET"()
    {
        given: "a restTemplate"
        RestTemplate restTemplate = Mock( RestTemplate )

        and: "a configTemplate"
        def testConfigTemplate = new TestConfigTemplate( configClientProperties, restTemplate )

        and: "a responseEntity"
        ResponseEntity responseEntity = Mock( ResponseEntity )

        and: "a status code"
        HttpStatus httpStatus = HttpStatus.OK

        when: "configTemplate sends and receives a request"
        testConfigTemplate.sendAndReceive( HttpMethod.GET, "url", null, null, String )

        then: "response entity is returned"
        1 * restTemplate.exchange( _, _, _, _ ) >> responseEntity

        and: "response entity returns status code OK"
        responseEntity.getStatusCode() >> httpStatus
    }

    def "configTemplate performs a GET using a token"()
    {
        given: "configClientProperties"
        ConfigClientProperties mockConfigClientProperties = Mock( ConfigClientProperties )

        and: "a restTemplate"
        RestTemplate restTemplate = Mock( RestTemplate )

        and: "a configTemplate"
        def testConfigTemplate = new TestConfigTemplate( mockConfigClientProperties, restTemplate )

        and: "a responseEntity"
        ResponseEntity responseEntity = Mock( ResponseEntity )

        and: "a status code"
        HttpStatus httpStatus = HttpStatus.OK

        when: "configTemplate sends and receives a request"
        testConfigTemplate.sendAndReceive( HttpMethod.GET, "url", null, null, String )

        then: "token is used"
        mockConfigClientProperties.getToken() >> "token"

        and: "response entity is returned"
        1 * restTemplate.exchange( _, _, _, _ ) >> responseEntity

        and: "response entity returns status code OK"
        responseEntity.getStatusCode() >> httpStatus
    }

    def "configTemplate performs a GET but RestClientException is thrown"()
    {
        given: "a restTemplate"
        RestTemplate restTemplate = Mock( RestTemplate )

        and: "a configTemplate"
        def testConfigTemplate = new TestConfigTemplate( configClientProperties, restTemplate )

        when: "configTemplate sends and receives a request"
        testConfigTemplate.sendAndReceive( HttpMethod.GET, "url", null, null, String )

        then: "RestClientException is thrown"
        1 * restTemplate.exchange( _, _, _, _ ) >> {
            throw new RestClientException( "Failed" )
        }

        and: "ConfigException is thrown"
        thrown( ConfigException )
    }

    def "configTemplate performs a GET using a token but is unsuccessful"()
    {
        given: "configClientProperties"
        ConfigClientProperties mockConfigClientProperties = Mock( ConfigClientProperties )

        and: "a restTemplate"
        RestTemplate restTemplate = Mock( RestTemplate )

        and: "a configTemplate"
        def testConfigTemplate = new TestConfigTemplate( mockConfigClientProperties, restTemplate )

        and: "a responseEntity"
        ResponseEntity responseEntity = Mock( ResponseEntity )

        and: "a status code"
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR

        when: "configTemplate sends and receives a request"
        testConfigTemplate.sendAndReceive( HttpMethod.GET, "url", null, null, String )

        then: "token is used"
        mockConfigClientProperties.getToken() >> "token"

        and: "response entity is returned"
        1 * restTemplate.exchange( _, _, _, _ ) >> responseEntity

        and: "response entity returns status code INTERNAL_SERVER_ERROR"
        responseEntity.getStatusCode() >> httpStatus

        and: "ConfigException is thrown"
        thrown( ConfigException )
    }

    def "configTemplate performs a POST and sends a request body"()
    {
        given: "a restTemplate"
        RestTemplate restTemplate = Mock( RestTemplate )

        and: "a configTemplate"
        def testConfigTemplate = new TestConfigTemplate( configClientProperties, restTemplate )

        and:"a request body"
        def requestBody = "body"

        and: "a responseEntity"
        ResponseEntity responseEntity = Mock( ResponseEntity )

        and: "a status code"
        HttpStatus httpStatus = HttpStatus.OK

        when: "configTemplate sends and receives a request"
        testConfigTemplate.sendAndReceive( HttpMethod.POST, "url", requestBody, null, String )

        then: "response entity is returned"
        1 * restTemplate.exchange( _, _, _, _ ) >> responseEntity

        and: "response entity returns status code OK"
        responseEntity.getStatusCode() >> httpStatus
    }

    // ============================================================
    // Inner Class:
    // ============================================================

    private class TestConfigTemplate extends ConfigTemplate
    {
        TestConfigTemplate( final ConfigClientProperties configClientProperties, final RestTemplate restTemplate )
        {
            super( configClientProperties )
            this.restTemplate = restTemplate
        }
    }
}
