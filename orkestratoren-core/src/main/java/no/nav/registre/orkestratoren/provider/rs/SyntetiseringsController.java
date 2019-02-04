package no.nav.registre.orkestratoren.provider.rs;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;

import lombok.extern.slf4j.Slf4j;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserEiaRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSkdmeldingerRequest;
import no.nav.registre.orkestratoren.service.ArenaInntektSyntPakkenService;
import no.nav.registre.orkestratoren.service.EiaSyntPakkenService;
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

    @LogExceptions
    @PostMapping(value = "/tps/skdmeldinger/generer")
    public ResponseEntity opprettSkdmeldingerITPS(@RequestBody SyntetiserSkdmeldingerRequest syntetiserSkdmeldingerRequest) {
        try {
            return tpsSyntPakkenService.genererSkdmeldinger(syntetiserSkdmeldingerRequest.getAvspillergruppeId(),
                    syntetiserSkdmeldingerRequest.getMiljoe(),
                    syntetiserSkdmeldingerRequest.getAntallMeldingerPerEndringskode());
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
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
}