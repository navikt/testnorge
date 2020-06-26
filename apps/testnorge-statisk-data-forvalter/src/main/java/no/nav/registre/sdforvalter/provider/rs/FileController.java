package no.nav.registre.sdforvalter.provider.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

import no.nav.registre.sdforvalter.adapter.EregAdapter;
import no.nav.registre.sdforvalter.adapter.TpsIdenterAdapter;
import no.nav.registre.sdforvalter.converter.csv.EregCsvConverter;
import no.nav.registre.sdforvalter.converter.csv.TpsIdentCsvConverter;
import no.nav.registre.sdforvalter.domain.Ereg;
import no.nav.registre.sdforvalter.domain.EregListe;
import no.nav.registre.sdforvalter.domain.TpsIdent;
import no.nav.registre.sdforvalter.domain.TpsIdentListe;

@RestController
@RequestMapping("/api/v1/faste-data/file")
@RequiredArgsConstructor
public class FileController {

    private final EregAdapter eregAdapter;
    private final TpsIdenterAdapter tpsIdenterAdapter;

    @GetMapping("/ereg")
    public void exportEreg(@RequestParam(name = "gruppe", required = false) String gruppe, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setHeader("Content-Disposition", "attachment; filename=ereg-" + LocalDateTime.now().toString() + ".csv");
        EregListe eregListe = eregAdapter.fetchBy(gruppe);

        EregCsvConverter.inst().write(response.getWriter(), eregListe.getListe());
    }

    @PostMapping("/ereg")
    public ResponseEntity<HttpStatus> importEreg(@RequestParam("file") MultipartFile file) throws IOException {
        List<Ereg> list = EregCsvConverter.inst().read(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        eregAdapter.save(new EregListe(list));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tpsIdenter")
    public void exportTpsIdenter(@RequestParam(name = "gruppe", required = false) String gruppe, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setHeader("Content-Disposition", "attachment; filename=tpsIdent-" + LocalDateTime.now().toString() + ".csv");

        TpsIdentListe tpsIdentListe = tpsIdenterAdapter.fetchBy(gruppe);
        TpsIdentCsvConverter.inst().write(response.getWriter(), tpsIdentListe.getListe());
    }

    @PostMapping("/tpsIdenter")
    public ResponseEntity<?> importTpsIdenter(@RequestParam("file") MultipartFile file) throws IOException {
        List<TpsIdent> list = TpsIdentCsvConverter.inst().read(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        tpsIdenterAdapter.save(new TpsIdentListe(list));
        return ResponseEntity.ok().build();
    }
}