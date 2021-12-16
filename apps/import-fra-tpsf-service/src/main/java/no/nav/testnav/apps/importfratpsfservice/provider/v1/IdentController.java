package no.nav.testnav.apps.importfratpsfservice.provider.v1;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.importfratpsfservice.dto.SkdEndringsmelding;
import no.nav.testnav.apps.importfratpsfservice.service.TpsfService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/identer")
@RequiredArgsConstructor
public class IdentController {

    private final TpsfService tpsfService;

    @Operation(description = "Leser SKD-meldinger med personer fra TPSF-gruppe og legger disse inn PDL-forvalter + eksisterende Dolly-gruppe")
    @PutMapping(value = "/gruppe/{gruppeId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<SkdEndringsmelding> importIdenter(@PathVariable("gruppeId") Long skdGruppeId,
                                                 @RequestParam Long dollyGruppeId) {

        return tpsfService.importIdenter(skdGruppeId, dollyGruppeId);
    }
}
