package no.nav.registre.testnorge.tilbakemeldingapi.provider;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.tilbakemeldingapi.v1.TilbakemeldingDTO;
import no.nav.registre.testnorge.tilbakemeldingapi.domain.Tilbakemelding;
import no.nav.registre.testnorge.tilbakemeldingapi.service.TilbakemeldingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tilbakemelding")
public class TilbakemeldingController {
    private final TilbakemeldingService tilbakemeldingService;

    @PostMapping
    public ResponseEntity<HttpStatus> publish(@RequestBody TilbakemeldingDTO dto) {
        tilbakemeldingService.publish(new Tilbakemelding(dto));
        return ResponseEntity.ok().build();
    }
}
