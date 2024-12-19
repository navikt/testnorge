package no.nav.testnav.altinn3tilgangservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.altinn3tilgangservice.domain.PersonRequest;
import no.nav.testnav.altinn3tilgangservice.service.AltinnBrukerTilgangService;
import no.nav.testnav.libs.dto.altinn3.v1.OrganisasjonDTO;
import no.nav.testnav.libs.dto.altinn3.v1.PersonDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/brukertilgang")
@RequiredArgsConstructor
public class AltinnBrukerTilgangController {

    private final AltinnBrukerTilgangService brukerTilgangService;

    @PostMapping
    public Flux<OrganisasjonDTO> getPersonOrganisasjonTilgang(@RequestBody PersonRequest request) {

        return brukerTilgangService.getPersonOrganisasjonTilgang(request.getIdent());
    }

    @PostMapping("/detaljert")
    public Mono<PersonDTO> getPersonOrganisasjonDetaljertTilgang(@RequestBody PersonRequest request) {

        return brukerTilgangService.getPersonOrganisasjonDetaljertTilgang(request.getIdent());
    }
}
