package no.nav.testnav.apps.importfratpsfservice.provider.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.importfratpsfservice.consumer.TpsfConsumer;
import no.nav.testnav.apps.importfratpsfservice.dto.SkdEndringsmeldingGruppe;
import no.nav.testnav.apps.importfratpsfservice.service.TpsfService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final TpsfConsumer tpsfConsumer;

    @Operation(description = "Leser SKD-meldinger med personer fra TPSF-gruppe og legger disse inn PDL-forvalter + eksisterende Dolly-gruppe")
    @PutMapping(value = "/gruppe/{gruppeId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> importIdenter(@PathVariable("gruppeId") Long skdGruppeId,
                                      @RequestParam Long dollyGruppeId,
                                      @Parameter(description = "Hent fra og med sidenummer (inklusiv), default = 0")
                                      @RequestParam(required = false) Integer fraSideNr,
                                      @Parameter(description = "Hent til og med sidenummer (eksklusiv), default = antall sider i SKD gruppe")
                                      @RequestParam(required = false) Integer tilSideNr) {

        return tpsfService.importIdenter(skdGruppeId, dollyGruppeId, fraSideNr, tilSideNr);
    }

    @GetMapping(value = "/grupper")
    public Flux<SkdEndringsmeldingGruppe> getGrupper() {

        return tpsfConsumer.getSkdGrupper();
    }
}
