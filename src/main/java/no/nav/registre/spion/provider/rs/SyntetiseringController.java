package no.nav.registre.spion.provider.rs;

import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import no.nav.registre.spion.domain.Vedtak;
import no.nav.registre.spion.service.VedtakService;

@RestController
@RequestMapping("api/v1/syntetisering")
@AllArgsConstructor
public class SyntetiseringController {

    private final VedtakService vedtakService;

    @PostMapping(value = "/genererVedtak")
    public List<Vedtak> genererVedtak(@RequestParam int antallNyeVedtak){
        return vedtakService.generateVedtak(antallNyeVedtak);
    }
}
