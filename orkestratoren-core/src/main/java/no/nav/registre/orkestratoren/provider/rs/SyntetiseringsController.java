package no.nav.registre.orkestratoren.provider.rs;

import lombok.extern.slf4j.Slf4j;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.orkestratoren.consumer.rs.response.RsPureXmlMessageResponse;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserAaregRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserBisysRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserEiaRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInstRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserMedlRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserNavmeldingerRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserPoppRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSamRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSkdmeldingerRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserTpRequest;
import no.nav.registre.orkestratoren.service.TestnorgeAaregService;
import no.nav.registre.orkestratoren.service.TestnorgeInntektService;
import no.nav.registre.orkestratoren.service.TesnorgeArenaService;
import no.nav.registre.orkestratoren.service.TestnorgeBisysService;
import no.nav.registre.orkestratoren.service.TestnorgeEiaService;
import no.nav.registre.orkestratoren.service.TestnorgeInstService;
import no.nav.registre.orkestratoren.service.TestnorgeMedlService;
import no.nav.registre.orkestratoren.service.TestnorgeSigrunService;
import no.nav.registre.orkestratoren.service.TestnorgeSamService;
import no.nav.registre.orkestratoren.service.TestnorgeTpService;
import no.nav.registre.orkestratoren.service.TestnorgeSkdService;

@RestController
@RequestMapping("api/v1/syntetisering")
@Slf4j
public class SyntetiseringsController {

    @Autowired
    private TestnorgeSkdService testnorgeSkdService;

    @Autowired
    private TestnorgeInntektService testnorgeInntektService;

    @Autowired
    private TestnorgeEiaService testnorgeEiaService;

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
    private TesnorgeArenaService tesnorgeArenaService;

    @Autowired
    private TestnorgeMedlService testnorgeMedlService;

    @LogExceptions
    @PostMapping(value = "/tps/skdmeldinger/generer")
    public ResponseEntity opprettSkdmeldingerITPS(@RequestBody SyntetiserSkdmeldingerRequest syntetiserSkdmeldingerRequest) {
        return testnorgeSkdService.genererSkdmeldinger(syntetiserSkdmeldingerRequest.getAvspillergruppeId(),
                syntetiserSkdmeldingerRequest.getMiljoe(),
                syntetiserSkdmeldingerRequest.getAntallMeldingerPerEndringskode());
    }

    @LogExceptions
    @PostMapping(value = "/nav/endringsmeldinger/generer")
    public List<RsPureXmlMessageResponse> opprettNavmeldingerITPS(@RequestBody SyntetiserNavmeldingerRequest syntetiserNavmeldingerRequest) {
        return testnorgeSkdService.genererNavmeldinger(syntetiserNavmeldingerRequest);
    }

    @LogExceptions
    @PostMapping(value = "/inntekt/generer")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public Map<String, List<Object>> opprettSyntetiskInntektsmeldingIInntektstub(@RequestBody SyntetiserInntektsmeldingRequest syntetiserInntektsmeldingRequest) {
        return testnorgeInntektService.genererInntektsmeldinger(syntetiserInntektsmeldingRequest);
    }

    @LogExceptions
    @PostMapping(value = "/eia/sykemeldinger/generer")
    public List<String> opprettSykemeldingerIEia(@RequestBody SyntetiserEiaRequest syntetiserEiaRequest) {
        List<String> fnrMedGenererteMeldinger = testnorgeEiaService.genererEiaSykemeldinger(syntetiserEiaRequest);
        log.info("eia har opprettet {} sykemeldinger. Personer som har fått opprettet sykemelding: {}", fnrMedGenererteMeldinger.size(), Arrays.toString(fnrMedGenererteMeldinger.toArray()));
        return fnrMedGenererteMeldinger;
    }

    @LogExceptions
    @PostMapping(value = "/popp/skattegrunnlag/generer")
    public ResponseEntity opprettSkattegrunnlagISigrun(@RequestHeader(value = "testdataEier", defaultValue = "", required = false) String testdataEier,
            @RequestBody SyntetiserPoppRequest syntetiserPoppRequest) {
        return testnorgeSigrunService.genererSkattegrunnlag(syntetiserPoppRequest, testdataEier);
    }

    @LogExceptions
    @PostMapping(value = "/aareg/arbeidsforhold/generer")
    public ResponseEntity opprettArbeidsforholdIAareg(@RequestParam boolean sendAlleEksisterende, @RequestBody SyntetiserAaregRequest syntetiserAaregRequest) {
        return testnorgeAaregService.genererArbeidsforholdsmeldinger(syntetiserAaregRequest, sendAlleEksisterende);
    }

    @LogExceptions
    @PostMapping(value = "/inst/institusjonsforhold/generer")
    public Object opprettInstitutjonsforholdIInst(@RequestBody SyntetiserInstRequest syntetiserInstRequest) {
        return testnorgeInstService.genererInstitusjonsforhold(syntetiserInstRequest);
    }

    @LogExceptions
    @PostMapping(value = "/bisys/bistandsmeldinger/generer")
    public Object opprettBistandsmeldingerIBisys(@RequestBody SyntetiserBisysRequest syntetiserBisysRequest) {
        return testnorgeBisysService.genererBistandsmeldinger(syntetiserBisysRequest);
    }

    @LogExceptions
    @PostMapping(value = "/tp/ytelser/generer")
    public ResponseEntity opprettYtelserITp(@RequestBody SyntetiserTpRequest request) {
        return testnorgeTpService.genererTp(request);
    }

    @LogExceptions
    @PostMapping(value = "/sam/samordningsmeldinger/generer")
    public ResponseEntity opprettSamordningsmeldingerISam(@RequestBody SyntetiserSamRequest syntetiserSamRequest) {
        return testnorgeSamService.genererSamordningsmeldinger(syntetiserSamRequest);
    }

    @LogExceptions
    @PostMapping(value = "/arena/arbeidsoeker/generer")
    public List<String> opprettArbeidssoekereIArena(@RequestBody SyntetiserArenaRequest syntetiserArenaRequest) {
        return tesnorgeArenaService.opprettArbeidssokereIArena(syntetiserArenaRequest);
    }

    @LogExceptions
    @PostMapping(value = "/medl/medlemskap/generer")
    public Object opprettMedlemskapIMedl(@RequestBody SyntetiserMedlRequest syntetiserMedlRequest) {
        return testnorgeMedlService.genererMedlemskap(syntetiserMedlRequest);
    }
}