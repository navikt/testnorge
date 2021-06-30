package no.nav.registre.testnorge.oversiktfrontend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import no.nav.registre.testnorge.oversiktfrontend.config.ApplicationsProperties;
import no.nav.registre.testnorge.oversiktfrontend.dto.TokenDTO;
import no.nav.testnav.libs.security.domain.AccessScopes;
import no.nav.testnav.libs.security.domain.AccessToken;
import no.nav.testnav.libs.security.service.AccessTokenService;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationsController {
    private final ApplicationsProperties properties;
    private final AccessTokenService accessTokenService;

    @GetMapping
    public ResponseEntity<Set<String>> getApplications() {
        return ResponseEntity.ok(properties.getApplications().keySet());
    }

    @GetMapping(value = "{name}/token/on-behalf-of")
    public ResponseEntity<TokenDTO> onBehalfOf(@PathVariable("name") String name) {

        var scope = properties.getApplications().get(name);

        if (scope == null) {
            return ResponseEntity.notFound().build();
        }

        AccessToken accessToken = accessTokenService.generateToken(
                new AccessScopes("api://" + scope + "//.default")
        ).block();

        if (accessToken.getTokenValue() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new TokenDTO(accessToken));
    }
}