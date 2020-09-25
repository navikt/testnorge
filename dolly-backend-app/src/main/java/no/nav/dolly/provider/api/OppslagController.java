package no.nav.dolly.provider.api;

import static java.util.Arrays.asList;
import static no.nav.dolly.config.CachingConfig.CACHE_KODEVERK;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.aareg.AaregConsumer;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdResponse;
import no.nav.dolly.bestilling.inntektstub.InntektstubConsumer;
import no.nav.dolly.bestilling.inntektstub.domain.ValiderInntekt;
import no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterConsumer;
import no.nav.dolly.bestilling.sykemelding.HelsepersonellConsumer;
import no.nav.dolly.bestilling.sykemelding.domain.dto.HelsepersonellListeDTO;
import no.nav.dolly.consumer.fastedatasett.DatasettType;
import no.nav.dolly.consumer.fastedatasett.FasteDatasettConsumer;
import no.nav.dolly.consumer.identpool.IdentpoolConsumer;
import no.nav.dolly.consumer.kodeverk.KodeverkConsumer;
import no.nav.dolly.consumer.kodeverk.KodeverkMapper;
import no.nav.dolly.consumer.kodeverk.domain.GetKodeverkKoderBetydningerResponse;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.consumer.saf.SafConsumer;
import no.nav.dolly.consumer.saf.domain.SafRequest;
import no.nav.dolly.consumer.saf.domain.SafRequest.VariantFormat;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.domain.resultset.kodeverk.KodeverkAdjusted;
import no.nav.dolly.service.InntektsmeldingEnumService;
import no.nav.dolly.service.TransaksjonMappingService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class OppslagController {

    private final KodeverkMapper kodeverkMapper;
    private final KodeverkConsumer kodeverkConsumer;
    private final AaregConsumer aaregConsumer;
    private final PdlPersonConsumer pdlPersonConsumer;
    private final InntektstubConsumer inntektstubConsumer;
    private final FasteDatasettConsumer fasteDatasettConsumer;
    private final PensjonforvalterConsumer pensjonforvalterConsumer;
    private final IdentpoolConsumer identpoolConsumer;
    private final InntektsmeldingEnumService inntektsmeldingEnumService;
    private final TransaksjonMappingService transaksjonMappingService;
    private final HelsepersonellConsumer helsepersonellConsumer;
    private final SafConsumer safConsumer;

    @Cacheable(CACHE_KODEVERK)
    @GetMapping("/kodeverk/{kodeverkNavn}")
    @Operation(description = "Hent kodeverk etter kodeverkNavn")
    public KodeverkAdjusted fetchKodeverkByName(@PathVariable("kodeverkNavn") String kodeverkNavn) {
        GetKodeverkKoderBetydningerResponse response = kodeverkConsumer.fetchKodeverkByName(kodeverkNavn);
        return kodeverkMapper.mapBetydningToAdjustedKodeverk(kodeverkNavn, response.getBetydninger());
    }

    @GetMapping("/pdlperson/ident/{ident}")
    @Operation(description = "Hent person tilhørende ident fra pdlperson")
    public ResponseEntity pdlPerson(@PathVariable("ident") String ident) {
        return pdlPersonConsumer.getPdlPerson(ident);
    }

    @GetMapping("/inntektstub/{ident}")
    @Operation(description = "Hent inntekter tilhørende ident fra Inntektstub")
    public ResponseEntity inntektstub(@PathVariable String ident) {
        return inntektstubConsumer.getInntekter(ident);
    }

    @PostMapping("/inntektstub")
    @Operation(description = "Valider inntekt mot Inntektstub")
    public ResponseEntity inntektstub(@RequestBody ValiderInntekt validerInntekt) {
        return inntektstubConsumer.validerInntekter(validerInntekt);
    }

    @GetMapping("/systemer")
    @Operation(description = "Hent liste med systemer og deres beskrivelser")
    public List<SystemTyper.SystemBeskrivelse> getSystemTyper() {
        return asList(SystemTyper.values()).stream()
                .map(type -> SystemTyper.SystemBeskrivelse.builder().system(type.name()).beskrivelse(type.getBeskrivelse()).build())
                .collect(Collectors.toList());
    }

    @GetMapping("/helsepersonell")
    @Operation(description = "Hent liste med helsepersonell")
    public ResponseEntity<HelsepersonellListeDTO> getHelsepersonell() {
        return helsepersonellConsumer.getHelsepersonell();
    }

    @GetMapping("/fastedatasett/{datasettype}")
    @Operation(description = "Hent faste datasett med beskrivelser")
    public ResponseEntity getFasteDatasett(@PathVariable DatasettType datasettype) {
        return fasteDatasettConsumer.hentDatasett(datasettype);
    }

    @GetMapping("/fastedatasett/tps/{gruppe}")
    @Operation(description = "Hent faste datasett gruppe med beskrivelser")
    public ResponseEntity getFasteDatasettGruppe(@PathVariable String gruppe) {
        return fasteDatasettConsumer.hentDatasettGruppe(gruppe);
    }

    @GetMapping("/aareg/arbeidsforhold")
    @Operation(description = "Hent arbeidsforhold fra aareg")
    public List<ArbeidsforholdResponse> getArbeidsforhold(@RequestParam String ident, @RequestParam String miljoe) {
        return aaregConsumer.hentArbeidsforhold(ident, miljoe);
    }

    @GetMapping("/orgnummer")
    @Operation(description = "Hent faste orgnummer")
    public ResponseEntity getOrgnummer() {
        return fasteDatasettConsumer.hentOrgnummer();
    }

    @GetMapping("/popp/inntekt/{ident}/{miljoe}")
    @Operation(description = "Hent inntekter fra POPP-register")
    public ResponseEntity getPoppInntekter(@PathVariable String ident, @PathVariable String miljoe) {
        return pensjonforvalterConsumer.getInntekter(ident, miljoe);
    }

    @GetMapping("/popp/miljoe")
    @Operation(description = "Hent tilgjengelige miljøer for POPP-register")
    public Set<String> getPoppMiljoer() {
        return pensjonforvalterConsumer.getMiljoer();
    }

    @GetMapping("/personnavn")
    @Operation(description = "Henter 10 syntetiske personnavn")
    public ResponseEntity getPersonnavn() {
        return identpoolConsumer.getPersonnavn();
    }

    @GetMapping("/inntektsmelding/{enumtype}")
    @Operation(description = "Henter enumtyper for inntektsmelding")
    public List<String> getInntektsmeldingeTyper(@PathVariable InntektsmeldingEnumService.EnumTypes enumtype) {

        return inntektsmeldingEnumService.getEnumType(enumtype);
    }

    @GetMapping("/transaksjonid/{system}/{ident}")
    @Operation(description = "Henter transaksjon IDer for ident")
    public List<TransaksjonMapping> getTransaksjonIder(@PathVariable SystemTyper system, @PathVariable String ident) {

        return transaksjonMappingService.getTransaksjonMapping(system, ident);
    }

    @GetMapping("/inntektsmelding/{journalpostId}/{dokumentInfoId}")
    @Operation(description = "Henter dokumentinformasjon for inntektsmelding fra Joark")
    public List<JsonNode> getInntektsmeldingDokumentinfo(@PathVariable String journalpostId, @PathVariable String dokumentInfoId,
            @RequestParam VariantFormat variantFormat,
            @RequestParam String miljoe) {
        return safConsumer.getInntektsmeldingDokumentinfo(miljoe, new SafRequest(dokumentInfoId, journalpostId, variantFormat.name()));
    }

    @GetMapping("/dokarkiv/{journalpostId}")
    @Operation(description = "Henter dokumentinformasjon for dokarkiv fra Joark")
    public ResponseEntity<JsonNode> getDokarkivDokumentinfo(@PathVariable String journalpostId, @RequestParam(required = false) String miljoe) {
        return safConsumer.getDokarkivDokumentinfo(miljoe, journalpostId);
    }
}