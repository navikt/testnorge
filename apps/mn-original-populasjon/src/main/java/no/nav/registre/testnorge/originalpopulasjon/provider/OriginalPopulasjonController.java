package no.nav.registre.testnorge.originalpopulasjon.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.testnav.libs.dto.person.v1.PersonDTO;
import no.nav.registre.testnorge.originalpopulasjon.consumer.HendelseConsumer;
import no.nav.registre.testnorge.originalpopulasjon.domain.Person;
import no.nav.registre.testnorge.originalpopulasjon.service.PopulasjonService;

@RequestMapping("api/v1/original-populasjon")
@RequiredArgsConstructor
@RestController
public class OriginalPopulasjonController {

    private final PopulasjonService populasjonService;
    private final HendelseConsumer hendelseConsumer;

    @PostMapping
    public List<PersonDTO> createPopulasjon(@RequestParam Integer antall) {
        var populasjon = populasjonService.createPopulasjon(antall);
        List<PersonDTO> personliste = populasjon.stream().map(Person::toDTO).collect(Collectors.toList());

        personliste.forEach(hendelseConsumer::registrertOpprettelseAvPerson);
        return personliste;
    }
}
