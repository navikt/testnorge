package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.util.CurrentAuthentication;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import org.springframework.stereotype.Service;

/**
 * Enkel service for å slå opp innlogget bruker, for convenience.
 */
@Service
@RequiredArgsConstructor
public class AutentisertBrukerService {

    private final GetUserInfo userInfo;

    public Bruker get() {
        return CurrentAuthentication.getAuthUser(userInfo);
    }

}
