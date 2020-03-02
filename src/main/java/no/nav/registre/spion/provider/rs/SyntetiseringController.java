package no.nav.registre.spion.provider.rs;

import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.spion.provider.rs.request.SyntetiserSpionRequest;

import lombok.AllArgsConstructor;
import no.nav.registre.spion.domain.Vedtak;
import no.nav.registre.spion.provider.rs.response.SyntetiserSpionResponse;
import no.nav.registre.spion.service.SyntetiseringService;
import no.nav.registre.spion.service.VedtakPublisher;

@RestController
@RequestMapping("api/v1/syntetisering")
@AllArgsConstructor
public class SyntetiseringController {

    private final SyntetiseringService syntetiseringService;

    private final VedtakPublisher vedtakPublisher;

    @PostMapping(value = "/vedtak")
    public SyntetiserSpionResponse genererVedtak(@RequestBody SyntetiserSpionRequest request){
        List<Vedtak> genererteVedtak = syntetiseringService.syntetiserVedtak(request);

        return vedtakPublisher.publish(genererteVedtak);
    }

}
