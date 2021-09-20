package no.nav.pdl.forvalter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.service.PdlOrdreService;
import no.nav.pdl.forvalter.service.PersonService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonIDDTO;
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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/personer")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final PdlOrdreService pdlOrdreService;
    private final MapperFacade mapperFacade;

    @ResponseBody
    @GetMapping
    @Operation(description = "Hent personer")
    public List<FullPersonDTO> getPerson(@RequestParam List<String> identer) {

        return personService.getPerson(identer);
    }

    @ResponseBody
    @PostMapping
    @Operation(description = "Opprett person")
    public String createPerson(@RequestBody BestillingRequestDTO request) {

        return personService.createPerson(request);
    }

    @ResponseBody
    @PutMapping(value = "/{ident}")
    @Operation(description = "Endre, legg-til på person")
    public String updatePerson(@PathVariable String ident, @RequestBody PersonUpdateRequestDTO request) {

        return personService.updatePerson(ident, request);
    }

    @ResponseBody
    @DeleteMapping(value = "/{ident}")
    @Operation(description = "Slett person")
    public void deletePerson(@PathVariable String ident) {

        personService.deletePerson(ident);
    }

    @ResponseBody
    @PostMapping(value = "/ordre", produces = MediaType.APPLICATION_NDJSON_VALUE)
    @Operation(description = "Send person(er) til PDL (ordre)")
    public Flux<OrdreResponseDTO> sendPersonTilPdl(@RequestBody OrdreRequestDTO ordre) {

        return pdlOrdreService.send(ordre);
    }

    @ResponseBody
    @GetMapping(value = "/soek")
    @Operation(description = "Søk basert på fragment av ident og/eller en eller to navn")
    public List<PersonIDDTO> findPerson(@Parameter(description = "Søk på (u)fullstendig ident og/eller en eller flere navn",
            examples = @ExampleObject(value = "nat 324 bær")) String fragment) {

        return mapperFacade.mapAsList(personService.searchPerson(fragment), PersonIDDTO.class);
    }
}
