package no.nav.registre.spion.provider.rs;

import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import no.nav.registre.spion.domain.Vedtak;
import no.nav.registre.spion.provider.rs.request.SyntetiserSpionRequest;
import no.nav.registre.spion.service.SyntetiseringService;

@RestController
@RequestMapping("api/v1/syntetisering")
@AllArgsConstructor
public class SyntetiseringController {

    private final SyntetiseringService syntetiseringService;

    @PostMapping(value = "/genererVedtak")
    public List<Vedtak> genererVedtak(@RequestBody SyntetiserSpionRequest request){
        return syntetiseringService.syntetiserVedtak(request);
    }
}
