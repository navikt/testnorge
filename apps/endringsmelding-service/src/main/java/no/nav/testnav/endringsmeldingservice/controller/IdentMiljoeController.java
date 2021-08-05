package no.nav.testnav.endringsmeldingservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import no.nav.testnav.endringsmeldingservice.consumer.TpsForvalterConsumer;

@RestController
@RequestMapping("/api/v1/identer/{ident}/miljoer")
@RequiredArgsConstructor
public class IdentMiljoeController {
    private final TpsForvalterConsumer consumer;

    @GetMapping
    public ResponseEntity<Set<String>> getMiljoer(@PathVariable String ident) {
        var miljoer = consumer.hentMiljoer(ident);
        if (miljoer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(miljoer);
    }

}
