package no.nav.testnav.apps.adresseservice.controller;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.testnav.apps.adresseservice.dto.MatrikkeladresseRequest;
import no.nav.testnav.apps.adresseservice.dto.VegadresseRequest;
import no.nav.testnav.apps.adresseservice.service.OpenSearchQueryService;
import no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Slf4j
@RestController
@RequestMapping("/api/v1/adresser")
@RequiredArgsConstructor
public class AdresseController {

    private final OpenSearchQueryService openSearchQueryService;

    @GetMapping(value = "/vegadresse")
    @Operation(description = "Henter tilfeldige vegadresse(r) basert på parametre inn, tom forespørsel gir helt tilfeldig vegadresse")
    public Mono<List<VegadresseDTO>> getVegadresse(@RequestParam(required = false) String matrikkelId,
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

        val millis = System.currentTimeMillis();
        val request = VegadresseRequest.builder()
                .matrikkelId(matrikkelId)
                .adressenavn(adressenavn)
                .husnummer(husnummer)
                .husbokstav(husbokstav)
                .postnummer(postnummer)
                .poststed(poststed)
                .kommunenummer(kommunenummer)
                .kommunenavn(kommunenavn)
                .bydelsnummer(bydelsnummer)
                .bydelsnavn(bydelsnavn)
                .tilleggsnavn(tilleggsnavn)
                .fritekst(fritekst)
                .build();

        log.info("Adressesøk startet med parametre: {}", request);

        return openSearchQueryService.execQuery(request, nonNull(antall) ? antall : 1)
                .doOnNext(resultat -> log(resultat, millis));
    }

    @GetMapping(value = "/matrikkeladresse")
    @Operation(description = "Henter tilfeldige matrikkeladresse(r) basert på parametre inn, tom forespørsel gir helt tilfeldig matrikkeladresse")
    public Mono<List<MatrikkeladresseDTO>> getMatrikkeladresse(@RequestParam(required = false) String matrikkelId,
                                                               @RequestParam(required = false) String kommunenummer,
                                                               @RequestParam(required = false) String gaardsnummer,
                                                               @RequestParam(required = false) String bruksnummer,
                                                               @RequestParam(required = false) String postnummer,
                                                               @RequestParam(required = false) String poststed,
                                                               @RequestParam(required = false) String tilleggsnavn,
                                                               @Schema(defaultValue = "1") @RequestHeader(required = false) Long antall) {

        val millis = System.currentTimeMillis();
        val request = MatrikkeladresseRequest.builder()
                .matrikkelId(matrikkelId)
                .kommunenummer(kommunenummer)
                .gaardsnummer(gaardsnummer)
                .brukesnummer(bruksnummer)
                .postnummer(postnummer)
                .poststed(poststed)
                .tilleggsnavn(tilleggsnavn)
                .build();

        return openSearchQueryService.execQuery(request, nonNull(antall) ? antall : 1)
                .doOnNext(resultat -> log(resultat, millis));
    }

    private static <T> void log(List<T> resultat, long millis) {

        log.info("Adressesøk ferdig tok: {} ms, resultat {}", System.currentTimeMillis() - millis,
                resultat.stream()
                .map(Json::pretty)
                .collect(Collectors.joining()));
    }
}
