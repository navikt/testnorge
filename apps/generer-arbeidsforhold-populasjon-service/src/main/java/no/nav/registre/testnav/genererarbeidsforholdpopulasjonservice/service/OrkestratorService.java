package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class OrkestratorService {
    private final IdentService identService;
    private final PersonArbeidsforholdHistorkkService personArbeidsforholdHistorkkService;
    private final OppsummeringsdokumentService oppsummeringsdokumentService;

    public void orkestrerUtenArbeidsforhold(int max, String miljo, LocalDate fom, LocalDate tom) {
        var identerUtenArbeidsforhold = identService.getIdenterUtenArbeidsforhold(miljo, max);
        var personer = personArbeidsforholdHistorkkService.generer(identerUtenArbeidsforhold, miljo, fom, tom);
        oppsummeringsdokumentService.save(personer, miljo);
    }

    public void orkestrerMedArbeidsforhold(String miljo, int months) {
        var identerMedArbeidsforhold = identService.getIdenterMedArbeidsforhold(miljo);
        var personer = personArbeidsforholdHistorkkService.generer(Flux.fromStream(identerMedArbeidsforhold.stream()), miljo, months);
        oppsummeringsdokumentService.save(personer, miljo);
    }

}
