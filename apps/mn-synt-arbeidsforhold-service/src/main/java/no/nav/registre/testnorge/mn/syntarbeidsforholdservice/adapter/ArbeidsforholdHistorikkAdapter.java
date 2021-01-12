package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.ArbeidsforholdHistorikk;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.repository.ArbeidsforholdHistorikkRepository;

@Component
@RequiredArgsConstructor
public class ArbeidsforholdHistorikkAdapter {
    private final ArbeidsforholdHistorikkRepository repository;

    public List<ArbeidsforholdHistorikk> getAllFrom(String miljo) {
        return repository.findAllByMiljo(miljo).stream().map(ArbeidsforholdHistorikk::new).collect(Collectors.toList());
    }

    public ArbeidsforholdHistorikk get(String arbeidsforholdId, String miljo) {
        var model = repository.findByArbeidsforholdIdAndMiljo(arbeidsforholdId, miljo);
        return model.map(ArbeidsforholdHistorikk::new).orElse(null);
    }

    public void save(ArbeidsforholdHistorikk historikk) {
        repository.findByArbeidsforholdIdAndMiljo(historikk.getArbeidsforholdId(), historikk.getMiljo())
                .map(value -> historikk.toModel(value.getId())).orElseGet(() -> repository.save(historikk.toModel()));
    }

    public void deleteBy(String arbeidsforholdId, String miljo) {
        repository.deleteByArbeidsforholdIdAndMiljo(arbeidsforholdId, miljo);
    }

    public void deleteBy(String miljo) {
        repository.deleteAllByMiljo(miljo);
    }
}
