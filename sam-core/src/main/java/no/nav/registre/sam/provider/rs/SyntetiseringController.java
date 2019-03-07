package no.nav.registre.sam.provider.rs;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.sam.provider.rs.requests.SyntetiserSamRequest;
import no.nav.registre.sam.service.SyntetiseringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringController {

    @Autowired
    private SyntetiseringService syntetiseringService;

    @LogExceptions
    @PostMapping(value = "/generer")
    public ResponseEntity genererSammeldinger(@RequestBody SyntetiserSamRequest syntetiserSamRequest) {
        System.out.println("Finner hoder");
        //List<String> identer = syntetiseringService.finnLevendeIdenter(syntetiserSamRequest);
        List<String> identer = new ArrayList<>();
        identer.add("12345678910");
        System.out.println("Synter");
        return syntetiseringService.finnSyntetiserteMeldinger(identer);
    }
}