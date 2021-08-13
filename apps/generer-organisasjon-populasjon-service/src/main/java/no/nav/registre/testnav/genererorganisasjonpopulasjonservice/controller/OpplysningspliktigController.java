package no.nav.registre.testnav.genererorganisasjonpopulasjonservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import no.nav.registre.testnav.genererorganisasjonpopulasjonservice.service.GenererOpplysningspliktigService;
import no.nav.registre.testnav.genererorganisasjonpopulasjonservice.service.OpplysningspliktigService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/opplysningspliktig")
public class OpplysningspliktigController {
    private final GenererOpplysningspliktigService genererOpplysningspliktigService;
    private final OpplysningspliktigService opplysningspliktigService;

    @PostMapping
    public ResponseEntity<?> generer(@RequestHeader String miljo, @RequestHeader Integer antall) {
        var list = genererOpplysningspliktigService.generer(antall);
        var uuid = opplysningspliktigService.create(list, miljo);
        log.info("Bestilling opprettet med id: {}.", uuid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Set<String>> getOpplysningspliktigOrgnummer(@RequestHeader String miljo) {
        return ResponseEntity.ok(opplysningspliktigService.getOpplysningspliktigOrgnummer(miljo));
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestHeader String miljo) {
        opplysningspliktigService.deleteBy(miljo);
        return ResponseEntity.notFound().build();
    }
}

