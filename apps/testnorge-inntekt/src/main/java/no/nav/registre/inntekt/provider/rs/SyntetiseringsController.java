package no.nav.registre.inntekt.provider.rs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import no.nav.registre.inntekt.domain.inntektstub.RsInntekt;
import no.nav.registre.inntekt.provider.rs.requests.SyntetiseringsRequest;
import no.nav.registre.inntekt.service.SyntetiseringService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SyntetiseringsController {

    private final SyntetiseringService syntetiseringService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/syntetisering/generer")
    public Map<String, List<RsInntekt>> genererSyntetiserteInntektsmeldinger(
            @RequestParam(required = false, defaultValue = "true") Boolean opprettPaaEksisterende,
            @RequestBody SyntetiseringsRequest syntetiseringsRequest) {
        return syntetiseringService.startSyntetisering(syntetiseringsRequest, opprettPaaEksisterende);
    }
}
