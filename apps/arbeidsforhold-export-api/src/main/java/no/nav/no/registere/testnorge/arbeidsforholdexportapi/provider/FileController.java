package no.nav.no.registere.testnorge.arbeidsforholdexportapi.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping("/api/v1/files/tmp")
public class FileController {

    @GetMapping
    public ResponseEntity<List<String>> getFiles() throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get("/tmp"))) {
            return ResponseEntity.ok(paths
                    .filter(Files::isRegularFile)
                    .map(value -> value.getFileName().toString())
                    .filter(value -> value.endsWith(".csv"))
                    .collect(Collectors.toList())
            );
        }
    }

    @GetMapping("/{filename}")
    public ResponseEntity<?> getFile(@PathVariable("filename") String filename) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get("/tmp"))) {
            var path = paths.filter(Files::isRegularFile).filter(value -> value.getFileName().toString().equals(filename)).findFirst();

            if (path.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            var resource = new UrlResource(path.get().toUri());

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=export-" + LocalDateTime.now() + ".csv")
                    .body(resource);
        }
    }
}
