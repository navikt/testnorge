package no.nav.dolly.budpro.stillinger;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(path = "/api/stilling")
@RequiredArgsConstructor
class StillingController {

    private final StillingService service;

    @GetMapping("/all")
    Flux<Stilling> getAll() {
        return Flux.fromIterable(service.getAll());
    }

}
