package no.nav.registre.inntekt.provider.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import no.nav.inntektstub.domain.rs.RsInntekt;
import no.nav.registre.inntekt.provider.rs.requests.SyntetiseringsRequest;
import no.nav.registre.inntekt.service.SyntetiseringService;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class SyntetiseringsController {

    @Autowired
    private SyntetiseringService syntetiseringService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/syntetisering/generer")
    public ResponseEntity<Map<String, List<RsInntekt>>> genererSyntetiserteInntektsmeldinger(@RequestBody SyntetiseringsRequest syntetiseringsRequest) {
        syntetiseringService.startSyntetisering(syntetiseringsRequest);
        return ResponseEntity.ok().build();
    }
}
