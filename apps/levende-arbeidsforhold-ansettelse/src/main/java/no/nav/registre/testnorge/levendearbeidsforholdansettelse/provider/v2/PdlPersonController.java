package no.nav.registre.testnorge.levendearbeidsforholdansettelse.provider.v2;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v2/personer")
@RequiredArgsConstructor
public class PdlPersonController {

/*
    private final PdlPersonConsumer pdlPersonConsumer;

    @GetMapping("/ident/{ident}")
    @Operation(description = "Henter PDL-person angitt ved ident fra PDL-API / PDL-API-Q1")
    public Mono<JsonNode> pdlPerson(@PathVariable("ident") String ident,
                              @RequestParam(value = "pdlMiljoe", required = false, defaultValue = "Q2") PdlMiljoer
                                      pdlMiljoe) {
        return pdlPersonConsumer.getPdlPerson(ident, pdlMiljoe);
    }

    @GetMapping("/identer")
    @Operation(description = "Henter flere PDL-personer i hht liste av identer fra PDL-API")
    public Mono<JsonNode> pdlPerson(@RequestParam("identer") List<String> identer) {

        return pdlPersonConsumer.getPdlPersoner(identer);
    }

 */
}
