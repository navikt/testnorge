package no.nav.dolly.budpro.organisasjonsenhet;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/enhet")
@RequiredArgsConstructor
public class OrganisasjonsenhetController {

    private final OrganisasjonsenhetService service;

    @GetMapping("/all")
    List<Organisasjonsenhet> getAll() {
        return service.getAll();
    }

}
