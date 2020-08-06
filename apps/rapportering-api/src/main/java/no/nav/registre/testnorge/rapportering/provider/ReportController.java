package no.nav.registre.testnorge.rapportering.provider;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.testnorge.rapportering.service.ReportService;

@RestController
@RequestMapping("/api/v1/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService service;

    @PostMapping("publish")
    public ResponseEntity<HttpStatus> triggerReport() {
        service.publishAll();
        return ResponseEntity.ok().build();
    }
}
