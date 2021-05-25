package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrkestratorService {
    private final IdentService identService;
    private final PersonArbeidsforholdHistorkkService personArbeidsforholdHistorkkService;
    private final OppsummeringsdokumentService oppsummeringsdokumentService;

    public List<String> orkestrerUtenArbeidsforhold(int max, String miljo, LocalDate fom, LocalDate tom) {
        var identerUtenArbeidsforhold = identService.getIdenterUtenArbeidsforhold(miljo, max);
        var personer = personArbeidsforholdHistorkkService.generer(identerUtenArbeidsforhold, miljo, fom, tom);
        return oppsummeringsdokumentService.save(personer, miljo);
    }

    public List<String> orkestrerMedArbeidsforhold(String miljo, int months) {
        var identerMedArbeidsforhold = identService.getIdenterMedArbeidsforhold(miljo);
        var personer = personArbeidsforholdHistorkkService.generer(Flux.fromStream(identerMedArbeidsforhold.stream()), miljo, months);
        return oppsummeringsdokumentService.save(personer, miljo);
    }

}
