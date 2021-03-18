package no.nav.registre.testnorge.synt.sykemelding.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.libs.dto.synt.sykemelding.v1.SyntSykemeldingDTO;
import no.nav.registre.testnorge.synt.sykemelding.service.SykemeldingService;
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
    private final SykemeldingService service;

    @PostMapping
    public ResponseEntity<HttpStatus> opprett(@RequestBody SyntSykemeldingDTO sykemelding) {
        service.opprettSykemelding(sykemelding);
        return ResponseEntity.ok().build();
    }

    @ControllerAdvice
    public static class ExceptionHandlerAdvice {

        @ExceptionHandler(HttpClientErrorException.class)
        public ResponseEntity handleException(HttpClientErrorException e) {
            log.error("Klarte ikke å finne arbeidsforhold: ", e);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Klarte ikke å finne arbeidsforhold");
        }
    }
}