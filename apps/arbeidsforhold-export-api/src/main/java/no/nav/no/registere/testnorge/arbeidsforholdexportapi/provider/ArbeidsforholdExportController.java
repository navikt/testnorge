package no.nav.no.registere.testnorge.arbeidsforholdexportapi.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.service.ArbeidsforholdExportService;

@Slf4j
@RestController
@RequestMapping("/api/v1/arbeidsforhold")
@RequiredArgsConstructor
public class ArbeidsforholdExportController {
    private final ArbeidsforholdExportService service;

    @GetMapping
    public ResponseEntity<HttpStatus> getArbeidsforhold() throws IOException {
        var path = service.writeArbeidsforhold();
        return ResponseEntity.created(URI.create("/api/v1/files/tmp/" + path.getFileName().toString())).build();
    }

    @GetMapping("/permisjoner")
    public ResponseEntity<HttpStatus> getPermisjoner() throws IOException {
        var path = service.writePermisjoner();
        return ResponseEntity.created(URI.create("/api/v1/files/tmp/" + path.getFileName().toString())).build();
    }

    @GetMapping("/inntekter")
    public ResponseEntity<HttpStatus> getInntekter() throws IOException {
        var path = service.writeInntekter();
        return ResponseEntity.created(URI.create("/api/v1/files/tmp/" + path.getFileName().toString())).build();
    }
}
