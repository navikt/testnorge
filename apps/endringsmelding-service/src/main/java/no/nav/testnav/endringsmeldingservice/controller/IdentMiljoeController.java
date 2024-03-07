package no.nav.testnav.endringsmeldingservice.controller;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.endringsmeldingservice.consumer.TpsMessagingConsumer;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.PersonMiljoeDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/ident")
@RequiredArgsConstructor
public class IdentMiljoeController {
    private final TpsMessagingConsumer tpsMessagingConsumer;

    @PostMapping
    public Flux<PersonMiljoeDTO> identFinnesIMiljoer(@RequestBody String ident) {
        return tpsMessagingConsumer.hentMiljoer(ident);
    }

}