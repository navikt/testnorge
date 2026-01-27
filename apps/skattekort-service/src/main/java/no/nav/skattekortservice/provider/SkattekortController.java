package no.nav.skattekortservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.skattekortservice.service.SkattekortService;
import no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortRequestDTO;
import no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortResponseDTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/skattekort")
public class SkattekortController {

    private final SkattekortService skattekortService;

    @PostMapping(produces = MediaType.ALL_VALUE)
    public Mono<String> sendSkattekort(@RequestBody SkattekortRequestDTO skattekort) {

        return skattekortService.sendSkattekort(skattekort);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<SkattekortResponseDTO> hentSkattekort(
            @RequestParam String ident,
            @RequestParam(required = false) Integer inntektsaar) {

        return skattekortService.hentSkattekort(ident, inntektsaar);
    }
}
