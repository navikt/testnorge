package no.nav.testnav.dollysearchservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.dollysearchservice.service.IdentService;
import no.nav.testnav.libs.data.dollysearchservice.v1.IdentdataDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/identer")
@RequiredArgsConstructor
public class IdentSearchController {

    private final IdentService identService;

    @GetMapping
    @Operation(description = "Henter testnorge-identer som matcher søk i request")
    public Flux<IdentdataDTO> getIdenter(String fragment) {

        return identService.getIdenter(fragment);
    }
}