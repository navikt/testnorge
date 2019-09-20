package no.nav.registre.tss.provider.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.tss.domain.Person;
import no.nav.registre.tss.provider.rs.requests.SyntetiserTssRequest;
import no.nav.registre.tss.service.SyntetiseringService;

@Slf4j
@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringsController {

    @Autowired
    private SyntetiseringService syntetiseringService;

    @PostMapping(value = "/opprettLeger")
    public ResponseEntity opprettLegerITss(@RequestBody SyntetiserTssRequest syntetiserTssRequest) {
        List<Person> identer = syntetiseringService.hentIdenter(syntetiserTssRequest);
        List<String> syntetiskeTssRutiner = syntetiseringService.opprettSyntetiskeTssRutiner(identer);

        syntetiseringService.sendTilTss(syntetiskeTssRutiner, syntetiserTssRequest.getMiljoe());

        return ResponseEntity.status(HttpStatus.OK).body(syntetiskeTssRutiner);
    }
}
