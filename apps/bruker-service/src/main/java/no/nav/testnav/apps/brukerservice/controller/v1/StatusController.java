package no.nav.testnav.apps.brukerservice.controller.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.brukerservice.domain.User;
import no.nav.testnav.apps.brukerservice.service.v1.UserService;
import no.nav.testnav.apps.brukerservice.service.v1.ValidateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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
}
