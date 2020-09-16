package no.nav.dolly.provider.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.aareg.AaregConsumer;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdResponse;
import no.nav.dolly.bestilling.inntektstub.InntektstubConsumer;
import no.nav.dolly.bestilling.inntektstub.domain.ValiderInntekt;
import no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterConsumer;
import no.nav.dolly.bestilling.sykemelding.HelsepersonellConsumer;
import no.nav.dolly.bestilling.sykemelding.domain.dto.LegeListeDTO;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static no.nav.dolly.config.CachingConfig.CACHE_KODEVERK;

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
    @ApiOperation(value = "Hent kodeverk etter kodeverkNavn", authorizations = {@Authorization(value = "Bearer token fra bruker")})
    public KodeverkAdjusted fetchKodeverkByName(@PathVariable("kodeverkNavn") String kodeverkNavn) {
        GetKodeverkKoderBetydningerResponse response = kodeverkConsumer.fetchKodeverkByName(kodeverkNavn);
        return kodeverkMapper.mapBetydningToAdjustedKodeverk(kodeverkNavn, response.getBetydninger());
    }

    @GetMapping("/pdlperson/ident/{ident}")
    @ApiOperation(value = "Hent person tilhørende ident fra pdlperson", authorizations = {@Authorization(value = "Bearer token fra bruker")})
    public ResponseEntity pdlPerson(@PathVariable("ident") String ident) {
        return pdlPersonConsumer.getPdlPerson(ident);
    }

    @GetMapping("/inntektstub/{ident}")
    @ApiOperation(value = "Hent inntekter tilhørende ident fra Inntektstub", authorizations = {@Authorization(value = "Bearer token fra bruker")})
    public ResponseEntity inntektstub(@PathVariable String ident) {
        return inntektstubConsumer.getInntekter(ident);
    }

    @PostMapping("/inntektstub")
    @ApiOperation(value = "Valider inntekt mot Inntektstub", authorizations = {@Authorization(value = "Bearer token fra bruker")})
    public ResponseEntity inntektstub(@RequestBody ValiderInntekt validerInntekt) {
        return inntektstubConsumer.validerInntekter(validerInntekt);
    }

    @GetMapping("/systemer")
    @ApiOperation(value = "Hent liste med systemer og deres beskrivelser", authorizations = {@Authorization(value = "Bearer token fra bruker")})
    public List<SystemTyper.SystemBeskrivelse> getSystemTyper() {
        return asList(SystemTyper.values()).stream()
                .map(type -> SystemTyper.SystemBeskrivelse.builder().system(type.name()).beskrivelse(type.getBeskrivelse()).build())
                .collect(Collectors.toList());
    }

    @GetMapping("/helsepersonell/leger")
    @ApiOperation(value = "Hent liste med leger", authorizations = {@Authorization(value = "Bearer token fra bruker")})
    public ResponseEntity<LegeListeDTO> getLeger() {
        return helsepersonellConsumer.getLeger();
    }

    @GetMapping("/fastedatasett/{datasettype}")
    @ApiOperation(value = "Hent faste datasett med beskrivelser", authorizations = {@Authorization(value = "Bearer token fra bruker")})
    public ResponseEntity getFasteDatasett(@PathVariable DatasettType datasettype) {
        return fasteDatasettConsumer.hentDatasett(datasettype);
    }

    @GetMapping("/fastedatasett/tps/{gruppe}")
    @ApiOperation(value = "Hent faste datasett gruppe med beskrivelser", authorizations = {@Authorization(value = "Bearer token fra bruker")})
    public ResponseEntity getFasteDatasettGruppe(@PathVariable String gruppe) {
        return fasteDatasettConsumer.hentDatasettGruppe(gruppe);
    }

    @GetMapping("/aareg/arbeidsforhold")
    @ApiOperation(value = "Hent arbeidsforhold fra aareg", authorizations = {@Authorization(value = "Bearer token fra bruker")})
    public List<ArbeidsforholdResponse> getArbeidsforhold(@RequestParam String ident, @RequestParam String miljoe) {
        return aaregConsumer.hentArbeidsforhold(ident, miljoe);
    }

    @GetMapping("/orgnummer")
    @ApiOperation(value = "Hent faste orgnummer", authorizations = {@Authorization(value = "Bearer token fra bruker")})
    public ResponseEntity getOrgnummer() {
        return fasteDatasettConsumer.hentOrgnummer();
    }

    @GetMapping("/popp/inntekt/{ident}/{miljoe}")
    @ApiOperation(value = "Hent inntekter fra POPP-register", authorizations = {@Authorization(value = "Bearer token fra bruker")})
    public ResponseEntity getPoppInntekter(@PathVariable String ident, @PathVariable String miljoe) {
        return pensjonforvalterConsumer.getInntekter(ident, miljoe);
    }

    @GetMapping("/popp/miljoe")
    @ApiOperation(value = "Hent tilgjengelige miljøer for POPP-register", authorizations = {@Authorization(value = "Bearer token fra bruker")})
    public Set<String> getPoppMiljoer() {
        return pensjonforvalterConsumer.getMiljoer();
    }

    @GetMapping("/personnavn")
    @ApiOperation(value = "Henter 10 syntetiske personnavn", authorizations = {@Authorization(value = "Bearer token fra bruker")})
    public ResponseEntity getPersonnavn() {
        return identpoolConsumer.getPersonnavn();
    }

    @GetMapping("/inntektsmelding/{enumtype}")
    @ApiOperation(value = "Henter enumtyper for inntektsmelding", authorizations = {@Authorization(value = "Bearer token fra bruker")})
    public List<String> getInntektsmeldingeTyper(@PathVariable EnumTypes enumtype) {

        return inntektsmeldingEnumService.getEnumType(enumtype);
    }

    @GetMapping("/transaksjonid")
    @ApiOperation(value = "Henter transaksjon IDer for bestillingId, ident og system", authorizations = {@Authorization(value = "Bearer token fra bruker")})
    public List<RsTransaksjonMapping> getTransaksjonIderIdent(
            @ApiParam(value = "En ID som identifiserer en bestilling mot Dolly") @RequestParam(required = false) Long bestillingId,
            @ApiParam(value = "Ident (f.eks FNR) på person knyttet til en bestilling") @RequestParam(required = false) String ident,
            @ApiParam(value = "System kan hentes ut fra /api/v1/systemer") @RequestParam(required = false) String system) {

        return transaksjonMappingService.getTransaksjonMapping(system, ident, bestillingId);
    }

    @GetMapping("/inntektsmelding/{journalpostId}/{dokumentInfoId}")
    @ApiOperation(value = "Henter dokumentinformasjon for inntektsmelding fra Joark", authorizations = {@Authorization(value = "Bearer token fra bruker")})
    public List<JsonNode> getInntektsmeldingDokumentinfo(@PathVariable String journalpostId, @PathVariable String dokumentInfoId,
                                                         @RequestParam VariantFormat variantFormat,
                                                         @RequestParam String miljoe) {
        return safConsumer.getInntektsmeldingDokumentinfo(miljoe, new SafRequest(dokumentInfoId, journalpostId, variantFormat.name()));
    }

    @GetMapping("/dokarkiv/{journalpostId}")
    @ApiOperation(value = "Henter dokumentinformasjon for dokarkiv fra Joark", authorizations = {@Authorization(value = "Bearer token fra bruker")})
    public ResponseEntity<JsonNode> getDokarkivDokumentinfo(@PathVariable String journalpostId, @RequestParam(required = false) String miljoe) {
        return safConsumer.getDokarkivDokumentinfo(miljoe, journalpostId);
    }
}