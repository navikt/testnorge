package no.nav.adresse.service.provider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import no.nav.adresse.service.dto.AdresseRequest;
import no.nav.adresse.service.dto.AdresseResponse;
import no.nav.adresse.service.service.PdlAdresseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Objects.nonNull;

@RestController
@RequestMapping("/api/v1/adresser")
@RequiredArgsConstructor
public class AdresseController {

    private final PdlAdresseService pdlAdresseService;

    @GetMapping(value = "/postnummer/{postnummer}")
    @Operation(description = "Henter tilfeldige adresser basert på postnummer eller poststed")
    public AdresseResponse getTilfeldigAdressePostnummer(@PathVariable String postnummer,
                                                         @Schema(defaultValue = "1") @RequestHeader(required = false) Long antall) {

        return AdresseResponse.builder()
                .vegadresser(pdlAdresseService.getAdressePostnummer(postnummer, nonNull(antall) ? antall : 1))
                .build();
    }

    @GetMapping(value = "/kommunenummer/{kommunenummer}")
    @Operation(description = "Henter tilfeldige adresser basert på kommune (nummer eller navn), " +
            "opsjonelt bydel (nummer eller navn)")
    public AdresseResponse getTilfeldigAdresseKommunenummer(@PathVariable String kommunenummer,
                                                            @RequestParam(required = false) String bydel,
                                                            @Schema(defaultValue = "1") @RequestHeader(required = false) Long antall) {

        return AdresseResponse.builder()
                .vegadresser(pdlAdresseService.getAdresseKommunenummer(kommunenummer, bydel, nonNull(antall) ? antall : 1))
                .build();
    }

    @PostMapping(value = "/auto")
    @Operation(description = "Henter tilfeldige adresse basert på parametre inn, tom forespørsel gir helt tilfeldig adresse")
    public AdresseResponse getTilfeldigAdresseAutocomplete(@RequestBody AdresseRequest request,
                                                           @Schema(defaultValue = "1") @RequestHeader(required = false) Long antall) {

        return AdresseResponse.builder()
                .vegadresser(pdlAdresseService.getAdresseAutoComplete(request, nonNull(antall) ? antall : 1))
                .build();
    }
}
