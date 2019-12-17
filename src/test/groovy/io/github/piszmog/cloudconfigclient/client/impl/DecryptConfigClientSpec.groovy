package io.github.piszmog.cloudconfigclient.client.impl

import io.github.piszmog.cloudconfig.ConfigException
import io.github.piszmog.cloudconfig.client.impl.DecryptConfigClient
import io.github.piszmog.cloudconfig.template.ConfigTemplate
import org.springframework.http.ResponseEntity
import spock.lang.Specification

/**
 * Unit tests for {@link DecryptConfigClient}
 *
 * Created by Piszmog on 5/6/2018
 */
class DecryptConfigClientSpec extends Specification {
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

    def "an encrypted value is decrypted"() {
        given: "a decryptConfigClient"
        def decryptConfigClient = new DecryptConfigClient(configTemplate)

        and: "an encrypted value"
        def value = "encrypted"

        and: "a response entity"
        ResponseEntity responseEntity = Mock(ResponseEntity)

        when: "the value is decrypted"
        def decryptedValue = decryptConfigClient.decrypt(value)

        then: "configTemplate sends the encrypted value and receives the decrypted value"
        1 * configTemplate.sendAndReceive(_, _, _, _, _, _) >> responseEntity

        and: "the value is returned"
        1 * responseEntity.getBody() >> "value"
        decryptedValue == "value"
    }

    def "an encrypted value is decrypted using an application name"() {
        given: "a decryptConfigClient"
        def decryptConfigClient = new DecryptConfigClient(configTemplate)

        and: "an encrypted value"
        def value = "encrypted"

        and: "a response entity"
        ResponseEntity responseEntity = Mock(ResponseEntity)

        when: "the value is decrypted"
        def decryptedValue = decryptConfigClient.decrypt(value, "application name")

        then: "configTemplate sends the encrypted value and receives the decrypted value"
        1 * configTemplate.sendAndReceive(_, _, _, _, _, _) >> responseEntity

        and: "the value is returned"
        1 * responseEntity.getBody() >> "value"
        decryptedValue == "value"
    }

    def "an encrypted value is decrypted using an application name and profile"() {
        given: "a decryptConfigClient"
        def decryptConfigClient = new DecryptConfigClient(configTemplate)

        and: "an encrypted value"
        def value = "encrypted"

        and: "a response entity"
        ResponseEntity responseEntity = Mock(ResponseEntity)

        when: "the value is decrypted"
        def decryptedValue = decryptConfigClient.decrypt(value, "application name", "profile")

        then: "configTemplate sends the encrypted value and receives the decrypted value"
        1 * configTemplate.sendAndReceive(_, _, _, _, _, _) >> responseEntity

        and: "the value is returned"
        1 * responseEntity.getBody() >> "value"
        decryptedValue == "value"
    }

    def "an encrypted value is decrypted but ConfigException is thrown"() {
        given: "a decryptConfigClient"
        def decryptConfigClient = new DecryptConfigClient(configTemplate)

        and: "an encrypted value"
        def value = "encrypted"

        when: "the value is decrypted"
        decryptConfigClient.decrypt(value, "application name", "profile")

        then: "configTemplate sends the encrypted value but exception is thrown"
        1 * configTemplate.sendAndReceive(_, _, _, _, _, _) >> {
            throw new ConfigException("Error happened")
        }

        and: "the exception is thrown"
        thrown(ConfigException)
    }
}
