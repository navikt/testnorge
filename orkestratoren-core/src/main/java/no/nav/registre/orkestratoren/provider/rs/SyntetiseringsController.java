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
import no.nav.registre.orkestratoren.service.AaregSyntPakkenService;
import no.nav.registre.orkestratoren.service.InntektSyntPakkenService;
import no.nav.registre.orkestratoren.service.ArenaSyntPakkenService;
import no.nav.registre.orkestratoren.service.BisysSyntPakkenService;
import no.nav.registre.orkestratoren.service.EiaSyntPakkenService;
import no.nav.registre.orkestratoren.service.InstSyntPakkenService;
import no.nav.registre.orkestratoren.service.MedlSyntPakkenService;
import no.nav.registre.orkestratoren.service.PoppSyntPakkenService;
import no.nav.registre.orkestratoren.service.SamSyntPakkenService;
import no.nav.registre.orkestratoren.service.TpSyntPakkenService;
import no.nav.registre.orkestratoren.service.TpsSyntPakkenService;

@RestController
@RequestMapping("api/v1/syntetisering")
@Slf4j
public class SyntetiseringsController {

    @Autowired
    private TpsSyntPakkenService tpsSyntPakkenService;

    @Autowired
    private InntektSyntPakkenService inntektSyntPakkenService;

    @Autowired
    private EiaSyntPakkenService eiaSyntPakkenService;

    @Autowired
    private PoppSyntPakkenService poppSyntPakkenService;

    @Autowired
    private AaregSyntPakkenService aaregSyntPakkenService;

    @Autowired
    private InstSyntPakkenService instSyntPakkenService;

    @Autowired
    private BisysSyntPakkenService bisysSyntPakkenService;

    @Autowired
    private TpSyntPakkenService tpSyntPakkenService;

    @Autowired
    private SamSyntPakkenService samSyntPakkenService;

    @Autowired
    private ArenaSyntPakkenService arenaSyntPakkenService;

    @Autowired
    private MedlSyntPakkenService medlSyntPakkenService;

    @LogExceptions
    @PostMapping(value = "/tps/skdmeldinger/generer")
    public ResponseEntity opprettSkdmeldingerITPS(@RequestBody SyntetiserSkdmeldingerRequest syntetiserSkdmeldingerRequest) {
        return tpsSyntPakkenService.genererSkdmeldinger(syntetiserSkdmeldingerRequest.getAvspillergruppeId(),
                syntetiserSkdmeldingerRequest.getMiljoe(),
                syntetiserSkdmeldingerRequest.getAntallMeldingerPerEndringskode());
    }

    @LogExceptions
    @PostMapping(value = "/nav/endringsmeldinger/generer")
    public List<RsPureXmlMessageResponse> opprettNavmeldingerITPS(@RequestBody SyntetiserNavmeldingerRequest syntetiserNavmeldingerRequest) {
        return tpsSyntPakkenService.genererNavmeldinger(syntetiserNavmeldingerRequest);
    }

    @LogExceptions
    @PostMapping(value = "/inntekt/generer")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public String opprettSyntetiskInntektsmeldingIInntektstub(@RequestBody SyntetiserInntektsmeldingRequest syntetiserInntektsmeldingRequest) {
        return inntektSyntPakkenService.genererInntektsmeldinger(syntetiserInntektsmeldingRequest);
    }

    @LogExceptions
    @PostMapping(value = "/eia/sykemeldinger/generer")
    public List<String> opprettSykemeldingerIEia(@RequestBody SyntetiserEiaRequest syntetiserEiaRequest) {
        List<String> fnrMedGenererteMeldinger = eiaSyntPakkenService.genererEiaSykemeldinger(syntetiserEiaRequest);
        log.info("eia har opprettet {} sykemeldinger. Personer som har fått opprettet sykemelding: {}", fnrMedGenererteMeldinger.size(), Arrays.toString(fnrMedGenererteMeldinger.toArray()));
        return fnrMedGenererteMeldinger;
    }

    @LogExceptions
    @PostMapping(value = "/popp/skattegrunnlag/generer")
    public ResponseEntity opprettSkattegrunnlagISigrun(@RequestHeader(value = "testdataEier", defaultValue = "", required = false) String testdataEier,
            @RequestBody SyntetiserPoppRequest syntetiserPoppRequest) {
        return poppSyntPakkenService.genererSkattegrunnlag(syntetiserPoppRequest, testdataEier);
    }

    @LogExceptions
    @PostMapping(value = "/aareg/arbeidsforhold/generer")
    public ResponseEntity opprettArbeidsforholdIAareg(@RequestParam boolean lagreIAareg, @RequestBody SyntetiserAaregRequest syntetiserAaregRequest) {
        return aaregSyntPakkenService.genererArbeidsforholdsmeldinger(syntetiserAaregRequest, lagreIAareg);
    }

    @LogExceptions
    @PostMapping(value = "/inst/institusjonsforhold/generer")
    public Object opprettInstitutjonsforholdIInst(@RequestBody SyntetiserInstRequest syntetiserInstRequest) {
        return instSyntPakkenService.genererInstitusjonsforhold(syntetiserInstRequest);
    }

    @LogExceptions
    @PostMapping(value = "/bisys/bistandsmeldinger/generer")
    public Object opprettBistandsmeldingerIBisys(@RequestBody SyntetiserBisysRequest syntetiserBisysRequest) {
        return bisysSyntPakkenService.genererBistandsmeldinger(syntetiserBisysRequest);
    }

    @LogExceptions
    @PostMapping(value = "/tp/ytelser/generer")
    public ResponseEntity opprettYtelserITp(@RequestBody SyntetiserTpRequest request) {
        return tpSyntPakkenService.genererTp(request);
    }

    @LogExceptions
    @PostMapping(value = "/sam/samordningsmeldinger/generer")
    public ResponseEntity opprettSamordningsmeldingerISam(@RequestBody SyntetiserSamRequest syntetiserSamRequest) {
        return samSyntPakkenService.genererSamordningsmeldinger(syntetiserSamRequest);
    }

    @LogExceptions
    @PostMapping(value = "/arena/arbeidsoeker/generer")
    public List<String> opprettArbeidssoekereIArena(@RequestBody SyntetiserArenaRequest syntetiserArenaRequest) {
        return arenaSyntPakkenService.opprettArbeidssokereIArena(syntetiserArenaRequest);
    }

    @LogExceptions
    @PostMapping(value = "/medl/medlemskap/generer")
    public Object opprettMedlemskapIMedl(@RequestBody SyntetiserMedlRequest syntetiserMedlRequest) {
        return medlSyntPakkenService.genererMedlemskap(syntetiserMedlRequest);
    }
}