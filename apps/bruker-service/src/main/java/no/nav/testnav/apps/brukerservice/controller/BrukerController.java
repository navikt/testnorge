package no.nav.testnav.apps.brukerservice.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import no.nav.testnav.apps.brukerservice.domain.User;
import no.nav.testnav.apps.brukerservice.dto.BrukerDTO;
import no.nav.testnav.apps.brukerservice.exception.JwtIdMismatchException;
import no.nav.testnav.apps.brukerservice.exception.UserAlreadyExistsException;
import no.nav.testnav.apps.brukerservice.exception.UserHasNoAccessToOrgnisasjonException;
import no.nav.testnav.apps.brukerservice.exception.UsernameAlreadyTakenException;
import no.nav.testnav.apps.brukerservice.service.JwtService;
import no.nav.testnav.apps.brukerservice.service.UserService;
import no.nav.testnav.apps.brukerservice.service.ValidateService;
import no.nav.testnav.libs.securitycore.config.UserConstant;

@Slf4j
@RestController
@RequestMapping("/api/v1/brukere")
@RequiredArgsConstructor
public class BrukerController {

    private final ValidateService validateService;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping
    public Mono<ResponseEntity<BrukerDTO>> createBruker(
            @RequestBody BrukerDTO brukerDTO,
            ServerHttpRequest serverHttpRequest
    ) {
        return validateService.validateOrganiasjonsnummerAccess(brukerDTO.organisasjonsnummer())
                .then(userService.create(brukerDTO.brukernavn(), brukerDTO.organisasjonsnummer()))
                .map(User::toDTO)
                .map(dto -> ResponseEntity.created(URI.create(serverHttpRequest.getURI() + "/" + dto.id())).body(dto));
    }

    @GetMapping
    public Mono<ResponseEntity<List<BrukerDTO>>> getBrukere(
            @RequestParam String organisasjonsnummer
    ) {
        return validateService.validateOrganiasjonsnummerAccess(organisasjonsnummer)
                .then(userService.getUserFromOrganisasjonsnummer(organisasjonsnummer))
                .map(User::toDTO)
                .map(Collections::singletonList)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<BrukerDTO>> getBruker(
            @PathVariable String id,
            @RequestHeader(UserConstant.USER_HEADER_JWT) String jwt
    ) {
        return jwtService.verify(jwt, id)
                .then(userService.getUser(id))
                .map(User::toDTO)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PatchMapping("/{id}/brukernavn")
    public Mono<ResponseEntity<String>> oppdaterBrukernavn(
            @PathVariable String id,
            @RequestBody String brukernavn,
            @RequestHeader(UserConstant.USER_HEADER_JWT) String jwt
    ) {
        return jwtService.verify(jwt, id)
                .then(userService.updateUsername(id, brukernavn))
                .map(User::getBrukernavn)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/brukernavn/{brukernavn}")
    public Mono<ResponseEntity<String>> getBrukernavn(
            @PathVariable String brukernavn
    ) {
        return userService.getUserByBrukernavn(brukernavn)
                .map(User::getBrukernavn)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> deleteBruker(
            @PathVariable String id,
            @RequestHeader(UserConstant.USER_HEADER_JWT) String jwt
    ) {
        return jwtService.verify(jwt, id)
                .then(userService.delete(id))
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @PostMapping("/{id}/token")
    public Mono<ResponseEntity<String>> getToken(@PathVariable String id) {
        return userService.getUser(id, true)
                .doOnNext(user -> validateService.validateOrganiasjonsnummerAccess(user.getOrganisasjonsnummer()))
                .flatMap(jwtService::getToken)
                .map(ResponseEntity::ok);
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
