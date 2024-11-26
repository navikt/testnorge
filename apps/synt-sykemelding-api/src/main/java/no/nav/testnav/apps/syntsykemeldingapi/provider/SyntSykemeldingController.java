package no.nav.testnav.apps.syntsykemeldingapi.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.testnav.apps.syntsykemeldingapi.service.SykemeldingService;
import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldningResponseDTO;
import no.nav.testnav.libs.dto.synt.sykemelding.v1.SyntSykemeldingDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@Slf4j
@RequestMapping("/api/v1/synt-sykemelding")
@RequiredArgsConstructor
public class SyntSykemeldingController {
    private final SykemeldingService sykemeldingService;

    @PostMapping
    public SykemeldningResponseDTO opprett(@RequestBody SyntSykemeldingDTO sykemelding) {
        
        return sykemeldingService.opprettSykemelding(sykemelding);
    }
}