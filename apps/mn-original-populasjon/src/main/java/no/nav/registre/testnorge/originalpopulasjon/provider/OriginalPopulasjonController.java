package no.nav.registre.testnorge.originalpopulasjon.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.person.v1.PersonDTO;
import no.nav.registre.testnorge.originalpopulasjon.domain.Person;
import no.nav.registre.testnorge.originalpopulasjon.service.PopulasjonService;

@RequestMapping("api/v1/original-populasjon")
@RequiredArgsConstructor
@RestController
public class OriginalPopulasjonController {

    private final PopulasjonService populasjonService;

    @GetMapping
    public List<PersonDTO> createPopulasjon(@RequestParam Integer antall) {
        var populasjon = populasjonService.createPopulasjon(antall);
        return populasjon.stream().map(Person::toDTO).collect(Collectors.toList());
    }
}
