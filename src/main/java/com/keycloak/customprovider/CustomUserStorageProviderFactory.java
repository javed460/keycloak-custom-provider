package com.keycloak.customprovider;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class CustomUserStorageProviderFactory implements UserStorageProviderFactory<CustomUserStorageProvider> {

    @Override
    public String getId() {
        return "custom-rest-provider";
    }

    @Override
    public CustomUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        return new CustomUserStorageProvider(session, model);
    }

    @Override
    public String getHelpText() {
        return "Federates users from an External REST API";
    }
}