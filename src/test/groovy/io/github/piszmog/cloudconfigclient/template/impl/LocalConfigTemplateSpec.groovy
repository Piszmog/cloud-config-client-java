package io.github.piszmog.cloudconfigclient.template.impl

import io.github.piszmog.cloudconfig.template.impl.LocalConfigTemplate
import org.springframework.cloud.config.client.ConfigClientProperties
import org.springframework.core.env.StandardEnvironment
import org.springframework.http.HttpMethod
import spock.lang.Specification

/**
 * Unit tests for {@link LocalConfigTemplate}
 *
 * Created by Piszmog on 5/5/2018
 */
class LocalConfigTemplateSpec extends Specification
{
    private ConfigClientProperties configClientProperties

    def setup()
    {
        configClientProperties = new ConfigClientProperties(new StandardEnvironment())
    }

    def "test"()
    {
        given: "a localConfigTemplate with default read timeout"
        def localConfigTemplate = new LocalConfigTemplate(configClientProperties)

        when: "localConfigTemplate is initialized"
        localConfigTemplate.init()

        and: "localConfigTemplate sends and receives to a url path"
        localConfigTemplate.sendAndReceive("url path", HttpMethod.GET, null, null, String)

        then:
        1 == 1
    }
}
