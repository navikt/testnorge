package no.nav.testnav.apps.brukerservice.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.brukerservice.domain.User;
import no.nav.testnav.apps.brukerservice.exception.JwtIdMismatchException;
import no.nav.testnav.apps.brukerservice.exception.UserAlreadyExistsException;
import no.nav.testnav.apps.brukerservice.exception.UserHasNoAccessToOrgnisasjonException;
import no.nav.testnav.apps.brukerservice.exception.UsernameAlreadyTakenException;
import no.nav.testnav.apps.brukerservice.service.UserService;
import no.nav.testnav.apps.brukerservice.service.ValidateService;

@Slf4j
@RestController
@RequestMapping("/api/v1/status")
@RequiredArgsConstructor
public class StatusController {

    private final ValidateService validateService;
    private final UserService userService;


    @GetMapping("/organisasjon/issame/{brukernavn1}/{brukernavn2}")
    public Mono<ResponseEntity<Boolean>> getBrukernavnOrgComparison(
            @PathVariable String brukernavn1,
            @PathVariable String brukernavn2
    ) {
        return userService.getUserByBrukernavn(brukernavn1)
                .map(User::getOrganisasjonsnummer)
                .flatMap(org1 -> validateService.validateOrganiasjonsnummerAccess(org1).then(Mono.just(org1)))
                .flatMap(org1 -> userService.getUserByBrukernavn(brukernavn2)
                        .map(User::getOrganisasjonsnummer)
                        .flatMap(org2 -> Mono.just(org1.equals(org2))))
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @ExceptionHandler({
            UserAlreadyExistsException.class,
            UsernameAlreadyTakenException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public String conflictHandler(Exception e) {
        log.trace("CONFLICT: {}", e.getMessage());
        return e.getMessage();
    }

    @ExceptionHandler(TokenExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String tokenExpiredHandler(Exception e) {
        log.trace("UNAUTHORIZED: {}", e.getMessage());
        return e.getMessage();
    }

    @ExceptionHandler({
            JWTVerificationException.class,
            JwtIdMismatchException.class,
            UserHasNoAccessToOrgnisasjonException.class
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String forbiddenHandler(Exception e) {
        log.trace("FORBIDDEN: {}", e.getMessage());
        return e.getMessage();
    }
}
