package no.nav.registre.testnorge.elsam.provider.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import no.nav.registre.elsam.domain.SykmeldingRequest;
import no.nav.registre.testnorge.elsam.exception.InvalidEnvironmentException;
import no.nav.registre.testnorge.elsam.service.SyfoMqService;
import no.nav.registre.testnorge.elsam.service.SykmeldingService;

@RestController
@RequestMapping("api/v1/sykmelding")
@Slf4j
public class SykmeldingController {

    @Autowired
    private SykmeldingService sykmeldingService;

    @Autowired
    private SyfoMqService syfoMqService;

    @PostMapping
    public ResponseEntity<String> opprettSykmelding(
            @RequestParam String miljoe,
            @RequestBody SykmeldingRequest sykmeldingRequest
    ) throws IOException {
        var sykemelding = sykmeldingService.opprettSykmelding(sykmeldingRequest);
        try {
            syfoMqService.opprettSykmeldingNyttMottak(miljoe, sykemelding.toXml());
            return ResponseEntity.ok().body(sykemelding.toXml());
        } catch (InvalidEnvironmentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
