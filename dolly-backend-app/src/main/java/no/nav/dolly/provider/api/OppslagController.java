package no.nav.dolly.provider.api;

import static java.util.Arrays.asList;
import static no.nav.dolly.config.CachingConfig.CACHE_KODEVERK;
import static no.nav.dolly.config.CachingConfig.CACHE_NORG2;

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

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.aareg.AaregConsumer;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdResponse;
import no.nav.dolly.bestilling.inntektstub.InntektstubConsumer;
import no.nav.dolly.bestilling.inntektstub.domain.ValiderInntekt;
import no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterConsumer;
import no.nav.dolly.consumer.fastedatasett.DatasettType;
import no.nav.dolly.consumer.fastedatasett.FasteDatasettConsumer;
import no.nav.dolly.consumer.identpool.IdentpoolConsumer;
import no.nav.dolly.consumer.kodeverk.KodeverkConsumer;
import no.nav.dolly.consumer.kodeverk.KodeverkMapper;
import no.nav.dolly.consumer.norg2.Norg2Consumer;
import no.nav.dolly.consumer.norg2.Norg2EnhetResponse;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.domain.resultset.kodeverk.KodeverkAdjusted;
import no.nav.dolly.service.InntektsmeldingEnumService;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.tjenester.kodeverk.api.v1.GetKodeverkKoderBetydningerResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class OppslagController {

    private final KodeverkMapper kodeverkMapper;
    private final KodeverkConsumer kodeverkConsumer;
    private final Norg2Consumer norg2Consumer;
    private final AaregConsumer aaregConsumer;
    private final PdlPersonConsumer pdlPersonConsumer;
    private final InntektstubConsumer inntektstubConsumer;
    private final FasteDatasettConsumer fasteDatasettConsumer;
    private final PensjonforvalterConsumer pensjonforvalterConsumer;
    private final IdentpoolConsumer identpoolConsumer;
    private final InntektsmeldingEnumService inntektsmeldingEnumService;
    private final TransaksjonMappingService transaksjonMappingService;

    @Cacheable(CACHE_KODEVERK)
    @GetMapping("/kodeverk/{kodeverkNavn}")
    @ApiOperation("Hent kodeverk etter kodeverkNavn")
    public KodeverkAdjusted fetchKodeverkByName(@PathVariable("kodeverkNavn") String kodeverkNavn) {
        GetKodeverkKoderBetydningerResponse response = kodeverkConsumer.fetchKodeverkByName(kodeverkNavn);
        return kodeverkMapper.mapBetydningToAdjustedKodeverk(kodeverkNavn, response.getBetydninger());
    }

    @Cacheable(CACHE_NORG2)
    @GetMapping("/norg2/enhet/{tknr}")
    @ApiOperation("Hent enhet tilhørende Tknr fra NORG")
    public Norg2EnhetResponse fetchEnhetByTknr(@PathVariable("tknr") String tknr) {
        return norg2Consumer.fetchEnhetByEnhetNr(tknr);
    }

    @GetMapping("/pdlperson/ident/{ident}")
    @ApiOperation("Hent person tilhørende ident fra pdlperson")
    public ResponseEntity pdlPerson(@PathVariable("ident") String ident) {
        return pdlPersonConsumer.getPdlPerson(ident);
    }

    @GetMapping("/inntektstub/{ident}")
    @ApiOperation("Hent inntekter tilhørende ident fra Inntektstub")
    public ResponseEntity inntektstub(@PathVariable String ident) {
        return inntektstubConsumer.getInntekter(ident);
    }

    @PostMapping("/inntektstub")
    @ApiOperation("Valider inntekt mot Inntektstub")
    public ResponseEntity inntektstub(@RequestBody ValiderInntekt validerInntekt) {
        return inntektstubConsumer.validerInntekter(validerInntekt);
    }

    @GetMapping("/systemer")
    @ApiOperation("Hent liste med systemer og deres beskrivelser")
    public List<SystemTyper.SystemBeskrivelse> getSystemTyper() {
        return asList(SystemTyper.values()).stream()
                .map(type -> SystemTyper.SystemBeskrivelse.builder().system(type.name()).beskrivelse(type.getBeskrivelse()).build())
                .collect(Collectors.toList());
    }

    @GetMapping("/fastedatasett/{datasettype}")
    @ApiOperation("Hent faste datasett med beskrivelser")
    public ResponseEntity getFasteDatasett(@PathVariable DatasettType datasettype) {
        return fasteDatasettConsumer.hentDatasett(datasettype);
    }

    @GetMapping("/fastedatasett/tps/{gruppe}")
    @ApiOperation("Hent faste datasett gruppe med beskrivelser")
    public ResponseEntity getFasteDatasettGruppe(@PathVariable String gruppe) {
        return fasteDatasettConsumer.hentDatasettGruppe(gruppe);
    }

    @GetMapping("/aareg/arbeidsforhold")
    @ApiOperation("Hent arbeidsforhold fra aareg")
    public List<ArbeidsforholdResponse> getArbeidsforhold(@RequestParam String ident, @RequestParam String miljoe) {
        return aaregConsumer.hentArbeidsforhold(ident, miljoe);
    }

    @GetMapping("/orgnummer")
    @ApiOperation("Hent faste orgnummer")
    public ResponseEntity getOrgnummer() {
        return fasteDatasettConsumer.hentOrgnummer();
    }

    @GetMapping("/popp/inntekt/{ident}/{miljoe}")
    @ApiOperation("Hent inntekter fra POPP-register")
    public ResponseEntity getPoppInntekter(@PathVariable String ident, @PathVariable String miljoe) {
        return pensjonforvalterConsumer.getInntekter(ident, miljoe);
    }

    @GetMapping("/popp/miljoe")
    @ApiOperation("Hent tilgjengelige miljøer for POPP-register")
    public Set<String> getPoppMiljoer() {
        return pensjonforvalterConsumer.getMiljoer();
    }

    @GetMapping("/personnavn")
    @ApiOperation("Henter 10 syntetiske personnavn")
    public ResponseEntity getPersonnavn() {
        return identpoolConsumer.getPersonnavn();
    }

    @GetMapping("/inntektsmelding/{enumtype}")
    @ApiOperation("Henter enumtyper for inntektsmelding")
    public List<String> getInntektsmeldingeTyper(@PathVariable InntektsmeldingEnumService.EnumTypes enumtype) {

        return inntektsmeldingEnumService.getEnumType(enumtype);
    }

    @GetMapping("/transaksjonid/{system}/{ident}")
    @ApiOperation("Henter transaksjon IDer for ident")
    public List<TransaksjonMapping> getTransaksjonIder(@PathVariable SystemTyper system, @PathVariable String ident) {

        return transaksjonMappingService.getTransaksjonMapping(system, ident);
    }
}