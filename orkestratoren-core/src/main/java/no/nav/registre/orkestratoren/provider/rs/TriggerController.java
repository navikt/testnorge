package no.nav.registre.orkestratoren.provider.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.orkestratoren.consumer.rs.response.AvspillingResponse;
import no.nav.registre.orkestratoren.service.ConsumeTpsSyntPakken;

@RestController
@RequestMapping("/trigger")
public class TriggerController {

    @Autowired
    private ConsumeTpsSyntPakken consumeTpsSyntPakken;

    @RequestMapping(value = "/opprettSkdMeldinger", method = RequestMethod.POST)
    public AvspillingResponse opprettSkdMeldinger(@RequestBody Trigger trigger) {

        return consumeTpsSyntPakken.produserOgSendSkdmeldingerTilTpsIMiljoer(trigger.getMiljoer(),
                trigger.getAntallMeldinger(), trigger.getAarsakskoder());
    }
}