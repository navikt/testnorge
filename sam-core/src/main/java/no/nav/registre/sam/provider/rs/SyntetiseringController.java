package no.nav.registre.sam.provider.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.sam.provider.rs.requests.SyntetiserSamRequest;
import no.nav.registre.sam.service.SyntetiseringService;

@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringController {

    @Autowired
    private SyntetiseringService syntetiseringService;

    @LogExceptions
    @PostMapping(value = "/generer")
    public ResponseEntity genererSamordningsmeldinger(@RequestBody SyntetiserSamRequest syntetiserSamRequest) {
        List<String> identer = syntetiseringService.finnLevendeIdenter(syntetiserSamRequest);
        return syntetiseringService.opprettOgLagreSyntetiserteSamordningsmeldinger(identer);
    }
}