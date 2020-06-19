package no.nav.registre.testnorge.sykemelding.provider;

import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.testnorge.common.headers.NavHeaders;
import no.nav.registre.testnorge.dto.sykemelding.v1.SykemeldingDTO;
import no.nav.registre.testnorge.sykemelding.consumer.SyfoConsumer;
import no.nav.registre.testnorge.sykemelding.domain.ApplicationInfo;
import no.nav.registre.testnorge.sykemelding.domain.Sykemelding;

@RestController
@RequestMapping("/api/v1/sykemeldinger")
@RequiredArgsConstructor
public class SykemeldingController {

    private final SyfoConsumer syfoConsumer;
    private final ApplicationInfo applicationInfo;

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestHeader(NavHeaders.UUID) String uuid, @RequestBody SykemeldingDTO dto) {
        MDC.put(NavHeaders.UUID, uuid);
        syfoConsumer.send(new Sykemelding(dto, applicationInfo));
        return ResponseEntity.ok().header(NavHeaders.UUID, uuid).build();
    }
}
