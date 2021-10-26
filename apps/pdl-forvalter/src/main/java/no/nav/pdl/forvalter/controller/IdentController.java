package no.nav.pdl.forvalter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.dto.Paginering;
import no.nav.pdl.forvalter.service.IdentService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AvailibilityResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonIDDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/identer")
@RequiredArgsConstructor
public class IdentController {

    private final IdentService identService;

    @ResponseBody
    @GetMapping(produces = "application/json; charset=utf-8")
    @Operation(description = "Hent personer")
    public List<AvailibilityResponseDTO> getPersonAvailability(@Parameter(description = "Sjekk om ident(er) er tilgjengelige for oppretting")
                                                               @RequestParam List<String> identer) {

        return identService.checkAvailibility(identer);
    }

    @ResponseBody
    @GetMapping(path = "/fragment", produces = "application/json; charset=utf-8")
    @Operation(description = "Hent personer")
    public List<PersonIDDTO> getPerson(@Parameter(description = "Hent identitet ved søk på (u)fullstendig ident og/eller en eller flere navn")
                                       @RequestParam(required = false) String fragment,
                                       @Parameter(description = "Sidenummer ved sortering på 'sistOppdatert' og nyeste først")
                                       @RequestParam(required = false, defaultValue = "0") Integer sidenummer,
                                       @Parameter(description = "Sidestørrelse ved sortering på 'sistOppdatert' og nyeste først")
                                       @RequestParam(required = false, defaultValue = "10") Integer sidestorrelse) {

        return identService.getfragment(fragment, Paginering.builder()
                .sidenummer(sidenummer)
                .sidestoerrelse(sidestorrelse)
                .build());
    }
}