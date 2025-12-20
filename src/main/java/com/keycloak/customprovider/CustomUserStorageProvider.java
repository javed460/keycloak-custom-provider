package com.keycloak.customprovider;

import com.keycloak.customprovider.model.RemoteUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@RequiredArgsConstructor
public class CustomUserStorageProvider implements UserStorageProvider, UserLookupProvider, CredentialInputValidator {

    private final KeycloakSession session;
    private final ComponentModel model;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private static final String API_URL = "http://localhost:8082/api/users";

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/" + username))
                    .timeout(java.time.Duration.ofSeconds(10))
                    .GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                RemoteUser remoteUser = mapper.readValue(response.body(), RemoteUser.class);
                return new ExternalUserAdapter(session, realm, model, remoteUser);
            }
        } catch (Exception e) {
            log.error("Failed to fetch user from external service", e);
        }
        return null;
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType())) return false;

        try {
            String body = String.format("{\"username\":\"%s\", \"password\":\"%s\"}",
                    user.getUsername(), input.getChallengeResponse());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/verify"))
                    .timeout(java.time.Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            log.info("Inside isValid statusCode={}", statusCode);
            return statusCode == 200;
        } catch (Exception e) {
            log.error("Inside isValid got exception", e);
            return false;
        }
    }

    @Override
    public void close() {
    }

    @Override
    public boolean supportsCredentialType(String type) {
        return PasswordCredentialModel.TYPE.equals(type);
    }

    @Override
    public boolean isConfiguredFor(RealmModel r, UserModel u, String t) {
        return supportsCredentialType(t);
    }

    @Override
    public UserModel getUserById(RealmModel r, String id) {
        log.info("getUserById: " + id);
        StorageId storageId = new StorageId(id);
        String externalId = storageId.getExternalId();
        log.info("externalId={}", externalId);
        return getUserByUsername(r, externalId);
    }

    @Override
    public UserModel getUserByEmail(RealmModel r, String email) {
        return getUserByUsername(r, email);
    }
}