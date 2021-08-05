package no.nav.testnav.endringsmeldingservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import no.nav.testnav.endringsmeldingservice.consumer.TpsForvalterConsumer;

@RestController
@RequestMapping("/api/v1/identer/{ident}/miljoer")
@RequiredArgsConstructor
public class IdentMiljoeController {
    private final TpsForvalterConsumer consumer;

    @GetMapping
    public Mono<ResponseEntity<?>> getMiljoer(@PathVariable String ident) {
        return consumer.hentMiljoer(ident)
                .map(miljoer -> {
                    if (miljoer == null) {
                        return ResponseEntity.notFound().build();
                    }
                    return ResponseEntity.ok(miljoer);
                });
    }

}
