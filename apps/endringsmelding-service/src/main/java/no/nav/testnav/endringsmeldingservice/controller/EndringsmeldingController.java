package no.nav.testnav.endringsmeldingservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Set;

import no.nav.testnav.endringsmeldingservice.consumer.TpsForvalterConsumer;
import no.nav.testnav.libs.dto.endringsmelding.v1.DoedsmeldingDTO;
import no.nav.testnav.libs.dto.endringsmelding.v1.FoedselsmeldingDTO;

@RestController
@RequestMapping("/api/v1/endringsmelding")
@RequiredArgsConstructor
public class EndringsmeldingController {

    private final TpsForvalterConsumer consumer;

    @PostMapping("/foedeselsmelding")
    public Mono<ResponseEntity<?>> sendFoedselsmelding(
            @RequestHeader Set<String> miljoer,
            @RequestBody FoedselsmeldingDTO dto
    ) {
        return consumer.sendFoedselsmelding(dto, miljoer)
                .map(status -> {
                    if (!status.isOk()) {
                        return ResponseEntity.badRequest().body(status.getErrors());
                    }
                    return ResponseEntity.ok(status.getPersonId());
                });

    }

    @PostMapping("/doedsmelding")
    public Mono<ResponseEntity<?>> sendFoedselsmelding(
            @RequestHeader Set<String> miljoer,
            @RequestBody DoedsmeldingDTO dto
    ) {
        return consumer.sendDoedsmelding(dto, miljoer)
                .map(status -> {
                    if (!status.isOk()) {
                        return ResponseEntity.badRequest().body(status.getErrors());
                    }
                    return ResponseEntity.ok(status.getPersonId());
                });
    }
}
