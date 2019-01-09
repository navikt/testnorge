package no.nav.registre.orkestratoren.provider.rs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.orkestratoren.consumer.rs.response.SkdMeldingerTilTpsRespons;
import no.nav.registre.orkestratoren.exceptions.HttpStatusCodeExceptionContainer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserEiaRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSkdmeldingerRequest;
import no.nav.registre.orkestratoren.service.ArenaInntektSyntPakkenService;
import no.nav.registre.orkestratoren.service.EiaSyntPakkenService;
import no.nav.registre.orkestratoren.service.TpsSyntPakkenService;

@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringsController {

    @Autowired
    private TpsSyntPakkenService tpsSyntPakkenService;

    @Autowired
    private ArenaInntektSyntPakkenService arenaInntektSyntPakkenService;

    @Autowired
    private EiaSyntPakkenService eiaSyntPakkenService;

    @LogExceptions
    @PostMapping(value = "/tps/skdmeldinger/generer")
    public ResponseEntity opprettSkdMeldingerOgSendTilTps(@RequestBody SyntetiserSkdmeldingerRequest syntetiserSkdmeldingerRequest) {
        try {
            SkdMeldingerTilTpsRespons skdMeldingerTilTpsRespons = tpsSyntPakkenService.produserOgSendSkdmeldingerTilTpsIMiljoer(syntetiserSkdmeldingerRequest.getAvspillergruppeId(),
                    syntetiserSkdmeldingerRequest.getMiljoe(),
                    syntetiserSkdmeldingerRequest.getAntallMeldingerPerEndringskode());
            return ResponseEntity.ok(skdMeldingerTilTpsRespons);
        } catch (HttpStatusCodeExceptionContainer e) {
            return ResponseEntity.status(e.getGeneralStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    @LogExceptions
    @PostMapping(value = "/arena/inntekt/generer")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public List<String> opprettSyntetiskInntektsmeldingIInntektstub(@RequestBody SyntetiserInntektsmeldingRequest request) {
        return arenaInntektSyntPakkenService.genererEnInntektsmeldingPerFnrIInntektstub(request);
    }

    @LogExceptions
    @PostMapping(value = "/eia/sykemelding/generer")
    public List<String> genererSykemeldingerIEia(@RequestBody SyntetiserEiaRequest syntetiserEiaRequest) {
        return eiaSyntPakkenService.genererEiaSykemeldinger(syntetiserEiaRequest);
    }
}