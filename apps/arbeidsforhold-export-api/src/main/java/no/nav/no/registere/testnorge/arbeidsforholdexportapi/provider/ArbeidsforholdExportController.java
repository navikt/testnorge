package no.nav.no.registere.testnorge.arbeidsforholdexportapi.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.service.ArbeidsforholdExportService;

@Slf4j
@RestController
@RequestMapping("/api/v1/arbeidsforhold")
@RequiredArgsConstructor
public class ArbeidsforholdExportController {

    private static final String NUMBER_OF_PAGES_HEADER = "NUMBER_OF_PAGES";
    private final ArbeidsforholdExportService service;

    @GetMapping
    public ResponseEntity<?> getArbeidsforhold() throws IOException {
        var path = service.writeArbeidsforhold();
        var resource = new UrlResource(path.toUri());
        log.info("Arbeidsforhold lasted ned.");
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=syntetisering-arbeidesforhold-" + LocalDateTime.now() + ".csv")
                .body(resource);
    }

    @GetMapping("/{page}")
    public ResponseEntity<?> getArbeidsforhold(@PathVariable("page") Integer page) throws IOException {
        var path = service.writeArbeidsforhold(page);
        var resource = new UrlResource(path.toUri());
        log.info("Arbeidsforhold lasted ned.");
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=syntetisering-arbeidesforhold-" + page + "-" + LocalDateTime.now() + ".csv")
                .body(resource);
    }

    @GetMapping("/permisjoner")
    public ResponseEntity<HttpStatus> gerPermisjoner(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setHeader("Content-Disposition", "attachment; filename=syntetisering-permisjoner-" + LocalDateTime.now() + ".csv");
        service.writePermisjoner(response.getWriter());
        log.info("Permisjoner lasted ned.");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/permisjoner/{page}")
    public ResponseEntity<HttpStatus> gerPermisjoner(@PathVariable("page") Integer page, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setHeader("Content-Disposition", "attachment; filename=syntetisering-permisjoner-" + page + "-" + LocalDateTime.now() + ".csv");
        var numberOfPages = service.writePermisjoner(response.getWriter(), page);
        log.info("Permisjoner lasted ned.");
        return ResponseEntity.ok().header(NUMBER_OF_PAGES_HEADER, numberOfPages.toString()).build();
    }
}
