package no.nav.skattekortservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.skattekortservice.service.SkattekortService;
import no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortTilArbeidsgiverDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/skattekort")
public class SkattekortController {

    private final SkattekortService skattekortService;

    @PostMapping("/arbeidsgiver")
    public Mono<String> sendSkattekortTilArbeidsgiver(@RequestBody SkattekortTilArbeidsgiverDTO skattekort) {

        return skattekortService.sendSkattekort(skattekort);
    }
}
