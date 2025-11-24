package no.nav.dolly.budpro.organisasjonsenhet;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(path = "/api/enhet")
@RequiredArgsConstructor
class OrganisasjonsenhetController {

    private final OrganisasjonsenhetService service;

    @GetMapping("/all")
    Flux<Organisasjonsenhet> getAll() {
        return Flux.fromIterable(service.getAll());
    }

}
