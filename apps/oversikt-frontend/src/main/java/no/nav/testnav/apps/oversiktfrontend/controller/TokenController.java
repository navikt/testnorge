package no.nav.testnav.apps.oversiktfrontend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.oversiktfrontend.domain.Application;
import no.nav.testnav.apps.oversiktfrontend.dto.ApplicationDTO;
import no.nav.testnav.apps.oversiktfrontend.dto.TokenDTO;
import no.nav.testnav.apps.oversiktfrontend.service.TokenService;

@RestController
@RequestMapping("/api/v1/token")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;


    @PostMapping("/magic-token")
    public ResponseEntity<Mono<TokenDTO>> getMagicToken(ServerWebExchange exchange) {
        var token = tokenService.getMagicToken(exchange).map(TokenDTO::new);
        return ResponseEntity.ok(token);
    }

    @PostMapping
    public ResponseEntity<Mono<TokenDTO>> getToken(
            @RequestBody ApplicationDTO applicationDTO,
            @RequestParam(required = false, defaultValue = "false") Boolean clientCredentials,
            ServerWebExchange exchange
    ) {
        var token = tokenService.getToken(new Application(applicationDTO), clientCredentials, exchange).map(TokenDTO::new);
        return ResponseEntity.ok(token);
    }


}
