package io.github.piszmog.cloudconfigclient.client.impl

import io.github.piszmog.cloudconfig.ConfigException
import io.github.piszmog.cloudconfig.client.impl.FileConfigClient
import io.github.piszmog.cloudconfig.template.ConfigTemplate
import org.springframework.http.ResponseEntity
import spock.lang.Specification

/**
 * Unit tests for
 *
 * Created by Piszmog on 5/6/2018
 */
class FileConfigClientSpec extends Specification
{
    // ============================================================
    // Class Attributes:
    // ============================================================

    private ConfigTemplate configTemplate

    // ============================================================
    // Setup:
    // ============================================================

    def setup()
    {
        configTemplate = Mock( ConfigTemplate )
    }

    // ============================================================
    // Tests:
    // ============================================================

    def "a file is retrieved"()
    {
        given: "a decryptConfigClient"
        def fileConfigClient = new FileConfigClient( configTemplate )

        and: "a response entity"
        ResponseEntity responseEntity = Mock( ResponseEntity )

        when: "the file is retrieved"
        def fileContent = fileConfigClient.getFile( "file name", String )

        then: "configTemplate retrieves the file"
        1 * configTemplate.sendAndReceive( _, _, _, _, _, _ ) >> responseEntity

        and: "the file is returned"
        1 * responseEntity.getBody() >> "file content"
        fileContent == "file content"
    }

    def "a file is retrieved using a branch"()
    {
        given: "a decryptConfigClient"
        def fileConfigClient = new FileConfigClient( configTemplate )

        and: "a response entity"
        ResponseEntity responseEntity = Mock( ResponseEntity )

        when: "the file is retrieved"
        def fileContent = fileConfigClient.getFile( "file name", "branch", String )

        then: "configTemplate retrieves the file"
        1 * configTemplate.sendAndReceive( _, _, _, _, _, _ ) >> responseEntity

        and: "the file is returned"
        1 * responseEntity.getBody() >> "file content"
        fileContent == "file content"
    }

    def "no file name is provided"()
    {
        given: "a decryptConfigClient"
        def fileConfigClient = new FileConfigClient( configTemplate )

        when: "the file is retrieved"
        fileConfigClient.getFile( null, String )

        then: "IllegalArgumentException is thrown"
        thrown( IllegalArgumentException )
    }

    def "a file is retrieved but an exception occurs"()
    {
        given: "a decryptConfigClient"
        def fileConfigClient = new FileConfigClient( configTemplate )

        when: "the file is retrieved"
        fileConfigClient.getFile( "file name", String )

        then: "configTemplate retrieves the file but an exception is thrown"
        1 * configTemplate.sendAndReceive( _, _, _, _, _, _ ) >> {
            throw new ConfigException( "Failed" )
        }

        and: "the exception is thrown"
        thrown( ConfigException )
    }
}
