package no.nav.dolly.provider;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import no.nav.dolly.service.BrukerService;
import no.nav.dolly.service.excel.ExcelService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;
    private final BrukerService brukerService;

    @SneakyThrows
    @GetMapping(value = "/gruppe/{gruppeId}")
    public Mono<ResponseEntity<Resource>> getExcelsheet(@PathVariable Long gruppeId) {

        var resource = excelService.getExcelWorkbook(gruppeId);

        return wrapContents(resource);
    }

    @SneakyThrows
    @GetMapping(value = "/organisasjoner")
    @Transactional(readOnly = true)
    public ResponseEntity<Resource> getOrganisasjonExcelsheet(@RequestParam(required = false) String brukerId) {

        var bruker = brukerService.fetchOrCreateBruker(brukerId);
        var resource = excelService.getExcelOrganisasjonerWorkbook(bruker);

        return wrapContents(resource);
    }

    private ResponseEntity<Resource> wrapContents(Resource resource) throws IOException {

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + resource.getFilename())
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }

    private Mono<ResponseEntity<Resource>> wrapContents(Mono<Resource> resource) {

        return resource
                .handle((resource1, sink) -> {
                    try {
                        sink.next(ResponseEntity.ok()
                                .header("Content-Disposition", "attachment; filename=" + resource1.getFilename())
                                .contentLength(resource1.contentLength())
                                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                                .body(resource1));
                    } catch (IOException e) {
                        sink.error(new RuntimeException(e));
                    }
                });
    }
}
