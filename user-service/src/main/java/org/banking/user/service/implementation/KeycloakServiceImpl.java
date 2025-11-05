package org.banking.user.service.implementation;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import org.banking.user.config.KeyCloakManager;
import org.banking.user.service.KeycloakService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {

    private final KeyCloakManager keyCloakManager;

    @Override
    public Integer createUser(UserRepresentation userRepresentation){
        return keyCloakManager.getKeyCloakInstanceWithRealm().users().create(userRepresentation).getStatus();
    }

    @Override 
    public List<UserRepresentation> readUserByEmail(String emailId){
        return keyCloakManager.getKeyCloakInstanceWithRealm().users().search(emailId);
    }

    @Override
    public List<UserRepresentation> readUsers(List<String> authIds){
        
        return authIds.stream().map(authId -> {
            UserResource UserResource = keyCloakManager.getKeyCloakInstanceWithRealm().users().get(authId);
            return UserResource.toRepresentation();
        }).collect(Collectors.toList());
    }

    @Override
    public UserRepresentation readUser(String authId) {

        UsersResource userResource = keyCloakManager.getKeyCloakInstanceWithRealm().users();

        return userResource.get(authId).toRepresentation();
    }

    @Override
    public void updateUser(UserRepresentation userRepresentation){
        keyCloakManager.getKeyCloakInstanceWithRealm().users().get(userRepresentation.getId()).update(userRepresentation);
    }
    

}
