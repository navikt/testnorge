package no.nav.registre.spion.provider.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.spion.domain.SykemeldingInformation;
import no.nav.registre.spion.domain.Vedtak;
import no.nav.registre.spion.service.VedtakService;

@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringController {

    @Autowired VedtakService vedtakService;

    @GetMapping(value = "/generer")
    public String generer() {
        return "Work in progress.";
    }

    @PostMapping(value = "/genererVedtak")
    public Vedtak genererVedtak(@RequestBody SykemeldingInformation info){
        return vedtakService.generateVedtak(info);
    }
}
