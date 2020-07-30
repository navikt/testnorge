package no.nav.registre.aareg.provider.rs;

import lombok.RequiredArgsConstructor;
import no.nav.registre.aareg.provider.rs.requests.SyntetiserAaregRequest;
import no.nav.registre.aareg.provider.rs.response.RsAaregResponse;
import no.nav.registre.aareg.service.SyntetiseringService;
import no.nav.registre.aareg.syntetisering.RsAaregSyntetiseringsRequest;
import org.springframework.http.ResponseEntity;
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

    @PostMapping(value = "/generer")
    public ResponseEntity<List<RsAaregResponse>> genererArbeidsforholdsmeldinger(
            @RequestParam(defaultValue = "true") Boolean sendAlleEksisterende,
            @RequestBody SyntetiserAaregRequest syntetiserAaregRequest
    ) {
        return syntetiseringService.opprettArbeidshistorikkOgSendTilAaregstub(syntetiserAaregRequest, sendAlleEksisterende);
    }

    @PostMapping(value = "/sendTilAareg")
    public List<RsAaregResponse> sendArbeidsforholdTilAareg(
            @RequestParam(required = false, defaultValue = "false") Boolean fyllUtArbeidsforhold,
            @RequestBody List<RsAaregSyntetiseringsRequest> syntetiserteArbeidsforhold
    ) {
        return syntetiseringService.sendArbeidsforholdTilAareg(syntetiserteArbeidsforhold, fyllUtArbeidsforhold);
    }
}