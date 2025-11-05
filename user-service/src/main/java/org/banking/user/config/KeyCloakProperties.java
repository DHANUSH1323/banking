package org.banking.user.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KeyCloakProperties {

    @Value("${app.config.keycloak.server-url}")
    private String serverUrl;

    @Value("${app.config.keycloak.realm}")
    private String realm;

    @Value("${app.config.keycloak.client-id}")
    private String clientId;

    @Value("${app.config.keycloak.client-secret}")
    private String clientSecret;

    private static Keycloak keycloakInstance = null;

    /**
     * Returns an instance of Keycloak. If the instance is null, it creates a
     * new instance using the provided configuration.
     *
     * @return The Keycloak instance
     */
    public Keycloak getKeycloakInstance() {

        if (keycloakInstance == null) {
            keycloakInstance = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .grantType("client_credentials")
                    .build();
        }

        return keycloakInstance;
    }

    /**
     * Returns the realm.
     *
     * @return the realm
     */
    public String getRealm() {
        return realm;
    }

}
