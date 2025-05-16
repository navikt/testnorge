package no.nav.dolly.budpro.koststed;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/koststed")
class KoststedController {

    private final KoststedService service;

    @GetMapping("/all")
    Flux<Koststed> getAll() {
        return Flux.fromIterable(service.getAll());
    }

}
