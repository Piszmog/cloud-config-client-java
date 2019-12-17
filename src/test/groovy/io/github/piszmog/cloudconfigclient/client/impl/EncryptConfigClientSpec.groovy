package io.github.piszmog.cloudconfigclient.client.impl

import io.github.piszmog.cloudconfig.ConfigException
import io.github.piszmog.cloudconfig.client.impl.EncryptConfigClient
import io.github.piszmog.cloudconfig.template.ConfigTemplate
import org.springframework.http.ResponseEntity
import spock.lang.Specification

/**
 * Unit tests for {@link EncryptConfigClient}
 *
 * Created by Piszmog on 5/6/2018
 */
class EncryptConfigClientSpec extends Specification {
    // ============================================================
    // Class Attributes:
    // ============================================================

    private ConfigTemplate configTemplate

    // ============================================================
    // Setup:
    // ============================================================

    def setup() {
        configTemplate = Mock(ConfigTemplate)
    }

    // ============================================================
    // Tests:
    // ============================================================

    def "encryption is checked is enabled"() {
        given: "an encryptConfigClient"
        def encryptConfigClient = new EncryptConfigClient(configTemplate)

        and: "a response entity"
        ResponseEntity responseEntity = Mock(ResponseEntity)

        when: "encryption is checked"
        def isEnabled = encryptConfigClient.isEncryptionEnabled()

        then: "configTemplate checks if encryption is enabled"
        1 * configTemplate.sendAndReceive(_, _, _, _, _, _) >> responseEntity

        and: "encryption is enabled"
        isEnabled
    }

    def "encryption is checked is enabled but an exception is thrown"() {
        given: "an encryptConfigClient"
        def encryptConfigClient = new EncryptConfigClient(configTemplate)

        when: "encryption is checked"
        encryptConfigClient.isEncryptionEnabled()

        then: "configTemplate checks if encryption is enabled but exception occurs"
        1 * configTemplate.sendAndReceive(_, _, _, _, _, _) >> {
            throw new ConfigException("Error happened")
        }

        and: "the exception is thrown"
        thrown(ConfigException)
    }

    def "a value is encrypted"() {
        given: "an encryptConfigClient"
        def encryptConfigClient = new EncryptConfigClient(configTemplate)

        and: "a value"
        def value = "value"

        and: "a response entity"
        ResponseEntity responseEntity = Mock(ResponseEntity)

        when: "the value is encrypted"
        def encryptedValue = encryptConfigClient.encrypt(value)

        then: "configTemplate sends the value and receives the encrypted value"
        1 * configTemplate.sendAndReceive(_, _, _, _, _, _) >> responseEntity

        and: "the encrypted value is returned"
        1 * responseEntity.getBody() >> "encrypted"
        encryptedValue == "encrypted"
    }

    def "a value is encrypted using an application name"() {
        given: "an encryptConfigClient"
        def encryptConfigClient = new EncryptConfigClient(configTemplate)

        and: "a value"
        def value = "value"

        and: "a response entity"
        ResponseEntity responseEntity = Mock(ResponseEntity)

        when: "the value is encrypted"
        def encryptedValue = encryptConfigClient.encrypt(value, "application name")

        then: "configTemplate sends the value and receives the encrypted value"
        1 * configTemplate.sendAndReceive(_, _, _, _, _, _) >> responseEntity

        and: "the encrypted value is returned"
        1 * responseEntity.getBody() >> "encrypted"
        encryptedValue == "encrypted"
    }

    def "a value is encrypted using an application name and profile"() {
        given: "an encryptConfigClient"
        def encryptConfigClient = new EncryptConfigClient(configTemplate)

        and: "a value"
        def value = "value"

        and: "a response entity"
        ResponseEntity responseEntity = Mock(ResponseEntity)

        when: "the value is encrypted"
        def encryptedValue = encryptConfigClient.encrypt(value, "application name", "profile")

        then: "configTemplate sends the value and receives the encrypted value"
        1 * configTemplate.sendAndReceive(_, _, _, _, _, _) >> responseEntity

        and: "the encrypted value is returned"
        1 * responseEntity.getBody() >> "encrypted"
        encryptedValue == "encrypted"
    }

    def "a value is encrypted but ConfigException is thrown"() {
        given: "an encryptConfigClient"
        def encryptConfigClient = new EncryptConfigClient(configTemplate)

        and: "a value"
        def value = "value"

        when: "the value is encrypted"
        encryptConfigClient.encrypt(value, "application name", "profile")

        then: "configTemplate sends the value but exception is thrown"
        1 * configTemplate.sendAndReceive(_, _, _, _, _, _) >> {
            throw new ConfigException("Error happened")
        }

        and: "the exception is thrown"
        thrown(ConfigException)
    }
}
