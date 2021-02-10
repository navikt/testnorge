package no.nav.no.registere.testnorge.arbeidsforholdexportapi.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.converter.csv.ArbeidsforholdSyntetiseringCsvConverter;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.converter.csv.PermisjonSyntetiseringCsvConverter;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.service.ArbeidsforholdExportService;

@Slf4j
@RestController
@RequestMapping("/api/v1/arbeidsforhold")
@RequiredArgsConstructor
public class ArbeidsforholdExportController {

    private final ArbeidsforholdExportService service;

    @GetMapping
    public ResponseEntity<HttpStatus> getArbeidsforhold(@RequestHeader("page") Integer page, @RequestHeader("pageSize") Integer pageSize, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setHeader("Content-Disposition", "attachment; filename=syntetisering-arbeidesforhold-" + LocalDateTime.now() + ".csv");
        ArbeidsforholdSyntetiseringCsvConverter.inst().write(response.getWriter(), service.getArbeidsforhold());
        return ResponseEntity.ok().build();
    }


    @GetMapping("/test")
    public ResponseEntity<HttpStatus> getArbeidsforhold(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setHeader("Content-Disposition", "attachment; filename=syntetisering-arbeidesforhold-" + LocalDateTime.now() + ".csv");

        File file = service.getArbeidsforholdFile();
        log.info("Leser fil {}.",file.getPath());
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            response.getWriter().write(line);
        }

        ArbeidsforholdSyntetiseringCsvConverter.inst().write(response.getWriter(), service.getArbeidsforhold());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/permisjoner")
    public ResponseEntity<HttpStatus> gerPermisjoner(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setHeader("Content-Disposition", "attachment; filename=syntetisering-permisjoner-" + LocalDateTime.now() + ".csv");
        PermisjonSyntetiseringCsvConverter.inst().write(response.getWriter(), service.getPermisjoner());
        return ResponseEntity.ok().build();
    }
}
