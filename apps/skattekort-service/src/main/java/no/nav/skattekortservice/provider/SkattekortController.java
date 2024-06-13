package no.nav.skattekortservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.skattekortservice.service.SkattekortService;
import no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortTilArbeidsgiverDTO;
import no.nav.testnav.libs.dto.skattekortservice.v1.SokosGetRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping(produces = MediaType.ALL_VALUE)
    public Mono<String> sendSkattekort(@RequestBody SkattekortTilArbeidsgiverDTO skattekort) {

        return skattekortService.sendSkattekort(skattekort);
    }

    @GetMapping(produces = MediaType.ALL_VALUE)
    public Mono<String> hentSkattekort(@RequestBody SokosGetRequest request) {

        return skattekortService.hentSkattekort(request);
    }
}
