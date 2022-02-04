package no.nav.dolly.provider.api;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import no.nav.dolly.service.excel.ExcelService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;

    @SneakyThrows
    @GetMapping(value = "/gruppe/{gruppeId}")
    public ResponseEntity<Resource> getExcelsheet(@PathVariable Long gruppeId){

        var resource = excelService.getExcelWorkbook(gruppeId);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + resource.getFilename())
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }
}
