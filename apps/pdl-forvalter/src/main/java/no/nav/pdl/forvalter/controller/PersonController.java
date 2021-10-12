package no.nav.pdl.forvalter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.domain.Data;
import no.nav.pdl.forvalter.service.PdlOrdreService;
import no.nav.pdl.forvalter.service.PersonService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonUpdateRequestDTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.LongAccumulator;

@Slf4j
@RestController
@RequestMapping("/api/v1/personer")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final PdlOrdreService pdlOrdreService;

    @ResponseBody
    @GetMapping
    @Operation(description = "Hent personer")
    public Flux<FullPersonDTO> getPerson(@Parameter(description = "Hent personer med angitte identer, eller")
                                         @RequestParam(required = false) List<String> identer,
                                         @Parameter(description = "Hent identitet ved søk på (u)fullstendig ident og/eller en eller flere navn")
                                         @RequestParam(required = false) String fragment) {

        return personService.getPerson(identer, fragment);
    }

    @ResponseBody
    @PostMapping
    @Operation(description = "Opprett person basert på angitte informasjonselementer, minimum er {}")
    public String createPerson(@RequestBody BestillingRequestDTO request) {

        return personService.createPerson(request);
    }

    @ResponseBody
    @PutMapping(value = "/{ident}")
    @Operation(description = "Oppdater testperson basert på angitte informasjonselementer")
    public String updatePerson(@Parameter(description = "Ident på testperson som skal oppdateres")
                               @PathVariable String ident,
                               @RequestBody PersonUpdateRequestDTO request) {

        return personService.updatePerson(ident, request);
    }

    @ResponseBody
    @DeleteMapping(value = "/{ident}")
    @Operation(description = "Slett person")
    public void deletePerson(@Parameter(description = "Slett angitt testperson med relasjoner")
                             @PathVariable String ident) {

        personService.deletePerson(ident);
    }

    @ResponseBody
    @PostMapping(value = "/ordre", produces = MediaType.APPLICATION_NDJSON_VALUE)
    @Operation(description = "Send angitte testperson(er) med relasjoner til PDL")
    public Flux<OrdreResponseDTO> sendPersonTilPdl(@Parameter(description = "Angir om TPS er master, true == hovedperson skal ikke slettes i PDL")
                                                   @RequestParam(required = false) Boolean isTpsMaster,
                                                   @RequestBody OrdreRequestDTO ordre) {

        return pdlOrdreService.send(ordre, isTpsMaster);
    }

    @ResponseBody
    @GetMapping(value = "/test", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Data> source() {

        return Flux.interval(Duration.ofSeconds(1))
                .take(5)
                .map(i -> new Data(i, LocalDateTime.now()));
    }
}