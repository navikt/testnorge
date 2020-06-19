package no.nav.registre.testnorge.synt.sykemelding.provider;

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
import no.nav.registre.testnorge.common.session.NavSession;
import no.nav.registre.testnorge.dto.synt.sykemelding.v1.SyntSykemeldingListeDTO;
import no.nav.registre.testnorge.synt.sykemelding.service.SykemeldingService;

@RestController
@RequestMapping("/api/v1/synt-sykemelding")
@RequiredArgsConstructor
public class SyntSykemeldingController {
    private final SykemeldingService service;

    @PostMapping
    public ResponseEntity<HttpStatus> opprett(@RequestHeader(NavHeaders.UUID) String uuid, @RequestBody SyntSykemeldingListeDTO liste) {
        MDC.put(NavHeaders.UUID, uuid);
        service.opprettSykemelding(liste, new NavSession(uuid));
        return ResponseEntity.ok().header(NavHeaders.UUID, uuid).build();
    }
}