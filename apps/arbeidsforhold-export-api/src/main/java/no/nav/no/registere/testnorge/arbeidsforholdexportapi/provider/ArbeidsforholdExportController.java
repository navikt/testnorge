package no.nav.no.registere.testnorge.arbeidsforholdexportapi.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.converter.csv.ArbeidsforholdSyntentiseringCsvConverter;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.OpplysningspliktigList;

@Controller
@RequestMapping("/api/v1/arbeidsforhold")
@RequiredArgsConstructor
public class ArbeidsforholdExportController {


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HttpStatus> convert(@RequestParam("files") MultipartFile[] files, HttpServletResponse response) throws IOException {
        OpplysningspliktigList list = OpplysningspliktigList.from(files);

        response.setContentType("text/csv");
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setHeader("Content-Disposition", "attachment; filename=arbeidesforhold-syntentisering-" + LocalDateTime.now() + ".csv");
        ArbeidsforholdSyntentiseringCsvConverter.inst().write(response.getWriter(), list.toArbeidsforhold());
        return ResponseEntity.ok().build();
    }
}
