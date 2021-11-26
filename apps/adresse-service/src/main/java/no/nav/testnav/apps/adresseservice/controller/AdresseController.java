package no.nav.testnav.apps.adresseservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.adresseservice.dto.MatrikkeladresseRequest;
import no.nav.testnav.apps.adresseservice.dto.VegadresseRequest;
import no.nav.testnav.apps.adresseservice.service.PdlAdresseService;
import no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Objects.nonNull;

@RestController
@RequestMapping("/api/v1/adresser")
@RequiredArgsConstructor
public class AdresseController {

    private final PdlAdresseService pdlAdresseService;

    @GetMapping(value = "/veg")
    @Operation(description = "Henter tilfeldige vegadresse(r) basert på parametre inn, tom forespørsel gir helt tilfeldig vegadresse")
    @ResponseBody
    public List<VegadresseDTO> getVegadresse(@RequestParam(required = false) String matrikkelId,
                                             @RequestParam(required = false) String adressenavn,
                                             @RequestParam(required = false) String husnummer,
                                             @RequestParam(required = false) String husbokstav,
                                             @RequestParam(required = false) String postnummer,
                                             @RequestParam(required = false) String poststed,
                                             @RequestParam(required = false) String kommunenummer,
                                             @RequestParam(required = false) String kommunenavn,
                                             @RequestParam(required = false) String bydelsnummer,
                                             @RequestParam(required = false) String bydelsnavn,
                                             @RequestParam(required = false) String tilleggsnavn,
                                             @Schema(description = "Fritekstsøk", example = "Sannergata 2 0557 Oslo")
                                             @RequestParam(required = false) String fritekst,
                                             @Schema(defaultValue = "1") @RequestHeader(required = false) Long antall) {

        return pdlAdresseService.getVegadresse(VegadresseRequest.builder()
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

    @GetMapping(value = "/matrikkeladresse")
    @Operation(description = "Henter tilfeldige matrikkeladresse(r) basert på parametre inn, tom forespørsel gir helt tilfeldig matrikkeladresse")
    @ResponseBody
    public List<MatrikkeladresseDTO> getMatrikkeladresse(@RequestParam(required = false) String matrikkelId,
                                                         @RequestParam(required = false) String kommunenummer,
                                                         @RequestParam(required = false) String gaardsnummer,
                                                         @RequestParam(required = false) String bruksnummer,
                                                         @RequestParam(required = false) String postnummer,
                                                         @RequestParam(required = false) String poststed,
                                                         @RequestParam(required = false) String tilleggsnavn,
                                                         @Schema(defaultValue = "1") @RequestHeader(required = false) Long antall) {

        return pdlAdresseService.getMatrikkelAdresse(MatrikkeladresseRequest.builder()
                .matrikkelId(matrikkelId)
                .kommunenummer(kommunenummer)
                .gaardsnummer(gaardsnummer)
                .brukesnummer(bruksnummer)
                .postnummer(postnummer)
                .poststed(poststed)
                .tilleggsnavn(tilleggsnavn)
                .build(), nonNull(antall) ? antall : 1);
    }
}
