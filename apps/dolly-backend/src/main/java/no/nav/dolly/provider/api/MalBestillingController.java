package no.nav.dolly.provider.api;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingMal;
import no.nav.dolly.service.MalBestillingService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/api/v1/malbestilling")
@RestController
@RequiredArgsConstructor
public class MalBestillingController {

    private final MalBestillingService malBestillingService;

    @PostMapping(value = "/ident/{ident}")
    public RsBestillingMal createTemplateFromIdent(@PathVariable String ident,
                                                   @RequestParam String name) {

        return malBestillingService.createFromIdent(ident, name);
    }
}
