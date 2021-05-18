package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class OrkestratorService {
    private final IdentService identService;
    private final PersonArbeidsforholdHistorkkService personArbeidsforholdHistorkkService;
    private final OppsummeringsdokumentService oppsummeringsdokumentService;

    public void orkester(int max, String miljo, LocalDate fom, LocalDate tom) {
        var identerUtenArbeidsforhold = identService.getIdenterUtenArbeidsforhold(miljo, max);
        var personer = personArbeidsforholdHistorkkService.generer(identerUtenArbeidsforhold, miljo, fom, tom);
        oppsummeringsdokumentService.save(personer, miljo, fom, tom);
    }

}
