package no.nav.pdl.forvalter.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.domain.PdlPerson;
import no.nav.pdl.forvalter.dto.BestillingRequest;
import no.nav.pdl.forvalter.dto.PdlOrdreResponse;
import no.nav.pdl.forvalter.dto.PersonUpdateRequest;
import no.nav.pdl.forvalter.service.PdlOrdreService;
import no.nav.pdl.forvalter.service.PersonService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/personer")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final PdlOrdreService pdlOrdreService;

    @GetMapping(value = "/{ident}")
    @Operation(description = "Hent person")
    public PdlPerson getPerson(@PathVariable String ident) {

        return personService.getPerson(ident);
    }

    @PostMapping
    @Operation(description = "Opprett person")
    public JsonNode createPerson(@RequestBody BestillingRequest request) {

        return null;
    }

    @PutMapping(value = "/{ident}")
    @Operation(description = "Endre, legg-til på person")
    public DbPerson updatePerson(@PathVariable String ident, @RequestBody PersonUpdateRequest request) {

        return personService.updatePerson(ident, request);
    }

    @DeleteMapping(value = "/{ident}")
    @Operation(description = "Slett person")
    public void deletePerson(@PathVariable String ident) {

        personService.deletePerson(ident);
    }

    @PostMapping(value = "/{ident}/ordre")
    @Operation(description = "Send person til PDL (ordre)")
    public PdlOrdreResponse createPerson(@PathVariable String ident) {

        return PdlOrdreResponse.builder()
                .ident(ident)
                .ordrer(pdlOrdreService.sendTilPdl(ident))
                .build();
    }
}
