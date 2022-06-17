package no.nav.registre.orkestratoren.provider.rs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.orkestratoren.consumer.rs.response.GenererFrikortResponse;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserAaregRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserFrikortRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInstRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserMedlRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserPoppRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSamRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserTpRequest;
import no.nav.registre.orkestratoren.service.TestnorgeAaregService;
import no.nav.registre.orkestratoren.service.ArenaService;
import no.nav.registre.orkestratoren.service.TestnorgeFrikortService;
import no.nav.registre.orkestratoren.service.TestnorgeInntektService;
import no.nav.registre.orkestratoren.service.TestnorgeInstService;
import no.nav.registre.orkestratoren.service.TestnorgeMedlService;
import no.nav.registre.orkestratoren.service.TestnorgeSamService;
import no.nav.registre.orkestratoren.service.TestnorgeSigrunService;
import no.nav.registre.orkestratoren.service.TestnorgeTpService;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringsController {

    private final TestnorgeInntektService testnorgeInntektService;
    private final TestnorgeSigrunService testnorgeSigrunService;
    private final TestnorgeAaregService testnorgeAaregService;
    private final TestnorgeInstService testnorgeInstService;
    private final TestnorgeTpService testnorgeTpService;
    private final TestnorgeSamService testnorgeSamService;
    private final ArenaService arenaService;
    private final TestnorgeMedlService testnorgeMedlService;
    private final TestnorgeFrikortService testnorgeFrikortService;

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

    @PostMapping(value = "/arena/arbeidsoeker/generer/oppfoelging")
    public Map<String, NyeBrukereResponse> opprettArbeidssoekereMedOppfoelgingIArena(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return arenaService.opprettArbeidssoekereMedOppfoelgingIArena(syntetiserArenaRequest);
    }

    @PostMapping(value = "/arena/vedtakshistorikk/generer")
    public Map<String, List<NyttVedtakResponse>> opprettVedtakshistorikkIArena(
            @RequestBody SyntetiserArenaRequest vedtakshistorikkRequest
    ) {
        return arenaService.opprettArenaVedtakshistorikk(vedtakshistorikkRequest);
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