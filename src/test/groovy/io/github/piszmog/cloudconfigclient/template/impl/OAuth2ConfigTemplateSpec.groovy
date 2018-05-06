package io.github.piszmog.cloudconfigclient.template.impl

import io.github.piszmog.cloudconfig.template.impl.OAuth2ConfigTemplate
import org.springframework.cloud.config.client.ConfigClientProperties
import org.springframework.core.env.StandardEnvironment
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails
import spock.lang.Specification

/**
 * Created by Piszmog on 5/5/2018
 */
class OAuth2ConfigTemplateSpec extends Specification
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

    def "oAuth2ConfigTemplate is initialized"()
    {
        given: "a oAuth2ProtectedResourceDetails"
        OAuth2ProtectedResourceDetails oAuth2ProtectedResourceDetails = Mock( OAuth2ProtectedResourceDetails )

        and: "a oAuth2ConfigTemplate"
        OAuth2ConfigTemplate oAuth2ConfigTemplate = new OAuth2ConfigTemplate( configClientProperties, oAuth2ProtectedResourceDetails )

        when: "oAuth2ConfigTemplate is initialized"
        oAuth2ConfigTemplate.init()

        then: "template is properly initialized"
        notThrown( RuntimeException )
    }
}
