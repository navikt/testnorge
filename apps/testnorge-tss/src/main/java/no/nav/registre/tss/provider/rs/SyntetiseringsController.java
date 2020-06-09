package no.nav.registre.tss.provider.rs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.tss.domain.Samhandler;
import no.nav.registre.tss.domain.TssType;
import no.nav.registre.tss.provider.rs.request.SyntetiserTssRequest;
import no.nav.registre.tss.service.SyntetiseringService;

@Slf4j
@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class SyntetiseringsController {

    private final SyntetiseringService syntetiseringService;

    @PostMapping(value = "/opprettLeger")
    public ResponseEntity opprettLegerITss(@RequestBody SyntetiserTssRequest syntetiserTssRequest) {
        List<Samhandler> samhandlere = syntetiseringService.hentIdenter(syntetiserTssRequest).stream()
                .map(person -> new Samhandler(person, TssType.LE))
                .collect(Collectors.toList());
        List<String> syntetiskeTssRutiner = syntetiseringService.opprettSyntetiskeTssRutiner(samhandlere);

        syntetiseringService.sendTilTss(syntetiskeTssRutiner, syntetiserTssRequest.getMiljoe());

        return ResponseEntity.status(HttpStatus.OK).body(syntetiskeTssRutiner);
    }
}
