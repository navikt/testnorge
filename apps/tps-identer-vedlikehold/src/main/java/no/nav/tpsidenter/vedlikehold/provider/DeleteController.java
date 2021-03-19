package no.nav.tpsidenter.vedlikehold.provider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import no.nav.tpsidenter.vedlikehold.service.TpsfService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/testdata")
@RequiredArgsConstructor
public class DeleteController {

    private final TpsfService tpsfService;

    @DeleteMapping(value = "/upload", consumes = "multipart/form-data")
    @Operation(description = "Start sletting av identer fra opplastet file")
    public String startDelete(@Parameter(description = "Angi CSV-file for opplasting")
                                        @RequestPart("file") MultipartFile identerFile) {

        return tpsfService.deleteIdents(identerFile);
    }
}
