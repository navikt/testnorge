package no.nav.pdl.forvalter.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.service.PdlOrdreService;
import no.nav.pdl.forvalter.service.PersonService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonUpdateRequestDTO;
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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/personer")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final PdlOrdreService pdlOrdreService;

    @ResponseBody
    @GetMapping
    @Operation(description = "Hent person")
    public List<FullPersonDTO> getPerson(@RequestParam List<String> ident) {

        return personService.getPerson(ident);
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
    @PostMapping(value = "/{ident}/ordre")
    @Operation(description = "Send person til PDL (ordre)")
    public OrdreResponseDTO sendPersonTilPdl(@PathVariable String ident) {

        return pdlOrdreService.send(ident);
    }
}
