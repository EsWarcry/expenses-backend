package com.alvaro.gastos.service;

import com.alvaro.gastos.controller.ExpenseController;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KeycloakUserService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserService.class);
    private final Keycloak keycloak;
    private final String realm = "app-gastos";

    public KeycloakUserService() {
        this.keycloak = KeycloakBuilder.builder()
                .serverUrl("http://localhost:8082/")
                .realm("master")
                .clientId("admin-cli")
                .username("admin")
                .password("admin")
                .build();
    }

    public String createUser(String username, String email, String firstName, String lastName){
        try {
            UserRepresentation user = new UserRepresentation();
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEnabled(true);

            Response response = keycloak.realm(realm).users().create(user);

            if (response.getStatus() == 201){
                String location = response.getLocation().getPath();
                String keycloakId = location.substring(location.lastIndexOf("/")+1);
                logger.info("Usuario creado en keycloak con ID: {}");
                return keycloakId;
            } else {
                logger.error("Error creando usuario keycloak. status: {}", response.getStatus());
                throw new RuntimeException("Error creando usuario en keycloak. Status"+ response.getStatus());
            }
        } catch (Exception e){
            logger.error("Excepción al crear usuario en Keycloak", e);
            throw new RuntimeException("Error comunicando con Keycloak", e);
        }
    }

    public void setTemporaryPassword(String keycloakId, String password){
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(true);

        UserResource userResource = keycloak.realm("app-gastos").users().get(keycloakId);

        userResource.resetPassword(credential);

        UserRepresentation user = userResource.toRepresentation();
        user.setRequiredActions(Collections.singletonList("UPDATE_PASSWORD"));
        userResource.update(user);

        logger.info("Contraseña temporal asignada y requieredAction para usuario {}", keycloakId);
    }

    public List<String> getAllRoles(){
        RealmResource real = keycloak.realm(realm);
        return real.roles().list().stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList());
    }

    public void assignRoleToUser(String keycloakId, String roleName){
        RealmResource real = keycloak.realm(realm);
        RoleRepresentation role = real.roles().get(roleName).toRepresentation();
        real.users().get(keycloakId).roles().realmLevel().add(Collections.singletonList(role));
    }
}


