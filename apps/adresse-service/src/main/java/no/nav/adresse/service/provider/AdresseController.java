package no.nav.adresse.service.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.adresse.service.dto.AdresseRequest;
import no.nav.adresse.service.dto.PdlAdresseResponse.Vegadresse;
import no.nav.adresse.service.service.PdlAdresseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Objects.nonNull;

@RestController
@RequestMapping("/api/v1/adresser")
@RequiredArgsConstructor
public class AdresseController {

    private final PdlAdresseService pdlAdresseService;

    @GetMapping(value = "/postnummer/{postnummer}")
    @Operation(description = "Henter tilfeldige adresser basert på postnummer eller poststed")
    public List<Vegadresse> getTilfeldigAdressePostnummer(@PathVariable String postnummer,
                                                          @RequestParam(required = false) Long antall) {

        return pdlAdresseService.getAdressePostnummer(postnummer, nonNull(antall) ? antall : 1);
    }

    @GetMapping(value = "/kommunenummer/{kommune}")
    @Operation(description = "Henter tilfeldige adresser basert på kommune, opsjonelt bydel")
    public List<Vegadresse> getTilfeldigAdresseKommunenummer(@PathVariable String kommune,
                                                             @RequestParam(required = false) String bydel,
                                                             @RequestParam(required = false) Long antall) {

        return pdlAdresseService.getAdresseKommunenummer(kommune, bydel, nonNull(antall) ? antall : 1);
    }

    @GetMapping(value = "/auto")
    @Operation(description = "Henter spesifisert adresse")
    public List<Vegadresse> getTilfeldigAdresseAutocomplete(@RequestBody(required = false) AdresseRequest request,
                                                            @RequestParam(required = false) Long antall) {

        return pdlAdresseService.getAdresseAutoComplete(request, nonNull(antall) ? antall : 1);
    }
}
