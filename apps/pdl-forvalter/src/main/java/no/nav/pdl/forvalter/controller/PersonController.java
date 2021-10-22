package no.nav.pdl.forvalter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.dto.Paginering;
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
    @GetMapping(produces = "application/json; charset=utf-8")
    @Operation(description = "Hent personer")
    public List<FullPersonDTO> getPerson(@Parameter(description = "Hent personer med angitte identer, eller")
                                         @RequestParam(required = false) List<String> identer,
                                         @Parameter(description = "Hent identitet ved søk på (u)fullstendig ident og/eller en eller flere navn")
                                         @RequestParam(required = false) String fragment,
                                         @Parameter(description = "Sidenummer ved sortering på \'sistOppdatert\' og nyeste først")
                                         @RequestParam(required = false, defaultValue = "0") Integer sidenummer,
                                         @Parameter(description = "Sidestørrelse ved sortering på \'sistOppdatert\' og nyeste først")
                                         @RequestParam(required = false, defaultValue = "10") Integer sidestorrelse) {

        return personService.getPerson(identer, fragment, Paginering.builder()
                .sidenummer(sidenummer)
                .sidestoerrelse(sidestorrelse)
                .build());
    }

    @ResponseBody
    @PostMapping(produces = "application/json; charset=utf-8")
    @Operation(description = "Opprett person basert på angitte informasjonselementer, minimum er {}")
    public String createPerson(@RequestBody BestillingRequestDTO request) {

        return personService.createPerson(request);
    }

    @ResponseBody
    @PutMapping(value = "/{ident}", produces = "application/json; charset=utf-8")
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
    @PostMapping(value = "/{ident}/ordre", produces = "application/json; charset=utf-8")
    @Operation(description = "Send angitte testperson(er) med relasjoner til PDL")
    public OrdreResponseDTO sendPersonTilPdl(@Parameter(description = "Ident på hovedperson som skal sendes")
                                             @PathVariable String ident,
                                             @Parameter(description = "Angir om TPS er master, true == hovedperson skal ikke slettes i PDL")
                                             @RequestParam(required = false) Boolean isTpsMaster) {

        return pdlOrdreService.send(ident, isTpsMaster);
    }
}