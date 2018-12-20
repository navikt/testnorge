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
import org.springframework.web.client.HttpStatusCodeException;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.orkestratoren.consumer.rs.response.SkdMeldingerTilTpsRespons;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSkdmeldingerRequest;
import no.nav.registre.orkestratoren.service.ArenaInntektSyntPakkenService;
import no.nav.registre.orkestratoren.service.TpsSyntPakkenService;

@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringsController {

    @Autowired
    private TpsSyntPakkenService tpsSyntPakkenService;

    @Autowired
    private ArenaInntektSyntPakkenService arenaInntektSyntPakkenService;

    @LogExceptions
    @PostMapping(value = "/tps/skdmeldinger/generer")
    public ResponseEntity opprettSkdMeldingerOgSendTilTps(@RequestBody SyntetiserSkdmeldingerRequest syntetiserSkdmeldingerRequest) {
        try {
            SkdMeldingerTilTpsRespons skdMeldingerTilTpsRespons = tpsSyntPakkenService.produserOgSendSkdmeldingerTilTpsIMiljoer(syntetiserSkdmeldingerRequest.getSkdMeldingGruppeId(),
                    syntetiserSkdmeldingerRequest.getMiljoe(),
                    syntetiserSkdmeldingerRequest.getAntallMeldingerPerEndringskode());
            return ResponseEntity.ok(skdMeldingerTilTpsRespons);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    @LogExceptions
    @PostMapping(value = "/arena/inntekt/generer")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public List<String> opprettSyntetiskInntektsmeldingIInntektstub(@RequestBody SyntetiserInntektsmeldingRequest request) {
        return arenaInntektSyntPakkenService.genererEnInntektsmeldingPerFnrIInntektstub(request);
    }
}