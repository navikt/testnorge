package no.nav.registre.orkestratoren.provider.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.orkestratoren.consumer.rs.response.AvspillingResponse;
import no.nav.registre.orkestratoren.service.TpsSyntPakkenConsumer;

@RestController
@RequestMapping("/syntetisering")
public class SyntetiseringsController {

    @Autowired
    private TpsSyntPakkenConsumer tpsSyntPakkenConsumer;

    @LogExceptions
    @RequestMapping(value = "/tps/skdmeldinger/generer", method = RequestMethod.POST)
    public AvspillingResponse opprettSkdMeldinger(@RequestBody SyntetiserSkdmeldingerRequest syntetiserSkdmeldingerRequest) {

        return tpsSyntPakkenConsumer.produserOgSendSkdmeldingerTilTpsIMiljoer(syntetiserSkdmeldingerRequest.getSkdMeldingGruppeId(),
                syntetiserSkdmeldingerRequest.getMiljoe(),
                syntetiserSkdmeldingerRequest.getAntallMeldingerPerEndringskode());
    }
}