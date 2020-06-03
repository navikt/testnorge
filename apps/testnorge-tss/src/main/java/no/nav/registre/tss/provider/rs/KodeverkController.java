package no.nav.registre.tss.provider.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;

import no.nav.registre.tss.service.KodeverkService;

@RestController
@RequestMapping("api/v1/kodeverk")
public class KodeverkController {

    @Autowired
    private KodeverkService kodeverkService;

    @GetMapping("/{kodetabell}")
    public Object hentKodeverk(@PathVariable String kodetabell, @RequestParam String brukerId, @RequestParam String miljoe) throws JMSException {
        return kodeverkService.hentKodeverk(kodetabell, brukerId, miljoe);
    }
}
