package no.nav.testnav.dollyfasade.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.dollyfasade.consumers.PdlPersonConsumer;
import no.nav.testnav.libs.dto.personservice.v1.PdlMiljoer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.util.List;

@RestController
@RequestMapping("/api/v2/personer")
@RequiredArgsConstructor
public class PdlPersonController {

    private final PdlPersonConsumer pdlPersonConsumer;

    @GetMapping("/ident/{ident}")
    @Operation(description = "Henter PDL-person angitt ved ident og miljø, Q2 eller Q1")
    public Mono<JsonNode> getPdlPersonMiljoe(@PathVariable("ident") String ident,
                              @RequestParam(value = "pdlMiljoe", required = false, defaultValue = "Q2") PdlMiljoer
                                      pdlMiljoe) {
        return pdlPersonConsumer.getPdlPerson(ident, pdlMiljoe);
    }

    @GetMapping("/identer")
    @Operation(description = "Henter flere PDL-personer i hht liste av identer, fra PDL i Q2")
    public Flux<JsonNode> getPdlPersoner(@RequestParam("identer") List<String> identer) {

        return pdlPersonConsumer.getPdlPersoner(identer);
    }
}
