package no.nav.registre.orgnrservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import no.nav.registre.orgnrservice.domain.Organisasjon;
import no.nav.registre.orgnrservice.service.OrgnummerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orgnummer")
public class OrgnummerController {

    private static final String ORGNR_REGEX = "^([8-9])\\d{8}$";
    private final OrgnummerService orgnummerService;

    @Operation(summary = "Hent gyldige organisasjonsnummer")
    @GetMapping
    public List<String> getOrgnummer(@RequestHeader Integer antall) {
        return orgnummerService.hentOrgnr(antall);
    }

    @PutMapping
    @Operation(summary = "Sett et organisasjonsnummer til ledig")
    public Organisasjon setLedig(@RequestHeader @Pattern(regexp = ORGNR_REGEX) String orgnummer) {
        return orgnummerService.setLedigForOrgnummer(orgnummer, true);
    }
}