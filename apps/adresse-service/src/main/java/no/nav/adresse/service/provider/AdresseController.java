package no.nav.adresse.service.provider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import no.nav.adresse.service.dto.AdresseRequest;
import no.nav.adresse.service.service.PdlAdresseService;
import no.nav.registre.testnorge.libs.dto.adresseservice.v1.VegadresseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
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
    public List<VegadresseDTO> getTilfeldigAdressePostnummer(@PathVariable String postnummer,
                                                             @Schema(defaultValue = "1") @RequestHeader(required = false) Long antall) {

        return pdlAdresseService.getAdressePostnummer(postnummer, nonNull(antall) ? antall : 1);
    }

    @GetMapping(value = "/kommunenummer/{kommunenummer}")
    @Operation(description = "Henter tilfeldige adresser basert på kommune (nummer eller navn), " +
            "opsjonelt bydel (nummer eller navn)")
    public List<VegadresseDTO> getTilfeldigAdresseKommunenummer(@PathVariable String kommunenummer,
                                                                @RequestParam(required = false) String bydel,
                                                                @Schema(defaultValue = "1") @RequestHeader(required = false) Long antall) {

        return pdlAdresseService.getAdresseKommunenummer(kommunenummer, bydel, nonNull(antall) ? antall : 1);
    }

    @GetMapping(value = "/auto")
    @Operation(description = "Henter tilfeldige adresse basert på parametre inn, tom forespørsel gir helt tilfeldig adresse")
    public List<VegadresseDTO> getTilfeldigAdresseAutocomplete(@RequestParam (required = false) String matrikkelId,
                                                               @RequestParam (required = false) String adressenavn,
                                                               @RequestParam (required = false) String husnummer,
                                                               @RequestParam (required = false) String husbokstav,
                                                               @RequestParam (required = false) String postnummer,
                                                               @RequestParam (required = false) String kommunenummer,
                                                               @RequestParam (required = false) String bydelsnummer,
                                                               @RequestParam (required = false) String poststed,
                                                               @RequestParam (required = false) String kommunenavn,
                                                               @RequestParam (required = false) String bydelsnavn,
                                                               @RequestParam (required = false) String tilleggsnavn,
                                                               @Schema(description = "Fritekstsøk", example = "Sannergata 2 0557 Oslo")
                                                               @RequestParam (required = false) String fritekst,
                                                               @Schema(defaultValue = "1") @RequestHeader(required = false) Long antall) {

        return pdlAdresseService.getAdresseAutoComplete(AdresseRequest.builder()
                .matrikkelId(matrikkelId)
                .adressenavn(adressenavn)
                .husnummer(husnummer)
                .husbokstav(husbokstav)
                .postnummer(postnummer)
                .kommunenummer(kommunenummer)
                .bydelsnummer(bydelsnummer)
                .poststed(poststed)
                .kommunenavn(kommunenavn)
                .bydelsnavn(bydelsnavn)
                .tilleggsnavn(tilleggsnavn)
                .fritekst(fritekst)
                .build(), nonNull(antall) ? antall : 1);
    }
}
