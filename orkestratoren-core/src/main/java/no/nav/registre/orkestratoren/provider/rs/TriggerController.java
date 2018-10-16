package no.nav.registre.orkestratoren.provider.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.orkestratoren.consumer.rs.response.AvspillingResponse;
import no.nav.registre.orkestratoren.service.TpsSyntPakkenConsumer;

@RestController
@RequestMapping("/trigger")
public class TriggerController {

    @Autowired
    private TpsSyntPakkenConsumer tpsSyntPakkenConsumer;

    @RequestMapping(value = "/opprettSkdMeldinger", method = RequestMethod.POST)
    public AvspillingResponse opprettSkdMeldinger(@RequestBody TriggerRequest triggerRequest) {

        return tpsSyntPakkenConsumer.produserOgSendSkdmeldingerTilTpsIMiljoer(triggerRequest.getAntallSkdMeldinger(),
                triggerRequest.getMiljoer(),
                triggerRequest.getAarsakskoder());
    }
}