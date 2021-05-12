package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.List;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.Person;

@Service
@RequiredArgsConstructor
public class OrkestratorService {
    private final IdentService identService;
    private final PersonArbeidsforholdHistorkkService personArbeidsforholdHistorkkService;

    public Flux<Person> orkester(int max, String miljo, LocalDate fom, LocalDate tom) {
        var identerUtenArbeidsforhold = identService.getIdenterUtenArbeidsforhold(miljo, max);
        return personArbeidsforholdHistorkkService.generer(identerUtenArbeidsforhold, miljo, fom, tom);
    }

}
