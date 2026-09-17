package no.nav.dolly.provider;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.service.InntektService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/v1/endring")
@RequiredArgsConstructor
public class EndringController {

    private final InntektService inntektService;

    @DeleteMapping("/inntekt/ident/{ident}/periode/{periode}")
    public Mono<Void> deleteInntektByAarMaaned(
            @PathVariable String ident,
            @PathVariable YearMonth periode) {

        return inntektService.deleteInntekt(ident, periode);
    }
}
