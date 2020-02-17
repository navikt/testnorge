package no.nav.registre.sdForvalter.adapter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import no.nav.registre.sdForvalter.database.model.TpsModel;
import no.nav.registre.sdForvalter.database.repository.TpsRepository;
import no.nav.registre.sdForvalter.domain.Tps;

@Component
@AllArgsConstructor
public class TpsAdapter {
    private final TpsRepository repository;
    private final KildeSystemAdapter kildeSystemAdapter;

    public Set<Tps> fetchTps() {
        Set<Tps> tpsSet = new HashSet<>();
        repository.findAll().forEach(model -> tpsSet.add(new Tps(model)));
        return tpsSet;
    }


    public Set<Tps> saveTps(Set<Tps> tpss) {
        Set<Tps> existingTpss = fetchTps();
        Set<Tps> noneExistingTpss = tpss
                .stream()
                .filter(tps -> !existingTpss.contains(tps))
                .collect(Collectors.toSet());

        Iterable<TpsModel> createdTps = repository.saveAll(noneExistingTpss
                .stream()
                .map(tps -> new TpsModel(tps, tps.getKildeSystem() != null ? kildeSystemAdapter.saveKildeSystem(tps.getKildeSystem()) : null))
                .collect(Collectors.toList())
        );
        return StreamSupport
                .stream(createdTps.spliterator(), true)
                .map(Tps::new)
                .collect(Collectors.toSet());
    }

}
