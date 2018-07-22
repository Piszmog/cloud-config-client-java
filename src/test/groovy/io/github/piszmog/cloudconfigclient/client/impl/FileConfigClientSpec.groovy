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
        given: "a fileConfigClient"
        def fileConfigClient = new FileConfigClient( configTemplate )

        and: "a response entity"
        ResponseEntity responseEntity = Mock( ResponseEntity )

        when: "the file is retrieved"
        def fileContent = fileConfigClient.getFileFromMaster( "file name", String )

        then: "configTemplate retrieves the file"
        1 * configTemplate.sendAndReceive( _, _, _, _, _, _ ) >> responseEntity

        and: "the file is returned"
        1 * responseEntity.getBody() >> "file content"
        fileContent == "file content"
    }

    def "a file is retrieved using a branch"()
    {
        given: "a fileConfigClient"
        def fileConfigClient = new FileConfigClient( configTemplate )

        and: "a response entity"
        ResponseEntity responseEntity = Mock( ResponseEntity )

        when: "the file is retrieved"
        def fileContent = fileConfigClient.getFileFromBranch( "file name", "branch", String )

        then: "configTemplate retrieves the file"
        1 * configTemplate.sendAndReceive( _, _, _, _, _, _ ) >> responseEntity

        and: "the file is returned"
        1 * responseEntity.getBody() >> "file content"
        fileContent == "file content"
    }

    def "no file name is provided"()
    {
        given: "a fileConfigClient"
        def fileConfigClient = new FileConfigClient( configTemplate )

        when: "the file is retrieved"
        fileConfigClient.getFileFromMaster( null, String )

        then: "IllegalArgumentException is thrown"
        thrown( IllegalArgumentException )
    }

    def "a file is retrieved but an exception occurs"()
    {
        given: "a fileConfigClient"
        def fileConfigClient = new FileConfigClient( configTemplate )

        when: "the file is retrieved"
        fileConfigClient.getFileFromMaster( "file name", String )

        then: "configTemplate retrieves the file but an exception is thrown"
        1 * configTemplate.sendAndReceive( _, _, _, _, _, _ ) >> {
            throw new ConfigException( "Failed" )
        }

        and: "the exception is thrown"
        thrown( ConfigException )
    }

    def "a file is retrieved from null branch"()
    {
        given: "a fileConfigClient"
        def fileConfigClient = new FileConfigClient( configTemplate )

        and: "a response entity"
        ResponseEntity responseEntity = Mock( ResponseEntity )

        when: "the file is retrieved"
        def fileContent = fileConfigClient.getFileFromBranch( "file name", null, String )

        then: "configTemplate retrieves the file"
        1 * configTemplate.sendAndReceive( _, _, _, _, _, _ ) >> responseEntity

        and: "the file is returned"
        1 * responseEntity.getBody() >> "file content"
        fileContent == "file content"

        and: "no label specified in template"
        1 * configTemplate.getLabel(  ) >> null
    }

    def "a file is retrieved from specified branch in specified directory"()
    {
        given: "a fileConfigClient"
        def fileConfigClient = new FileConfigClient( configTemplate )

        and: "a response entity"
        ResponseEntity responseEntity = Mock( ResponseEntity )

        when: "the file is retrieved"
        def fileContent = fileConfigClient.getFileFromBranch( "file name", "label", "directory", String )

        then: "configTemplate retrieves the file"
        1 * configTemplate.sendAndReceive( _, _, _, _, _, _ ) >> responseEntity

        and: "the file is returned"
        1 * responseEntity.getBody() >> "file content"
        fileContent == "file content"
    }

    def "a file is retrieved from default branch"()
    {
        given: "a fileConfigClient"
        def fileConfigClient = new FileConfigClient( configTemplate )

        and: "a response entity"
        ResponseEntity responseEntity = Mock( ResponseEntity )

        when: "the file is retrieved"
        def fileContent = fileConfigClient.getFileFromDefaultBranch( "file name", "directory", String )

        then: "configTemplate retrieves the file"
        1 * configTemplate.sendAndReceive( _, _, _, _, _, _ ) >> responseEntity

        and: "the file is returned"
        1 * responseEntity.getBody() >> "file content"
        fileContent == "file content"
    }

    def "a file is not retrieved from default branch when file is not specified"()
    {
        given: "a fileConfigClient"
        def fileConfigClient = new FileConfigClient( configTemplate )

        and: "a response entity"
        ResponseEntity responseEntity = Mock( ResponseEntity )

        when: "the file is retrieved"
        fileConfigClient.getFileFromDefaultBranch( null, "directory", String )

        then: "configTemplate never attempts to retrieve file"
        0 * configTemplate.sendAndReceive( _, _, _, _, _, _ ) >> responseEntity

        and: "an exception is thrown"
        thrown( IllegalArgumentException )
    }

    def "a file is not retrieved from default branch when directory is not specified"()
    {
        given: "a fileConfigClient"
        def fileConfigClient = new FileConfigClient( configTemplate )

        and: "a response entity"
        ResponseEntity responseEntity = Mock( ResponseEntity )

        when: "the file is retrieved"
        fileConfigClient.getFileFromDefaultBranch( "file", null, String )

        then: "configTemplate never attempts to retrieve file"
        0 * configTemplate.sendAndReceive( _, _, _, _, _, _ ) >> responseEntity

        and: "an exception is thrown"
        thrown( IllegalArgumentException )
    }
}
