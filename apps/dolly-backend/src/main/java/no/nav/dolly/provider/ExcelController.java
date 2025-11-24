package no.nav.dolly.provider;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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

@RestController
@RequestMapping("/api/v1/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;

    @SneakyThrows
    @GetMapping(value = "/gruppe/{gruppeId}")
    public Mono<ResponseEntity<Resource>> getExcelsheet(@PathVariable Long gruppeId) {

        return excelService.getExcelWorkbook(gruppeId)
                .map(this::wrapContents);
    }

    @SneakyThrows
    @GetMapping(value = "/organisasjoner")
    @Transactional
    public Mono<ResponseEntity<Resource>> getOrganisasjonExcelsheet(@RequestParam(required = false) String brukerId) {

        return excelService.getExcelOrganisasjonerWorkbook(brukerId)
                .map(this::wrapContents);
    }

    @SneakyThrows
    private ResponseEntity<Resource> wrapContents(Resource resource) {

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + resource.getFilename())
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }
}
