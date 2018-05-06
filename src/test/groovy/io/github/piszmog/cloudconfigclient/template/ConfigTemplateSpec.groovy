package io.github.piszmog.cloudconfigclient.template

import io.github.piszmog.cloudconfig.template.ConfigTemplate
import org.springframework.cloud.config.client.ConfigClientProperties
import org.springframework.core.env.StandardEnvironment
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
        testConfigTemplate.sendAndReceive( "url", HttpMethod.GET, null, null, String )

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
