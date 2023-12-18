package no.nav.registre.sdforvalter.provider.rs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import no.nav.registre.sdforvalter.adapter.EregAdapter;
import no.nav.registre.sdforvalter.converter.csv.EregCsvConverter;
import no.nav.registre.sdforvalter.domain.Ereg;
import no.nav.registre.sdforvalter.domain.EregListe;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.Gruppe;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/faste-data/file")
@Tag(
        name = "FileController",
        description = "Endepunkter som bruker csv/Excel for å hente/legge til data i Team Dollys interne oversikt over faste data"
)
@RequiredArgsConstructor
public class FileController {

    private final EregAdapter eregAdapter;

    @Operation(summary = "Hent organisasjoner fra Team Dollys database")
    @GetMapping("/ereg")
    public void exportEreg(@RequestParam(name = "gruppe", required = false) Gruppe gruppe, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setHeader("Content-Disposition", "attachment; filename=ereg-" + LocalDateTime.now() + ".csv");
        EregListe eregListe = eregAdapter.fetchBy(gruppe != null ? gruppe.name() : null);

        EregCsvConverter.inst().write(response.getWriter(), eregListe.getListe());
    }

    @Operation(summary = "Legg til organisasjoner i Team Dollys database")
    @PostMapping(path = "/ereg", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HttpStatus> importEreg(@RequestParam("file") MultipartFile file) throws IOException {
        List<Ereg> list = EregCsvConverter.inst().read(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        eregAdapter.save(new EregListe(list));
        return ResponseEntity.ok().build();
    }
}