package no.nav.dolly.provider.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.inntektstub.InntektstubConsumer;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.bestilling.inntektstub.domain.ValiderInntekt;
import no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterConsumer;
import no.nav.dolly.bestilling.skjermingsregister.SkjermingsRegisterConsumer;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingDataResponse;
import no.nav.dolly.bestilling.sykemelding.HelsepersonellConsumer;
import no.nav.dolly.bestilling.sykemelding.domain.dto.HelsepersonellListeDTO;
import no.nav.dolly.bestilling.udistub.UdiStubConsumer;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonResponse;
import no.nav.dolly.consumer.fastedatasett.DatasettType;
import no.nav.dolly.consumer.fastedatasett.FasteDatasettConsumer;
import no.nav.dolly.consumer.generernavn.GenererNavnConsumer;
import no.nav.dolly.consumer.kodeverk.KodeverkConsumer;
import no.nav.dolly.consumer.kodeverk.KodeverkMapper;
import no.nav.dolly.consumer.kodeverk.domain.KodeverkBetydningerResponse;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer.PDL_MILJOER;
import no.nav.dolly.consumer.profil.ProfilApiConsumer;
import no.nav.dolly.domain.PdlPerson.Navn;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.domain.resultset.kodeverk.KodeverkAdjusted;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.service.InntektsmeldingEnumService;
import no.nav.dolly.service.InntektsmeldingEnumService.EnumTypes;
import no.nav.dolly.service.RsTransaksjonMapping;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.testnav.libs.dto.profil.v1.ProfilDTO;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static no.nav.dolly.config.CachingConfig.CACHE_HELSEPERSONELL;
import static no.nav.dolly.config.CachingConfig.CACHE_KODEVERK;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class OppslagController {

    private final KodeverkMapper kodeverkMapper;
    private final KodeverkConsumer kodeverkConsumer;
    private final PdlPersonConsumer pdlPersonConsumer;
    private final InntektstubConsumer inntektstubConsumer;
    private final FasteDatasettConsumer fasteDatasettConsumer;
    private final PensjonforvalterConsumer pensjonforvalterConsumer;
    private final GenererNavnConsumer genererNavnConsumer;
    private final InntektsmeldingEnumService inntektsmeldingEnumService;
    private final ProfilApiConsumer profilApiConsumer;
    private final TransaksjonMappingService transaksjonMappingService;
    private final HelsepersonellConsumer helsepersonellConsumer;
    private final SkjermingsRegisterConsumer skjermingsRegisterConsumer;
    private final UdiStubConsumer udiStubConsumer;

    @Cacheable(CACHE_KODEVERK)
    @GetMapping("/kodeverk/{kodeverkNavn}")
    @Operation(description = "Hent kodeverk etter kodeverkNavn")
    public KodeverkAdjusted fetchKodeverkByName(@PathVariable("kodeverkNavn") String kodeverkNavn) {
        KodeverkBetydningerResponse response = kodeverkConsumer.fetchKodeverkByName(kodeverkNavn);
        return kodeverkMapper.mapBetydningToAdjustedKodeverk(kodeverkNavn, response.getBetydninger());
    }

    @GetMapping("/kodeverk")
    @Operation(description = "Hent kodeverk, returnerer map")
    public Map<String, String> fetchKodeverk(@RequestParam String kodeverk) {

        return kodeverkConsumer.getKodeverkByName(kodeverk)
                .block();
    }

    @GetMapping("/pdlperson/ident/{ident}")
    @Operation(description = "Hent person tilhørende ident fra pdlperson")
    public JsonNode pdlPerson(@PathVariable("ident") String ident,
                              @RequestParam(value = "pdlMiljoe", required = false, defaultValue = "Q2") PDL_MILJOER pdlMiljoe) {
        return pdlPersonConsumer.getPdlPerson(ident, pdlMiljoe);
    }

    @GetMapping("/pdlperson/identer")
    @Operation(description = "Hent flere personer angitt ved identer fra PDL, maks BLOCK_SIZE = 50 identer")
    public PdlPersonBolk pdlPerson(@RequestParam("identer") List<String> identer) {
        var personer = pdlPersonConsumer.getPdlPersoner(identer)
                .collectList()
                .block();

        return nonNull(personer) && !personer.isEmpty() ? personer.get(0) : null;
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

    @GetMapping("/skjerming/{ident}")
    @Operation(description = "Hent skjerming på ident")
    public SkjermingDataResponse getSkjerming(@PathVariable String ident) {

        var response = skjermingsRegisterConsumer.getSkjerming(ident);
        if (response.isEksistererIkke()) {
            throw new NotFoundException(format("Skjerming for ident %s ble ikke funnet", ident));
        }
        return response;
    }

    @GetMapping("/udistub/{ident}")
    @Operation(description = "Hent udistub ident")
    public UdiPersonResponse getUdistubIdent(@PathVariable String ident) {
        return udiStubConsumer.getUdiPerson(ident).block();
    }

    @Cacheable(CACHE_HELSEPERSONELL)
    @GetMapping("/helsepersonell")
    @Operation(description = "Hent liste med helsepersonell")
    public HelsepersonellListeDTO getHelsepersonell() {
        return helsepersonellConsumer.getHelsepersonell();
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

    @GetMapping("/popp/inntekt/{ident}/{miljoe}")
    @Operation(description = "Hent inntekter fra POPP-register")
    public JsonNode getPoppInntekter(@PathVariable String ident, @PathVariable String miljoe) {
        return pensjonforvalterConsumer.getInntekter(ident, miljoe);
    }

    @GetMapping("/tp/forhold/{ident}/{miljoe}")
    @Operation(description = "Hent ordning fra TP-register")
    public JsonNode getTpForhold(@PathVariable String ident, @PathVariable String miljoe) {
        return pensjonforvalterConsumer.getTpForhold(ident, miljoe);
    }

    @GetMapping("/tp/miljoe")
    @Operation(description = "Hent tilgjengelige miljøer for TP-register")
    public Set<String> getTpMiljoer() {
        return pensjonforvalterConsumer.getMiljoer().block();
    }

    @GetMapping("/personnavn")
    @Operation(description = "Henter et gitt antall syntetiske personnavn")
    public ResponseEntity<List<Navn>> getPersonnavn(@RequestParam(required = false, defaultValue = "10") Integer antall) {
        return genererNavnConsumer.getPersonnavn(antall);
    }

    @GetMapping("/inntektsmelding/{enumtype}")
    @Operation(description = "Henter enumtyper for inntektsmelding")
    public List<String> getInntektsmeldingeTyper(@PathVariable EnumTypes enumtype) {

        return inntektsmeldingEnumService.getEnumType(enumtype);
    }

    @GetMapping("/profil")
    @Operation(description = "Henter profil for innlogget bruker")
    public ProfilDTO getInntektsmeldingeTyper() {

        return profilApiConsumer.getProfil().getBody();
    }

    @GetMapping("/transaksjonid")
    @Operation(description = "Henter transaksjon IDer for bestillingId, ident og system")
    public List<RsTransaksjonMapping> getTransaksjonIderIdent(
            @Parameter(description = "En ID som identifiserer en bestilling mot Dolly") @RequestParam(required = false) Long bestillingId,
            @Parameter(description = "Ident (f.eks FNR) på person knyttet til en bestilling") @RequestParam String ident,
            @Parameter(description = "System kan hentes ut fra /api/v1/systemer") @RequestParam(required = false) String system) {

        return transaksjonMappingService.getTransaksjonMapping(system, ident, bestillingId);
    }
}
