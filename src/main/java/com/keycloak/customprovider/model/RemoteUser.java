package com.keycloak.customprovider.model;

import lombok.Data;

@Data
public class RemoteUser {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}