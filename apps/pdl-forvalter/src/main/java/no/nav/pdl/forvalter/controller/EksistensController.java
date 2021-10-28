package no.nav.pdl.forvalter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.service.EksistensService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AvailibilityResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/eksistens")
@RequiredArgsConstructor
public class EksistensController {

    private final EksistensService eksistensService;

    @ResponseBody
    @GetMapping(produces = "application/json; charset=utf-8")
    @Operation(description = "Sjekk om ident(er) er gyldig(e) og tilgjengelig(e) for oppretting av ny(e) person(er)")
    public List<AvailibilityResponseDTO> getIdentAvailability(@Parameter(description = "Ident(er) som skal sjekkes")
                                                               @RequestParam List<String> identer) {

        return eksistensService.checkAvailibility(identer);
    }
}