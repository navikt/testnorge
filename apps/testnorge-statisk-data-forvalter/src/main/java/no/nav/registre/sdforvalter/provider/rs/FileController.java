package no.nav.registre.sdforvalter.provider.rs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.sdforvalter.adapter.EregAdapter;
import no.nav.registre.sdforvalter.adapter.TpsIdenterAdapter;
import no.nav.registre.sdforvalter.converter.csv.EregCsvConverter;
import no.nav.registre.sdforvalter.converter.csv.TpsIdentCsvConverter;
import no.nav.registre.sdforvalter.domain.Ereg;
import no.nav.registre.sdforvalter.domain.EregListe;
import no.nav.registre.sdforvalter.domain.TpsIdent;
import no.nav.registre.sdforvalter.domain.TpsIdentListe;
import no.nav.registre.sdforvalter.service.IdentService;

@RestController
@RequestMapping("/api/v1/faste-data/file")
@Tag(
        name = "FileController",
        description = "Endepunkter som bruker csv/Excel for å hente/legge til data i Team Dollys interne oversikt over faste data"
)
@RequiredArgsConstructor
public class FileController {

    private final EregAdapter eregAdapter;
    private final TpsIdenterAdapter tpsIdenterAdapter;
    private final IdentService identService;

    @Operation(summary = "Hent organisasjoner fra Team Dollys database")
    @GetMapping("/ereg")
    public void exportEreg(@RequestParam(name = "gruppe", required = false) String gruppe, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setHeader("Content-Disposition", "attachment; filename=ereg-" + LocalDateTime.now().toString() + ".csv");
        EregListe eregListe = eregAdapter.fetchBy(gruppe);

        EregCsvConverter.inst().write(response.getWriter(), eregListe.getListe());
    }

    @Operation(summary = "Legg til organisasjoner i Team Dollys database")
    @PostMapping(path = "/ereg", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HttpStatus> importEreg(@RequestParam("file") MultipartFile file) throws IOException {
        List<Ereg> list = EregCsvConverter.inst().read(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        eregAdapter.save(new EregListe(list));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Hent personer fra Team Dollys database")
    @GetMapping("/tpsIdenter")
    public void exportTpsIdenter(@RequestParam(name = "gruppe", required = false) String gruppe, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setHeader("Content-Disposition", "attachment; filename=tpsIdent-" + LocalDateTime.now().toString() + ".csv");

        TpsIdentListe tpsIdentListe = tpsIdenterAdapter.fetchBy(gruppe);
        TpsIdentCsvConverter.inst().write(response.getWriter(), tpsIdentListe.getListe());
    }

    @Operation(summary = "Legg til personer i Team Dollys database")
    @PostMapping(path = "/tpsIdenter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importTpsIdenter(@RequestParam("file") MultipartFile file,
                                              @Parameter(description = "Hvis true settes tilfeldig navn på personer uten fornavn og etternavn")
                                              @RequestParam(name = "Generer manglende navn", defaultValue = "false") Boolean genererManglendeNavn) throws IOException {
        List<TpsIdent> list = TpsIdentCsvConverter.inst().read(cleanInput(file.getInputStream()));

        identService.save(new TpsIdentListe(list), genererManglendeNavn);
        return ResponseEntity.ok().build();
    }

    private Reader cleanInput(InputStream inputStream) {
        String character = "\uFEFF";
        return new StringReader(
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n")).replace(character, "")
        );
    }

}