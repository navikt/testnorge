package no.nav.registre.sdForvalter.provider.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import no.nav.registre.sdForvalter.adapter.EregAdapter;
import no.nav.registre.sdForvalter.converter.csv.EregCsvConverter;
import no.nav.registre.sdForvalter.domain.Ereg;
import no.nav.registre.sdForvalter.domain.EregListe;

@RestController
@RequestMapping("/api/v1/faste-data/file")
@RequiredArgsConstructor
public class FileController {

    private final EregAdapter eregAdapter;

    @GetMapping("/ereg")
    public void exportEreg(@RequestParam(name = "gruppe", required = false) String gruppe, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=ereg-" + LocalDateTime.now().toString() + ".csv");
        EregListe eregListe = eregAdapter.fetchBy(gruppe);

        EregCsvConverter.inst().write(response.getWriter(), eregListe.getListe());
    }

    @PostMapping("/ereg")
    public ResponseEntity<?> importEreg(@RequestParam("file") MultipartFile file) throws IOException {
        List<Ereg> list = EregCsvConverter.inst().read(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        eregAdapter.save(new EregListe(list));
        return ResponseEntity.ok().build();
    }

}