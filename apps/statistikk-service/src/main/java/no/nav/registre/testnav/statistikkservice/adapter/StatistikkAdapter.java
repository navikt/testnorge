package no.nav.registre.testnav.statistikkservice.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.registre.testnav.statistikkservice.repository.StatistikkRepository;
import no.nav.registre.testnorge.libs.dto.statistikkservice.v1.StatistikkType;
import no.nav.registre.testnav.statistikkservice.domain.Statistikk;
import no.nav.registre.testnav.statistikkservice.repository.model.StatistikkModel;

@Component
@RequiredArgsConstructor
public class StatistikkAdapter {

    public final StatistikkRepository repository;

    public List<Statistikk> findAll() {
        return repository.findAll().stream().map(Statistikk::new).collect(Collectors.toList());
    }

    public Statistikk find(StatistikkType type) {
        Optional<StatistikkModel> model = repository.findById(type);
        return model.map(Statistikk::new).orElse(null);
    }

    public Statistikk save(Statistikk statistikk) {
        return new Statistikk(repository.save(statistikk.toModel()));
    }
}
