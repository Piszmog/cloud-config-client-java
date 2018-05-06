package io.github.piszmog.cloudconfigclient.client.impl

import io.github.piszmog.cloudconfig.ConfigException
import io.github.piszmog.cloudconfig.client.impl.PublicKeyClient
import io.github.piszmog.cloudconfig.template.ConfigTemplate
import org.springframework.http.ResponseEntity
import spock.lang.Specification

/**
 * Unit tests for {@link PublicKeyClient}
 *
 * Created by Piszmog on 5/6/2018
 */
class PublicKeyClientSpec extends Specification
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

    def "the public key is retrieved"()
    {
        given: "a publicKeyClient"
        def publicKeyClient = new PublicKeyClient( configTemplate )

        and: "a response entity"
        ResponseEntity responseEntity = Mock( ResponseEntity )

        when: "the public key is retrieved"
        def publicKey = publicKeyClient.getPublicKey()

        then: "configTemplate retrieves the public key"
        1 * configTemplate.sendAndReceive( _, _, _, _, _, _ ) >> responseEntity

        and: "the public key is returned"
        1 * responseEntity.getBody() >> "public key"
        publicKey == "public key"
    }

    def "the public key is retrieved using application name"()
    {
        given: "a publicKeyClient"
        def publicKeyClient = new PublicKeyClient( configTemplate )

        and: "a response entity"
        ResponseEntity responseEntity = Mock( ResponseEntity )

        when: "the public key is retrieved"
        def publicKey = publicKeyClient.getPublicKey( "application name" )

        then: "configTemplate retrieves the public key"
        1 * configTemplate.sendAndReceive( _, _, _, _, _, _ ) >> responseEntity

        and: "the public key is returned"
        1 * responseEntity.getBody() >> "public key"
        publicKey == "public key"
    }

    def "the public key is retrieved using application name and profile"()
    {
        given: "a publicKeyClient"
        def publicKeyClient = new PublicKeyClient( configTemplate )

        and: "a response entity"
        ResponseEntity responseEntity = Mock( ResponseEntity )

        when: "the public key is retrieved"
        def publicKey = publicKeyClient.getPublicKey( "application name", "profile" )

        then: "configTemplate retrieves the public key"
        1 * configTemplate.sendAndReceive( _, _, _, _, _, _ ) >> responseEntity

        and: "the public key is returned"
        1 * responseEntity.getBody() >> "public key"
        publicKey == "public key"
    }

    def "the public key is retrieved using application name and profile but an exception is thrown"()
    {
        given: "a publicKeyClient"
        def publicKeyClient = new PublicKeyClient( configTemplate )

        when: "the public key is retrieved"
        publicKeyClient.getPublicKey( "application name", "profile" )

        then: "configTemplate retrieves the public key but an exception occurs"
        1 * configTemplate.sendAndReceive( _, _, _, _, _, _ ) >> {
            throw new ConfigException( "Error happened" )
        }

        and: "the exception is thrown"
        thrown( ConfigException )
    }
}
