package com.alvaro.gastos.repository;

import com.alvaro.gastos.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Encuentra un usuario por su keycloakId.
     * Este método es crucial para la integración con Keycloak, ya que nos permite
     * encontrar nuestro usuario interno basándonos en el ID que Keycloak le asigna.
     *
     * @param keycloakId El ID único del usuario en Keycloak (UUID).
     * @return Un Optional que contiene el User si se encuentra, o un Optional vacío.
     */
    Optional<User> findByKeycloakId(String keycloadId);

    Optional<User> findByUsername(String name);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    @Query("SELECT u FROM User u " +
            " WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                " OR   LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
                " OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))"
    )
    List<User> searchUsers(@Param("keyword") String keyword);

}
