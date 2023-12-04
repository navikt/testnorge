package no.nav.testnav.endringsmeldingservice.controller;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.endringsmeldingservice.consumer.TpsMessagingConsumer;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.PersonMiljoeDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/identer/{ident}/miljoer")
@RequiredArgsConstructor
public class IdentMiljoeController {
    private final TpsMessagingConsumer tpsMessagingConsumer;

    @GetMapping
    public Flux<PersonMiljoeDTO> getMiljoer(@PathVariable String ident) {
        return tpsMessagingConsumer.hentMiljoer(ident);
    }

}
