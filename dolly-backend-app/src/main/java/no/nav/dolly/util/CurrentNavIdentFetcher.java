package no.nav.dolly.util;

import org.springframework.security.core.context.SecurityContextHolder;

import no.nav.dolly.security.sts.OidcTokenAuthentication;

public final class CurrentNavIdentFetcher {

    private CurrentNavIdentFetcher() {
    }

    public static String getLoggedInNavIdent() {
        OidcTokenAuthentication auth = (OidcTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return auth.getPrincipal().toUpperCase();
    }
}