package no.nav.testnav.dollysearchservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.dollysearchservice.service.LegacyService;
import no.nav.testnav.libs.data.dollysearchservice.v1.PersonDTO;
import no.nav.testnav.libs.data.dollysearchservice.v1.PersonSearch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Deprecated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/legacy")
public class LegacyController {

    private final LegacyService legacyService;

    @PostMapping
    @Operation(description = "Søker opp Testnorge-personer som ikke finnes i Dolly.")
    public Flux<PersonDTO> searchPersoner(@RequestBody PersonSearch personSearch) {

        return legacyService.searchPersoner(personSearch);
    }
}