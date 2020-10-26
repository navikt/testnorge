package no.nav.registre.testnorge.identservice.service.authorization;

import no.nav.registre.testnorge.identservice.testdata.servicerutiner.authorization.UserContextHolder;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.authorization.UserRole;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of the UserContextHolder interface using spring security
 */

@Service
public class DefaultUserContextHolder implements UserContextHolder {

    private static final String ANONYMOUS_USER = "anonymousUser";

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        Authentication authentication = getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    @Override
    public User getUser() {
        Authentication authentication = getAuthentication();
        return authentication == null || ANONYMOUS_USER.equals(authentication.getPrincipal()) ?
                new User(ANONYMOUS_USER, ANONYMOUS_USER) :
                new User(getDisplayName(), getUsername());
    }

    @Override
    public Set<UserRole> getRoles() {
        Authentication authentication = getAuthentication();
        return authentication != null && authentication.getAuthorities() != null ?
                new HashSet<>((Collection<UserRole>) authentication.getAuthorities()) : null;
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext() != null ?
                SecurityContextHolder.getContext().getAuthentication() : null;
    }
}
