package no.nav.testnav.endringsmeldingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.endringsmeldingservice.consumer.TpsMessagingConsumer;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.PersonMiljoeDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/ident")
@RequiredArgsConstructor
public class IdentMiljoeController {
    private final TpsMessagingConsumer tpsMessagingConsumer;

    @GetMapping("/miljoer")
    @Operation(description = "Sjekk om ident finnes i miljøer")
    public Flux<PersonMiljoeDTO> identFinnesIMiljoer(@RequestParam String ident) {
        return tpsMessagingConsumer.hentMiljoer(ident);
    }

}