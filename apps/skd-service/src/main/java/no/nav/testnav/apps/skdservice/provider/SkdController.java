package no.nav.testnav.apps.skdservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.skdservice.consumer.response.SkdMeldingerTilTpsRespons;
import no.nav.testnav.apps.skdservice.service.FasteMeldingerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class SkdController {

    private final FasteMeldingerService fasteMeldingerService;

    @Operation(description = "Her kan man starte avspilling av en TPSF-avspillergruppe. Dette innebærer at alle skd-meldingene i en gitt gruppe sendes til TPS i et gitt miljø.")
    @PostMapping(value = "/startAvspilling/{avspillergruppeId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<SkdMeldingerTilTpsRespons> startAvspillingAvTpsfAvspillergruppe(
            @PathVariable Long avspillergruppeId,
            @RequestParam String miljoe) {

        return fasteMeldingerService.startAvspillingAvTpsfAvspillergruppe(avspillergruppeId, miljoe);
    }
}
