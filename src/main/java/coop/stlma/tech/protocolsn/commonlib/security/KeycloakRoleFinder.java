package coop.stlma.tech.protocolsn.commonlib.security;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.security.token.DefaultRolesFinder;
import io.micronaut.security.token.RolesFinder;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Map user roles from a default Keycloak JWT.
 *
 * @author John Meyerin
 */
@Singleton
@Replaces(DefaultRolesFinder.class)
@Slf4j
public class KeycloakRoleFinder implements RolesFinder {

    /**
     * Find the roles for the user as saved in a keycloak token
     * @param attributes    keycloak token attributes
     * @return              list of roles
     */
    @Override
    public List<String> resolveRoles(Map<String, Object> attributes) {
        log.debug("Resolving roles from Keycloak attributes: {}", attributes);
        //We don't necessarily know what types of Objects micronaut will render the attribute as, so this should be
        //implemented in a safer way. That is outside the scope of this spike and will be future John's problem.
        Map<String, Object> realmAccess = (Map<String, Object>) attributes.get("realm_access");
        if (realmAccess == null) {
            return Collections.emptyList();
        } else {
            log.debug("Keycloak roles: {}", realmAccess.get("roles"));
            return (List<String>) realmAccess.get("roles");
        }
    }
}
