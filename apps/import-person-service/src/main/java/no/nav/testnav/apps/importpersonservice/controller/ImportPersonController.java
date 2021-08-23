package no.nav.testnav.apps.importpersonservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

import no.nav.testnav.apps.importpersonservice.consumer.PdlForvalterConsumer;
import no.nav.testnav.apps.importpersonservice.controller.dto.PersonListDTO;
import no.nav.testnav.apps.importpersonservice.domain.PersonList;

@RestController
@RequestMapping("/api/v1/personer")
@RequiredArgsConstructor
public class ImportPersonController {
    private final PdlForvalterConsumer pdlForvalterConsumer;

    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> opprett(@RequestBody PersonListDTO dto) {
        return pdlForvalterConsumer.opprett(new PersonList(dto));
    }

    @PostMapping(value = "/identer", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> opprettIdenter(@RequestBody List<String> identer) {
        return pdlForvalterConsumer.opprett(new PersonList(identer));
    }

}
