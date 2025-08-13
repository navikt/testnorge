package no.nav.testnav.apps.brukerservice.controller.v2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.brukerservice.domain.User;
import no.nav.testnav.apps.brukerservice.dto.BrukerDTO;
import no.nav.testnav.apps.brukerservice.service.v1.JwtService;
import no.nav.testnav.apps.brukerservice.service.v1.ValidateService;
import no.nav.testnav.apps.brukerservice.service.v2.UserService;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController("brukerControllerV2")
@RequestMapping("/api/v2/brukere")
@RequiredArgsConstructor
public class BrukerController {

    private final ValidateService validateService;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping
    public Mono<BrukerDTO> createBruker(
            @RequestBody BrukerDTO brukerDTO
    ) {
        return validateService.validateOrganiasjonsnummerAccess(brukerDTO.organisasjonsnummer())
                .then(userService.create(brukerDTO))
                .map(User::toDTO);
    }

    @GetMapping
    public Mono<List<BrukerDTO>> getBrukere(
            @RequestParam String organisasjonsnummer
    ) {
        return validateService.validateOrganiasjonsnummerAccess(organisasjonsnummer)
                .then(userService.getUserFromOrganisasjonsnummer(organisasjonsnummer))
                .map(User::toDTO)
                .map(Collections::singletonList);
    }

    @GetMapping("/{id}")
    public Mono<BrukerDTO> getBruker(
            @PathVariable String id,
            @RequestHeader(UserConstant.USER_HEADER_JWT) String jwt
    ) {
        return jwtService.verify(jwt, id)
                .then(userService.getUser(id))
                .map(User::toDTO);
    }

    @PutMapping()
    public Mono<BrukerDTO> updateBruker(
            @RequestBody BrukerDTO bruker
    ) {
        return validateService.validateOrganiasjonsnummerAccess(bruker.organisasjonsnummer())
                .then(userService.updateUser(bruker))
                .map(User::toDTO);
    }

    @GetMapping("/brukernavn/{brukernavn}")
    public Mono<String> getBrukernavn(
            @PathVariable String brukernavn
    ) {
        return userService.getUserByBrukernavn(brukernavn)
                .map(User::getBrukernavn);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteBruker(
            @PathVariable String id,
            @RequestHeader(UserConstant.USER_HEADER_JWT) String jwt
    ) {
        return jwtService.verify(jwt, id)
                .then(userService.delete(id));
    }

    @PostMapping("/{id}/token")
    public Mono<String> getToken(@PathVariable String id) {
        return userService.getUser(id, true)
                .doOnNext(user -> validateService.validateOrganiasjonsnummerAccess(user.getOrganisasjonsnummer()))
                .flatMap(jwtService::getToken);
    }
}
