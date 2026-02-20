package no.nav.registre.sdforvalter.provider.rs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import no.nav.registre.sdforvalter.adapter.EregAdapter;
import no.nav.registre.sdforvalter.converter.csv.EregCsvConverter;
import no.nav.registre.sdforvalter.domain.Ereg;
import no.nav.registre.sdforvalter.domain.EregListe;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.Gruppe;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
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
    public Mono<ResponseEntity<DataBuffer>> exportEreg(@RequestParam(name = "gruppe", required = false) Gruppe gruppe) {
        return Mono.fromCallable(() -> {
                    EregListe eregListe = eregAdapter.fetchBy(gruppe != null ? gruppe.name() : null);
                    StringWriter writer = new StringWriter();
                    EregCsvConverter.inst().write(new PrintWriter(writer), eregListe.getListe());
                    byte[] bytes = writer.toString().getBytes(StandardCharsets.UTF_8);
                    DataBuffer buffer = new DefaultDataBufferFactory().wrap(bytes);
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ereg-" + LocalDateTime.now() + ".csv")
                            .body(buffer);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Operation(summary = "Legg til organisasjoner i Team Dollys database")
    @PostMapping(path = "/ereg", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<HttpStatus>> importEreg(@RequestPart("file") FilePart file) {
        return file.content()
                .reduce(new DefaultDataBufferFactory().allocateBuffer(), (accumulator, buffer) -> {
                    accumulator.write(buffer);
                    return accumulator;
                })
                .flatMap(dataBuffer -> Mono.fromCallable(() -> {
                    List<Ereg> list = EregCsvConverter.inst().read(
                            new InputStreamReader(dataBuffer.asInputStream(), StandardCharsets.UTF_8));
                    eregAdapter.save(new EregListe(list));
                    return ResponseEntity.ok().<HttpStatus>build();
                }).subscribeOn(Schedulers.boundedElastic()));
    }
}