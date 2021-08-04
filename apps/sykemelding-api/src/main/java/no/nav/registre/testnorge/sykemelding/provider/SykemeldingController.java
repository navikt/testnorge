package no.nav.registre.testnorge.sykemelding.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingDTO;
import no.nav.registre.testnorge.sykemelding.domain.ApplicationInfo;
import no.nav.registre.testnorge.sykemelding.domain.Sykemelding;
import no.nav.registre.testnorge.sykemelding.service.SykemeldingService;

@RestController
@RequestMapping("/api/v1/sykemeldinger")
@RequiredArgsConstructor
public class SykemeldingController {

    private final SykemeldingService service;
    private final ApplicationInfo applicationInfo;

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody SykemeldingDTO dto) {
        service.send(new Sykemelding(dto, applicationInfo));
        return ResponseEntity.ok().build();
    }
}
