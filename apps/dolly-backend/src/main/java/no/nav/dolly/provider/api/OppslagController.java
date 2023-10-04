package no.nav.dolly.provider.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.inntektstub.InntektstubConsumer;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.bestilling.inntektstub.domain.ValiderInntekt;
import no.nav.dolly.consumer.fastedatasett.DatasettType;
import no.nav.dolly.consumer.fastedatasett.FasteDatasettConsumer;
import no.nav.dolly.consumer.generernavn.GenererNavnConsumer;
import no.nav.dolly.consumer.kodeverk.KodeverkConsumer;
import no.nav.dolly.consumer.kodeverk.KodeverkMapper;
import no.nav.dolly.consumer.organisasjon.tilgang.OrganisasjonTilgangConsumer;
import no.nav.dolly.consumer.organisasjon.tilgang.dto.OrganisasjonTilgang;
import no.nav.dolly.domain.PdlPerson.Navn;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.domain.resultset.kodeverk.KodeverkAdjusted;
import no.nav.dolly.service.InntektsmeldingEnumService;
import no.nav.dolly.service.InntektsmeldingEnumService.EnumTypes;
import no.nav.dolly.service.RsTransaksjonMapping;
import no.nav.dolly.service.TransaksjonMappingService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static no.nav.dolly.config.CachingConfig.CACHE_KODEVERK;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class OppslagController {

    private final KodeverkMapper kodeverkMapper;
    private final KodeverkConsumer kodeverkConsumer;
    private final InntektstubConsumer inntektstubConsumer;
    private final FasteDatasettConsumer fasteDatasettConsumer;
    private final GenererNavnConsumer genererNavnConsumer;
    private final InntektsmeldingEnumService inntektsmeldingEnumService;
    private final TransaksjonMappingService transaksjonMappingService;
    private final OrganisasjonTilgangConsumer organisasjonTilgangConsumer;

    @GetMapping("/organisasjoner/tilgang")
    public Flux<OrganisasjonTilgang> getOrganisasjonerTilgang() {

        return organisasjonTilgangConsumer.getOrgansisjonTilganger();
    }

    @Cacheable(CACHE_KODEVERK)
    @GetMapping("/kodeverk/{kodeverkNavn}")
    @Operation(description = "Hent kodeverk etter kodeverkNavn")
    public KodeverkAdjusted fetchKodeverkByName(@PathVariable("kodeverkNavn") String kodeverkNavn) {

        var response = kodeverkConsumer.fetchKodeverkByName(kodeverkNavn);
        return kodeverkMapper.mapBetydningToAdjustedKodeverk(kodeverkNavn, response)
                .blockFirst();
    }

    @GetMapping("/kodeverk")
    @Operation(description = "Hent kodeverk, returnerer map")
    public Mono<Map<String, String>> fetchKodeverk(@RequestParam String kodeverk) {

        return kodeverkConsumer.getKodeverkByName(kodeverk);
    }

    @GetMapping("/inntektstub/{ident}")
    @Operation(description = "Hent inntekter tilhørende ident fra Inntektstub")
    public List<Inntektsinformasjon> inntektstub(@PathVariable String ident) {

        return inntektstubConsumer.getInntekter(ident)
                .collectList()
                .block();
    }

    @PostMapping("/inntektstub")
    @Operation(description = "Valider inntekt mot Inntektstub")
    public ResponseEntity<Object> inntektstub(@RequestBody ValiderInntekt validerInntekt) {
        return inntektstubConsumer.validerInntekter(validerInntekt);
    }

    @GetMapping("/systemer")
    @Operation(description = "Hent liste med systemer og deres beskrivelser")
    public List<SystemTyper.SystemBeskrivelse> getSystemTyper() {
        return Arrays.stream(SystemTyper.values())
                .map(type -> SystemTyper.SystemBeskrivelse.builder().system(type.name()).beskrivelse(type.getBeskrivelse()).build())
                .toList();
    }

    @GetMapping("/fastedatasett/{datasettype}")
    @Operation(description = "Hent faste datasett med beskrivelser")
    public ResponseEntity<JsonNode> getFasteDatasett(@PathVariable DatasettType datasettype) {
        return fasteDatasettConsumer.hentDatasett(datasettype);
    }

    @GetMapping("/fastedatasett/tps/{gruppe}")
    @Operation(description = "Hent faste datasett gruppe med beskrivelser")
    public ResponseEntity<JsonNode> getFasteDatasettGruppe(@PathVariable String gruppe) {
        return fasteDatasettConsumer.hentDatasettGruppe(gruppe);
    }

    @GetMapping("/orgnummer")
    @Operation(description = "Hent faste orgnummer")
    public ResponseEntity<JsonNode> getOrgnummer() {
        return fasteDatasettConsumer.hentOrgnummer();
    }

    @GetMapping("/personnavn")
    @Operation(description = "Henter et gitt antall syntetiske personnavn")
    public ResponseEntity<List<Navn>> getPersonnavn
            (@RequestParam(required = false, defaultValue = "10") Integer antall) {
        return genererNavnConsumer.getPersonnavn(antall);
    }

    @GetMapping("/inntektsmelding/{enumtype}")
    @Operation(description = "Henter enumtyper for inntektsmelding")
    public List<String> getInntektsmeldingeTyper(@PathVariable EnumTypes enumtype) {

        return inntektsmeldingEnumService.getEnumType(enumtype);
    }

    @GetMapping("/transaksjonid")
    @Operation(description = "Henter transaksjon IDer for bestillingId, ident og system")
    public List<RsTransaksjonMapping> getTransaksjonIderIdent(
            @Parameter(description = "En ID som identifiserer en bestilling mot Dolly") @RequestParam(required = false) Long
                    bestillingId,
            @Parameter(description = "Ident (f.eks FNR) på person knyttet til en bestilling") @RequestParam String
                    ident,
            @Parameter(description = "System kan hentes ut fra /api/v1/systemer") @RequestParam(required = false) String
                    system) {

        return transaksjonMappingService.getTransaksjonMapping(system, ident, bestillingId);
    }
}
