package no.nav.registre.orkestratoren.provider.rs;

import java.util.Arrays;
import java.util.List;

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

import lombok.extern.slf4j.Slf4j;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserAaregRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserBisysRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserEiaRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInstRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserPoppRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSkdmeldingerRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserTpRequest;
import no.nav.registre.orkestratoren.service.AaregSyntPakkenService;
import no.nav.registre.orkestratoren.service.ArenaInntektSyntPakkenService;
import no.nav.registre.orkestratoren.service.BisysSyntPakkenService;
import no.nav.registre.orkestratoren.service.EiaSyntPakkenService;
import no.nav.registre.orkestratoren.service.InstSyntPakkenService;
import no.nav.registre.orkestratoren.service.PoppSyntPakkenService;
import no.nav.registre.orkestratoren.service.TpSyntPakkenService;
import no.nav.registre.orkestratoren.service.TpsSyntPakkenService;

@RestController
@RequestMapping("api/v1/syntetisering")
@Slf4j
public class SyntetiseringsController {

    @Autowired
    private TpsSyntPakkenService tpsSyntPakkenService;

    @Autowired
    private ArenaInntektSyntPakkenService arenaInntektSyntPakkenService;

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

    @LogExceptions
    @PostMapping(value = "/tps/skdmeldinger/generer")
    public ResponseEntity opprettSkdmeldingerITPS(@RequestBody SyntetiserSkdmeldingerRequest syntetiserSkdmeldingerRequest) {
        return tpsSyntPakkenService.genererSkdmeldinger(syntetiserSkdmeldingerRequest.getAvspillergruppeId(),
                syntetiserSkdmeldingerRequest.getMiljoe(),
                syntetiserSkdmeldingerRequest.getAntallMeldingerPerEndringskode());
    }

    @LogExceptions
    @PostMapping(value = "/arena/inntekt/generer")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public String opprettSyntetiskInntektsmeldingIInntektstub(@RequestBody SyntetiserInntektsmeldingRequest syntetiserInntektsmeldingRequest) {
        return arenaInntektSyntPakkenService.genererInntektsmeldinger(syntetiserInntektsmeldingRequest);
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
    @PostMapping(value = "aareg/arbeidsforhold/generer")
    public ResponseEntity opprettArbeidsforholdIAareg(@RequestParam boolean lagreIAareg, @RequestBody SyntetiserAaregRequest syntetiserAaregRequest) {
        return aaregSyntPakkenService.genererArbeidsforholdsmeldinger(syntetiserAaregRequest, lagreIAareg);
    }

    @LogExceptions
    @PostMapping(value = "inst/institusjonsforhold/generer")
    public ResponseEntity opprettInstitutjonsforholdIInst(@RequestBody SyntetiserInstRequest syntetiserInstRequest) {
        return instSyntPakkenService.genererInstitusjonsforhold(syntetiserInstRequest);
    }

    @LogExceptions
    @PostMapping(value = "inst/bistandsmeldinger/generer")
    public ResponseEntity opprettBistandsmeldingerIBisys(@RequestBody SyntetiserBisysRequest syntetiserBisysRequest) {
        return bisysSyntPakkenService.genererBistandsmeldinger(syntetiserBisysRequest);
    }

    @LogExceptions
    @PostMapping(value = "tp/ytelser/generer")
    public ResponseEntity opprettYtelserITp(@RequestBody SyntetiserTpRequest request) {
        return tpSyntPakkenService.genererTp(request);
    }
}