package no.nav.registre.testnorge.organisasjonmottak.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.testnorge.organisasjonmottak.domain.Navn;
import no.nav.registre.testnorge.organisasjonmottak.domain.Organiasjon;
import no.nav.registre.testnorge.organisasjonmottak.service.OrganiasjonService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/organiasjonmottak")
public class OrganiasjonMottakController {
    private final OrganiasjonService organiasjonService;

    @PostMapping("/org")
    public void setOrganiasjon(@RequestBody Organiasjon organiasjon) {
        organiasjonService.save(organiasjon, "T4");
    }

    @PostMapping("/navn")
    public void setNavn(@RequestBody Navn navn) {
        organiasjonService.save(navn, "T4");
    }
}
