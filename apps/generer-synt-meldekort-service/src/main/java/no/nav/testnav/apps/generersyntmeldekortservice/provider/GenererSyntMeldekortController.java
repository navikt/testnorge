package no.nav.testnav.apps.generersyntmeldekortservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.generersyntmeldekortservice.consumer.SyntMeldekortConsumer;
import no.nav.testnav.apps.generersyntmeldekortservice.utils.InputValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/generate")
@RequiredArgsConstructor
public class GenererSyntMeldekortController {

    private final SyntMeldekortConsumer meldekortConsumer;

    @GetMapping("/meldekort/{meldegruppe}")
    @Operation(description = "Generer et gitt antall meldekort i XML format for meldegrupper i Arena.")
    public Mono<ResponseEntity<List<String>>> generateMeldekort(
            @PathVariable String meldegruppe,
            @RequestParam(value = "Antall meldekort") int numToGenerate,
            @RequestParam(value = "Verdi som vil overskriver alle ArbeidetTimerSum i genererte meldekort", required = false) Double arbeidstimer
    ) {
        return InputValidator.validateInput(meldegruppe)
                .then(meldekortConsumer.getSyntheticMeldekort(meldegruppe, numToGenerate, arbeidstimer))
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Syntetisering feilet.")));
    }
}
