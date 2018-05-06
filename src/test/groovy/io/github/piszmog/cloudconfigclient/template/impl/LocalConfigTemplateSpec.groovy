package io.github.piszmog.cloudconfigclient.template.impl

import io.github.piszmog.cloudconfig.template.impl.LocalConfigTemplate
import org.springframework.cloud.config.client.ConfigClientProperties
import org.springframework.core.env.StandardEnvironment
import spock.lang.Specification

/**
 * Unit tests for {@link LocalConfigTemplate}
 *
 * Created by Piszmog on 5/5/2018
 */
class LocalConfigTemplateSpec extends Specification
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

    def "localConfigTemplate is initialized"()
    {
        given: "a localConfigTemplate"
        LocalConfigTemplate localConfigTemplate = new LocalConfigTemplate( configClientProperties )

        when: "localConfigTemplate is initialized"
        localConfigTemplate.init()

        then: "template is properly initialized"
        notThrown( RuntimeException )
    }

    def "localConfigTemplate is initialized when username and password are null"()
    {
        given: "configClientProperties"
        ConfigClientProperties mockConfigClientProperties = Mock( ConfigClientProperties )

        and: "a localConfigTemplate"
        LocalConfigTemplate localConfigTemplate = new LocalConfigTemplate( mockConfigClientProperties, 1 )

        when: "localConfigTemplate is initialized"
        localConfigTemplate.init()

        then: "password is not null"
        mockConfigClientProperties.getPassword() >> "password"

        and: "authorization is not null"
        mockConfigClientProperties.getAuthorization() >> "authorization"

        and: "there are headers"
        mockConfigClientProperties.getHeaders() >> new HashMap<String, String>()

        and: "template is unable to be initialized"
        thrown( RuntimeException )
    }

    def "localConfigTemplate is initialized when username and password are not null"()
    {
        given: "configClientProperties"
        ConfigClientProperties mockConfigClientProperties = Mock( ConfigClientProperties )

        and: "a localConfigTemplate"
        LocalConfigTemplate localConfigTemplate = new LocalConfigTemplate( mockConfigClientProperties )

        when: "localConfigTemplate is initialized"
        localConfigTemplate.init()

        then: "username and password are valid"
        mockConfigClientProperties.getUsername() >> "username"
        mockConfigClientProperties.getPassword() >> "password"

        and: "there are headers"
        mockConfigClientProperties.getHeaders() >> new HashMap<String, String>()

        and: "template is properly initialized"
        notThrown( RuntimeException )
    }

    def "localConfigTemplate is initialized when authorization is used"()
    {
        given: "configClientProperties"
        ConfigClientProperties mockConfigClientProperties = Mock( ConfigClientProperties )

        and: "a localConfigTemplate"
        LocalConfigTemplate localConfigTemplate = new LocalConfigTemplate( mockConfigClientProperties )

        when: "localConfigTemplate is initialized"
        localConfigTemplate.init()

        then: "username and password are valid"
        mockConfigClientProperties.getAuthorization() >> "authorization"

        and: "there are headers"
        mockConfigClientProperties.getHeaders() >> new HashMap<String, String>()

        and: "template is properly initialized"
        notThrown( RuntimeException )
    }
}
