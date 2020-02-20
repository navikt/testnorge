package no.nav.registre.sdForvalter.adapter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import no.nav.registre.sdForvalter.database.model.EregModel;
import no.nav.registre.sdForvalter.database.repository.EregRepository;
import no.nav.registre.sdForvalter.domain.Ereg;

@Component
@AllArgsConstructor
public class EregAdapter {
    private final EregRepository repository;
    private final KildeSystemAdapter kildeSystemAdapter;

    public List<Ereg> fetchEregData() {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .map(Ereg::new)
                .collect(Collectors.toList());
    }

    public List<Ereg> saveEregData(List<Ereg> eregs) {
        List<Ereg> existionEregs = fetchEregData();
        List<Ereg> noneExistingEregs = eregs
                .stream()
                .filter(ereg -> existionEregs.stream().noneMatch(existing -> existing.equals(ereg)))
                .collect(Collectors.toList());

        Iterable<EregModel> createdEregs = repository.saveAll(
                noneExistingEregs
                        .stream()
                        .map(ereg -> new EregModel(
                                ereg,
                                ereg.getKildeSystem() != null
                                        ? kildeSystemAdapter.saveKildeSystem(ereg.getKildeSystem())
                                        : null
                        ))
                        .collect(Collectors.toList())
        );

        return StreamSupport
                .stream(createdEregs.spliterator(), false)
                .map(Ereg::new)
                .collect(Collectors.toList());
    }


}
