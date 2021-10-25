package no.nav.pdl.forvalter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.dto.Paginering;
import no.nav.pdl.forvalter.service.IdentService;
import no.nav.pdl.forvalter.service.PdlOrdreService;
import no.nav.pdl.forvalter.service.PersonService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AvailibilityResponseDTO;
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
@RequestMapping("/api/v1/ident")
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
}