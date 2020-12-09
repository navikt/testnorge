package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.ArbeidsforholdHistorikk;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.repository.ArbeidsforholdHistorikkRepository;

@Component
@RequiredArgsConstructor
public class ArbeidsforholdHistorikkAdapter {
    private final ArbeidsforholdHistorikkRepository repository;

    public ArbeidsforholdHistorikk get(String arbeidsforholdId) {
        var model = repository
                .findById(arbeidsforholdId)
                .orElseGet(() -> repository.save(new ArbeidsforholdHistorikk(arbeidsforholdId).toModel()));
        return new ArbeidsforholdHistorikk(model);
    }

    public void save(String arbeidsforholdId, String historikk) {
        repository.save(new ArbeidsforholdHistorikk(arbeidsforholdId, historikk).toModel());
    }
}
