package no.nav.dolly.provider.api;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import no.nav.dolly.service.BrukerService;
import no.nav.dolly.service.excel.ExcelService;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static no.nav.dolly.util.CurrentAuthentication.getUserId;

@RestController
@RequestMapping("/api/v1/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;
    private final GetUserInfo getUserInfo;
    private final BrukerService brukerService;

    @SneakyThrows
    @GetMapping(value = "/gruppe/{gruppeId}")
    public ResponseEntity<Resource> getExcelsheet(@PathVariable Long gruppeId){

        var resource = excelService.getExcelWorkbook(gruppeId);

        return wrapContents(resource);
    }

    @SneakyThrows
    @GetMapping(value = "/organisasjoner")
    public ResponseEntity<Resource> getOrganisasjonExcelsheet(@RequestParam(required = false) String brukerId){

        var bruker = brukerService.fetchOrCreateBruker(StringUtils.isNotBlank(brukerId) ? brukerId : getUserId(getUserInfo));
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
}
