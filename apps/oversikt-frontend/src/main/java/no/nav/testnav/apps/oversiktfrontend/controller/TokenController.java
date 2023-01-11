package no.nav.testnav.apps.oversiktfrontend.controller;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.oversiktfrontend.domain.Application;
import no.nav.testnav.apps.oversiktfrontend.dto.ApplicationDTO;
import no.nav.testnav.apps.oversiktfrontend.dto.TokenDTO;
import no.nav.testnav.apps.oversiktfrontend.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/token")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;


    @PostMapping("/magic-token")
    public ResponseEntity<Mono<TokenDTO>> getMagicToken() {
        var token = tokenService.getMagicToken().map(TokenDTO::new);
        return ResponseEntity.ok(token);
    }

    @PostMapping
    public ResponseEntity<Mono<TokenDTO>> getToken(
            @RequestBody ApplicationDTO applicationDTO
    ) {
        var token = tokenService.getToken(new Application(applicationDTO)).map(TokenDTO::new);
        return ResponseEntity.ok(token);
    }


}
