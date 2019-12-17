package io.github.piszmog.cloudconfigclient.template.impl

import io.github.piszmog.cloudconfig.template.impl.OAuth2ConfigTemplate
import io.pivotal.spring.cloud.config.client.ConfigClientOAuth2Properties
import org.springframework.cloud.config.client.ConfigClientProperties
import org.springframework.core.env.StandardEnvironment
import spock.lang.Specification

/**
 * Unit tests for {@link OAuth2ConfigTemplate}
 *
 * Created by Piszmog on 5/5/2018
 */
class OAuth2ConfigTemplateSpec extends Specification {
    // ============================================================
    // Class Attributes:
    // ============================================================

    private ConfigClientProperties configClientProperties

    // ============================================================
    // Setup:
    // ============================================================

    def setup() {
        configClientProperties = new ConfigClientProperties(new StandardEnvironment())
    }

    // ============================================================
    // Tests:
    // ============================================================

    def "oAuth2ConfigTemplate is initialized"() {
        given: "a oAuth2ProtectedResourceDetails"
        ConfigClientOAuth2Properties configClientOAuth2Properties = new ConfigClientOAuth2Properties()
        configClientOAuth2Properties.accessTokenUri = "http://localhost:8080"
        configClientOAuth2Properties.clientId = "client-id"
        configClientOAuth2Properties.clientSecret = "client-secret"

        and: "a oAuth2ConfigTemplate"
        OAuth2ConfigTemplate oAuth2ConfigTemplate = new OAuth2ConfigTemplate(configClientProperties, configClientOAuth2Properties)

        when: "oAuth2ConfigTemplate is initialized"
        oAuth2ConfigTemplate.init()

        then: "template is properly initialized"
        notThrown(RuntimeException)
    }
}
