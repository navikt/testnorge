package no.nav.registre.testnorge.identservice.testdata.servicerutiner.authorization;


import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.User;

import java.util.Set;

/**
 * Abstraction of the user context for testability and reusability
 */

public interface UserContextHolder {
    String getDisplayName();
    String getUsername();

    boolean isAuthenticated();

    User getUser();

    Set<UserRole> getRoles();

}
