package no.nav.dolly.provider.api;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.service.ExcelService;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;

    @GetMapping(value = "/gruppe/{gruppeId}", produces = "application/vnd.ms-excel; charset=utf-8")
    public Resource getExcelsheet(@PathVariable Long gruppeId){

        return excelService.getExcelWorkbook(gruppeId);
    }
}
