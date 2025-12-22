package com.keycloak.customprovider;

import com.keycloak.customprovider.model.RemoteUser;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.*;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ExternalUserAdapter extends AbstractUserAdapter {
    private final RemoteUser remoteUser;

    public ExternalUserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, RemoteUser remoteUser) {
        super(session, realm, model);
        this.remoteUser = remoteUser;
        this.storageId = new StorageId(model.getId(), remoteUser.getUsername());
    }

    @Override
    public String getUsername() {
        return remoteUser.getUsername();
    }

    @Override
    public String getFirstName() {
        return remoteUser.getFirstName();
    }

    @Override
    public String getLastName() {
        return remoteUser.getLastName();
    }

    @Override
    public String getEmail() {
        return remoteUser.getEmail();
    }

    // Standard boiler-plate for read-only adapters
    @Override
    public Stream<String> getAttributeStream(String name) {
        if (name.equals(UserModel.EMAIL)) {
            return Stream.of(remoteUser.getEmail());
        } else if (name.equals(UserModel.FIRST_NAME)) {
            return Stream.of(remoteUser.getFirstName());
        } else if (name.equals(UserModel.LAST_NAME)) {
            return Stream.of(remoteUser.getLastName());
        }
        return super.getAttributeStream(name);
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        Map<String, List<String>> attrs = new HashMap<>();
        attrs.put(UserModel.EMAIL, Collections.singletonList(getEmail()));
        attrs.put(UserModel.FIRST_NAME, Collections.singletonList(getFirstName()));
        attrs.put(UserModel.LAST_NAME, Collections.singletonList(getLastName()));
        return attrs;
    }

    @Override
    public Stream<String> getRequiredActionsStream() {
        return Stream.empty();
    }

    @Override
    public void addRequiredAction(String action) {
    }

    @Override
    public void removeRequiredAction(String action) {
    }

    @Override
    public Stream<org.keycloak.models.GroupModel> getGroupsStream() {
        return Stream.empty();
    }

    @Override
    public Stream<GroupModel> getGroupsStream(String search, Integer first, Integer max) {
        return super.getGroupsStream(search, first, max);
    }

    @Override
    public long getGroupsCount() {
        return super.getGroupsCount();
    }

    @Override
    public long getGroupsCountByNameContaining(String search) {
        return super.getGroupsCountByNameContaining(search);
    }

    @Override
    public void joinGroup(GroupModel group, MembershipMetadata metadata) {
        super.joinGroup(group, metadata);
    }

    @Override
    public boolean isFederated() {
        return super.isFederated();
    }

    @Override
    public SubjectCredentialManager credentialManager() {
        return new org.keycloak.credential.UserCredentialManager(session, realm, this);
    }

    @Override
    public boolean hasDirectRole(RoleModel role) {
        return super.hasDirectRole(role);
    }

    @Override
    public Stream<org.keycloak.models.RoleModel> getRoleMappingsStream() {
        return Stream.empty();
    }

    @Override
    public void setUsername(String username) {

    }
}