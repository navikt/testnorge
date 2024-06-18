package no.nav.skattekortservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.skattekortservice.service.OppslagService;
import no.nav.testnav.libs.dto.skattekortservice.v1.Oppslagstyper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/oppslag")
@RequiredArgsConstructor
public class OppslagController {

    private final OppslagService oppslagService;

    @GetMapping
    public Mono<Map<String, String>> getOppslag(@RequestParam Oppslagstyper oppslagstype) {

        return oppslagService.getTyper(oppslagstype);
    }
}
