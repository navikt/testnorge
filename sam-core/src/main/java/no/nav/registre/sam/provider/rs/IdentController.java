package no.nav.registre.sam.provider.rs;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.sam.service.IdentService;

@RestController
@RequestMapping("api/v1/ident")
@RequiredArgsConstructor
public class IdentController {

    private final IdentService identService;

    @LogExceptions
    @ApiOperation(value = "Her kan man hente de identene fra en gitt liste som finnes i sam.")
    @PostMapping(value = "/filtrerIdenter")
    public List<String> filtrerIdenter(
            @RequestBody List<String> identer
    ) {
        return identService.filtrerIdenter(identer);
    }
}
