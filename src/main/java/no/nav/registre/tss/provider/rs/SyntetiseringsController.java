package no.nav.registre.tss.provider.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import no.nav.registre.tss.consumer.rs.responses.Response910;
import no.nav.registre.tss.domain.Person;
import no.nav.registre.tss.provider.rs.requests.SyntetiserTssRequest;
import no.nav.registre.tss.service.TssService;

@Slf4j
@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringsController {

    @Autowired
    private TssService tssService;

    @PostMapping(value = "/opprettLeger")
    public ResponseEntity opprettLegerITss(@RequestBody SyntetiserTssRequest syntetiserTssRequest) {
        List<Person> identer = tssService.hentIdenter(syntetiserTssRequest);
        List<String> syntetiskeTssRutiner = tssService.opprettSyntetiskeTssRutiner(identer);

        try {
            tssService.sendTilTss(syntetiskeTssRutiner);
        } catch (Exception e) {
            log.error("Kunne ikke sende til kø", e);
            throw e;
        }

        return ResponseEntity.status(HttpStatus.OK).body(syntetiskeTssRutiner);
    }

    @GetMapping("/hentLeger/{avspillergruppeId}")
    public Map<String, Response910> hentLegerFraTss(@PathVariable Long avspillergruppeId, @RequestParam(required = false) Integer antallLeger) throws JMSException {
        return tssService.sendOgMotta910RutineFraTss(avspillergruppeId, antallLeger);
    }

    @GetMapping("/hentLege/{ident}")
    public Response910 hentLegeFraTss(@PathVariable String ident) throws JMSException {
        return tssService.sendOgMotta910RutineFraTss(ident);
    }
}
