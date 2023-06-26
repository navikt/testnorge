package no.nav.testnav.endringsmeldingservice.controller;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.endringsmeldingservice.consumer.TpsMessagingConsumer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/identer/{ident}/miljoer")
@RequiredArgsConstructor
public class IdentMiljoeController {
    private final TpsMessagingConsumer tpsMessagingConsumer;

    @GetMapping
    public Mono<ResponseEntity<?>> getMiljoer(@PathVariable String ident) {
        return tpsMessagingConsumer.hentMiljoer(ident)
                .map(miljoer -> {
                    if (miljoer == null) {
                        return ResponseEntity.notFound().build();
                    }
                    return ResponseEntity.ok(miljoer);
                });
    }

}
