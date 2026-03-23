package no.nav.registre.testnorge.tilbakemeldingapi.provider;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.tilbakemeldingapi.v1.TilbakemeldingDTO;
import no.nav.registre.testnorge.tilbakemeldingapi.domain.Tilbakemelding;
import no.nav.registre.testnorge.tilbakemeldingapi.service.TilbakemeldingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tilbakemelding")
public class TilbakemeldingController {
    private final TilbakemeldingService tilbakemeldingService;

    @PostMapping
    public Mono<Void> publish(@RequestBody TilbakemeldingDTO dto) {
        return tilbakemeldingService.publish(new Tilbakemelding(dto));
    }
}
