package no.nav.registre.orkestratoren.provider.rs;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.orkestratoren.consumer.rs.response.GenererFrikortResponse;
import no.nav.registre.orkestratoren.consumer.rs.response.RsPureXmlMessageResponse;
import no.nav.registre.orkestratoren.consumer.rs.response.SkdMeldingerTilTpsRespons;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserAaregRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaAapRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaVedtakshistorikkRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserBisysRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserFrikortRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInstRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserMedlRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserNavmeldingerRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserPoppRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSamRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSkdmeldingerRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserTpRequest;
import no.nav.registre.orkestratoren.service.TestnorgeAaregService;
import no.nav.registre.orkestratoren.service.TestnorgeArenaService;
import no.nav.registre.orkestratoren.service.TestnorgeBisysService;
import no.nav.registre.orkestratoren.service.TestnorgeFrikortService;
import no.nav.registre.orkestratoren.service.TestnorgeInntektService;
import no.nav.registre.orkestratoren.service.TestnorgeInstService;
import no.nav.registre.orkestratoren.service.TestnorgeMedlService;
import no.nav.registre.orkestratoren.service.TestnorgeSamService;
import no.nav.registre.orkestratoren.service.TestnorgeSigrunService;
import no.nav.registre.orkestratoren.service.TestnorgeSkdService;
import no.nav.registre.orkestratoren.service.TestnorgeTpService;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/syntetisering")
@Slf4j
public class SyntetiseringsController {

    @Autowired
    private TestnorgeSkdService testnorgeSkdService;

    @Autowired
    private TestnorgeInntektService testnorgeInntektService;

    @Autowired
    private TestnorgeSigrunService testnorgeSigrunService;

    @Autowired
    private TestnorgeAaregService testnorgeAaregService;

    @Autowired
    private TestnorgeInstService testnorgeInstService;

    @Autowired
    private TestnorgeBisysService testnorgeBisysService;

    @Autowired
    private TestnorgeTpService testnorgeTpService;

    @Autowired
    private TestnorgeSamService testnorgeSamService;

    @Autowired
    private TestnorgeArenaService testnorgeArenaService;

    @Autowired
    private TestnorgeMedlService testnorgeMedlService;

    @Autowired
    private TestnorgeFrikortService testnorgeFrikortService;

    @PostMapping(value = "/tps/skdmeldinger/generer")
    public ResponseEntity<SkdMeldingerTilTpsRespons> opprettSkdmeldingerITPS(
            @RequestBody SyntetiserSkdmeldingerRequest syntetiserSkdmeldingerRequest
    ) {
        return testnorgeSkdService.genererSkdmeldinger(syntetiserSkdmeldingerRequest.getAvspillergruppeId(),
                syntetiserSkdmeldingerRequest.getMiljoe(),
                syntetiserSkdmeldingerRequest.getAntallMeldingerPerEndringskode());
    }

    @PostMapping(value = "/nav/endringsmeldinger/generer")
    public List<RsPureXmlMessageResponse> opprettNavmeldingerITPS(
            @RequestBody SyntetiserNavmeldingerRequest syntetiserNavmeldingerRequest
    ) {
        return testnorgeSkdService.genererNavmeldinger(syntetiserNavmeldingerRequest);
    }

    @PostMapping(value = "/inntekt/generer")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public Map<String, List<Object>> opprettSyntetiskInntektsmeldingIInntektstub(
            @RequestBody SyntetiserInntektsmeldingRequest syntetiserInntektsmeldingRequest
    ) {
        return testnorgeInntektService.genererInntektsmeldinger(syntetiserInntektsmeldingRequest);
    }

    @PostMapping(value = "/popp/skattegrunnlag/generer")
    public ResponseEntity<List<Integer>> opprettSkattegrunnlagISigrun(
            @RequestHeader(value = "testdataEier", defaultValue = "", required = false) String testdataEier,
            @RequestBody SyntetiserPoppRequest syntetiserPoppRequest
    ) {
        return testnorgeSigrunService.genererSkattegrunnlag(syntetiserPoppRequest, testdataEier);
    }

    @PostMapping(value = "/aareg/arbeidsforhold/generer")
    public ResponseEntity<List<Object>> opprettArbeidsforholdIAareg(
            @RequestParam boolean sendAlleEksisterende,
            @RequestBody SyntetiserAaregRequest syntetiserAaregRequest
    ) {
        return testnorgeAaregService.genererArbeidsforholdsmeldinger(syntetiserAaregRequest, sendAlleEksisterende);
    }

    @PostMapping(value = "/inst/institusjonsforhold/generer")
    public Object opprettInstitutjonsforholdIInst(
            @RequestBody SyntetiserInstRequest syntetiserInstRequest
    ) {
        return testnorgeInstService.genererInstitusjonsforhold(syntetiserInstRequest);
    }

    @PostMapping(value = "/bisys/bistandsmeldinger/generer")
    public Object opprettBistandsmeldingerIBisys(
            @RequestBody SyntetiserBisysRequest syntetiserBisysRequest
    ) {
        return testnorgeBisysService.genererBistandsmeldinger(syntetiserBisysRequest);
    }

    @PostMapping(value = "/tp/ytelser/generer")
    public ResponseEntity<?> opprettYtelserITp(
            @RequestBody SyntetiserTpRequest request
    ) {
        return testnorgeTpService.genererTp(request);
    }

    @PostMapping(value = "/sam/samordningsmeldinger/generer")
    public ResponseEntity<List<Object>> opprettSamordningsmeldingerISam(
            @RequestBody SyntetiserSamRequest syntetiserSamRequest
    ) {
        return testnorgeSamService.genererSamordningsmeldinger(syntetiserSamRequest);
    }

    @PostMapping(value = "/arena/arbeidsoeker/generer")
    public List<String> opprettArbeidssoekereIArena(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return testnorgeArenaService.opprettArbeidssokereIArena(syntetiserArenaRequest, false);
    }

    @PostMapping(value = "/arena/arbeidsoeker/generer/oppfoelging")
    public List<String> opprettArbeidssoekereMedOppfoelgingIArena(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return testnorgeArenaService.opprettArbeidssokereIArena(syntetiserArenaRequest, true);
    }

    @PostMapping(value = "/arena/vedtakshistorikk/generer")
    public ResponseEntity<String> opprettVedtakshistorikkIArena(
            @RequestBody SyntetiserArenaVedtakshistorikkRequest vedtakshistorikkRequest
    ) {
        testnorgeArenaService.opprettArenaVedtakshistorikk(vedtakshistorikkRequest);
        return ResponseEntity.ok().body("Opprettelsesrequest sendt til arena. Se logg til testnorge-arena for mer info.");
    }

    @PostMapping(value = "/arena/aap/generer")
    public List<NyttVedtakAap> opprettAapIArena(
            @RequestBody SyntetiserArenaAapRequest aapRequest
    ) {
        return testnorgeArenaService.opprettArenaAap(aapRequest);
    }

    @PostMapping(value = "/medl/medlemskap/generer")
    public Object opprettMedlemskapIMedl(
            @RequestBody SyntetiserMedlRequest syntetiserMedlRequest
    ) {
        return testnorgeMedlService.genererMedlemskap(syntetiserMedlRequest);
    }

    @PostMapping(value = "/frikort/egenandeler/generer")
    public List<GenererFrikortResponse> opprettEgenandelerIFrikort(
            @RequestBody SyntetiserFrikortRequest syntetiserFrikortRequest
    ) {
        return testnorgeFrikortService.genererFrikortEgenmeldinger(syntetiserFrikortRequest);
    }
}