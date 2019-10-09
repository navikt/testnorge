package no.nav.registre.tss.provider.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;
import java.util.List;
import java.util.Map;

import no.nav.registre.tss.consumer.rs.response.Response910;
import no.nav.registre.tss.service.IdentService;

@RestController
@RequestMapping("api/v1/ident")
public class IdentController {

    @Autowired
    private IdentService identService;

    @PostMapping
    public List<String> opprettLeger(@RequestParam String miljoe, @RequestBody List<String> identer) {
        return identService.opprettLegerITss(miljoe.toLowerCase(), identer);
    }

    @PostMapping("/samhandlere")
    public List<String> opprettSamhandlere(@RequestParam String miljoe, @RequestBody List<String> identer) {
        return identService.opprettSamhandlereITss(miljoe.toLowerCase(), identer);
    }

    @GetMapping("/samhandlere/{avspillergruppeId}")
    public Map<String, Response910> hentLegerFraTss(@PathVariable Long avspillergruppeId, @RequestParam String miljoe, @RequestParam(required = false) Integer antallLeger) throws JMSException {
        return identService.hentLegerIAvspillergruppeFraTss(avspillergruppeId, antallLeger, miljoe);
    }

    @GetMapping("/samhandler/{ident}")
    public Response910 hentLegeFraTss(@PathVariable String ident, @RequestParam String miljoe) throws JMSException {
        return identService.hentLegeFraTss(ident, miljoe);
    }
}
