package no.nav.registre.testnorge.organisasjonmottakservice.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.testnorge.organisasjonmottakservice.domain.Organiasjon;
import no.nav.registre.testnorge.organisasjonmottakservice.service.OrganiasjonService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/organiasjonmottak")
public class OrganiasjonMottakController {
    private final OrganiasjonService organiasjonService;

    @PostMapping
    private void test(@RequestBody Organiasjon organiasjon) {
        organiasjonService.save(organiasjon, "T4");
    }
}
