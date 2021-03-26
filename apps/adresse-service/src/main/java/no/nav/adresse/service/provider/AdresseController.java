package no.nav.adresse.service.provider;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.adresse.service.dto.AdresseRequest;
import no.nav.adresse.service.service.PdlAdresseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/adresser")
@RequiredArgsConstructor
public class AdresseController {

    private final PdlAdresseService pdlAdresseService;

    @GetMapping(value = "/postnummer/{postnummer}")
    @Operation(description = "Henter tilfelfeldige adresser basert på postnummer")
    public JsonNode getTilfeldigAdressePostnummer(@PathVariable String postnummer) {

        return pdlAdresseService.getAdressePostnummer(postnummer);
    }

    @GetMapping(value = "/kommunenummer/{kommunenummer}")
    @Operation(description = "Henter tilfeldige adresser basert på kommunenummer, opsjonelt bydelsnummer")
    public JsonNode getTilfeldigAdresseKommunenummer(@PathVariable String kommunenummer,
                                                     @RequestParam(required = false) String bydelsnavn) {

        return pdlAdresseService.getAdresseKommunenummer(kommunenummer, bydelsnavn);
    }

    @GetMapping(value = "/auto")
    @Operation(description = "Henter spesifisert adresse")
    public JsonNode getTilfeldigAdresseAutocomplete(@RequestBody AdresseRequest request) {

        return pdlAdresseService.getAdresseAutoComplete(request);
    }
}
