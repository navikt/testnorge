package no.nav.no.registere.testnorge.arbeidsforholdexportapi.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.converter.csv.ArbeidsforholdSyntetiseringCsvConverter;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.converter.csv.PermisjonSyntetiseringCsvConverter;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.OpplysningspliktigList;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.service.ArbeidsforholdExportService;

@RestController
@RequestMapping("/api/v1/arbeidsforhold")
@RequiredArgsConstructor
public class ArbeidsforholdExportController {

    private final ArbeidsforholdExportService service;

    @GetMapping
    public ResponseEntity<HttpStatus> getArbeidsforhold(HttpServletResponse response) throws IOException {

        response.setContentType("text/csv");
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setHeader("Content-Disposition", "attachment; filename=syntetisering-arbeidesforhold-" + LocalDateTime.now() + ".csv");
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

//    @GetMapping()
//    public ResponseEntity<HttpStatus> convertArbeidsforholdFromFolder(@RequestHeader("folderPath") String folderPath, HttpServletResponse response) throws IOException {
//        OpplysningspliktigList list = OpplysningspliktigList.from(folderPath);
//
//        response.setContentType("text/csv");
//        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
//        response.setHeader("Content-Disposition", "attachment; filename=syntetisering-arbeidesforhold-" + LocalDateTime.now() + ".csv");
//        ArbeidsforholdSyntetiseringCsvConverter.inst().write(response.getWriter(), list.toArbeidsforhold());
//        return ResponseEntity.ok().build();
//    }
//
//    @GetMapping("/permisjoner")
//    public ResponseEntity<HttpStatus> convertPermisjonerFromFolder(@RequestHeader("folderPath") String folderPath, HttpServletResponse response) throws IOException {
//        OpplysningspliktigList list = OpplysningspliktigList.from(folderPath);
//
//        response.setContentType("text/csv");
//        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
//        response.setHeader("Content-Disposition", "attachment; filename=syntetisering-permisjoner-" + LocalDateTime.now() + ".csv");
//        PermisjonSyntetiseringCsvConverter.inst().write(response.getWriter(), list.toPermisjoner());
//        return ResponseEntity.ok().build();
//    }

}
