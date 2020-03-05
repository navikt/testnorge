package no.nav.registre.sdForvalter.adapter;

import lombok.AllArgsConstructor;
import no.nav.registre.sdForvalter.domain.TpsIdent;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import no.nav.registre.sdForvalter.database.model.TpsIdentModel;
import no.nav.registre.sdForvalter.database.repository.TpsIdenterRepository;

@Component
@AllArgsConstructor
public class TpsIdenterAdapter {
    private final TpsIdenterRepository repository;
    private final KildeSystemAdapter kildeSystemAdapter;

    public Set<TpsIdent> fetchTpsIdenter() {
        Set<TpsIdent> tpsIdentSet = new HashSet<>();
        repository.findAll().forEach(model -> tpsIdentSet.add(new TpsIdent(model)));
        return tpsIdentSet;
    }


    public Set<TpsIdent> saveTpsIdenter(Set<TpsIdent> tpsIdenter) {
        Set<TpsIdent> existingTpsIdenter = fetchTpsIdenter();
        Set<TpsIdent> noneExistingTpsIdenter = tpsIdenter
                .stream()
                .filter(tpsIdent -> !existingTpsIdenter.contains(tpsIdent))
                .collect(Collectors.toSet());

        Iterable<TpsIdentModel> createdTpsIdenter = repository.saveAll(noneExistingTpsIdenter
                .stream()
                .map(tpsIdent -> new TpsIdentModel(tpsIdent, tpsIdent.getKildeSystem() != null ? kildeSystemAdapter.saveKildeSystem(tpsIdent.getKildeSystem()) : null))
                .collect(Collectors.toList())
        );
        return StreamSupport
                .stream(createdTpsIdenter.spliterator(), true)
                .map(TpsIdent::new)
                .collect(Collectors.toSet());
    }

}
