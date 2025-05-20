package no.nav.dolly.budpro.kommune;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(path = "/api/kommune")
@RequiredArgsConstructor
class KommuneController {

    private final KommuneService service;

    @GetMapping("/all")
    Flux<Kommune> getAll() {
        return Flux.fromIterable(service.getAll());
    }

}
