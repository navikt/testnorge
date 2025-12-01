package no.nav.testnav.endringsmeldingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.endringsmeldingservice.consumer.TpsMessagingConsumer;
import no.nav.testnav.endringsmeldingservice.domain.IdenterRequest;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsIdentStatusDTO;
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

    @PostMapping("/miljoer")
    @Operation(description = "Sjekk om ident finnes i miljøer")
    public Flux<TpsIdentStatusDTO> identFinnesIMiljoer(@RequestBody IdenterRequest body) {
        return tpsMessagingConsumer.hentMiljoer(body);
    }

}