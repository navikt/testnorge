package no.nav.dolly.budpro.ansettelsestype;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/ansettesestype")
class AnsettelsestypeController {

    private final AnsettelsestypeService service;

    @GetMapping("/all")
    Flux<String> getAll() {
        return Flux.fromIterable(service.getAll());
    }

}
