package no.nav.registre.sam.provider.rs;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.sam.multitenancy.TenantContext;
import no.nav.registre.sam.service.IdentService;

@RestController
@RequestMapping("api/v1/ident")
@RequiredArgsConstructor
public class IdentController {

    private final IdentService identService;

    @ApiOperation(value = "Her kan man hente de identene fra en gitt liste som finnes i sam.")
    @PostMapping(value = "/filtrerIdenter/{miljoe}")
    public List<String> filtrerIdenter(
            @PathVariable String miljoe,
            @RequestBody List<String> identer
    ) {
        TenantContext.setTenant(miljoe);
        return identService.filtrerIdenter(identer);
    }
}
