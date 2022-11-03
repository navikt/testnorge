package no.nav.testnav.apps.syntaaregservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.syntaaregservice.domain.synt.RsAaregSyntetiseringsRequest;
import no.nav.testnav.apps.syntaaregservice.provider.response.RsAaregResponse;
import no.nav.testnav.apps.syntaaregservice.service.SyntetiseringService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class SyntetiseringController {

    private final SyntetiseringService syntetiseringService;

    @PostMapping(value = "/sendTilAareg")
    public List<RsAaregResponse> sendArbeidsforholdTilAareg(
            @RequestParam(required = false, defaultValue = "false") Boolean fyllUtArbeidsforhold,
            @RequestBody List<RsAaregSyntetiseringsRequest> syntetiserteArbeidsforhold
    ) {
        return syntetiseringService.sendArbeidsforholdTilAareg(syntetiserteArbeidsforhold, fyllUtArbeidsforhold);
    }
}