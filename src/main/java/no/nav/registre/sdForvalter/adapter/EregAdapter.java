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
    private final GruppeAdapter gruppeAdapter;
    private final KildeSystemAdapter kildeSystemAdapter;

    public List<Ereg> fetchEregData(String gruppe) {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .filter(model -> gruppe == null
                        || model.getGruppeModel() == null
                        || model.getGruppeModel().getKode().equals(gruppe)
                )
                .map(Ereg::new)
                .collect(Collectors.toList());
    }

    private EregModel fetchEreg(String orgnr) {
        return repository.findById(orgnr).orElseThrow(
                () -> new RuntimeException("Finner ikke orgnr = " + orgnr + " i ereg databasen.")
        );
    }

    public List<Ereg> saveEregData(List<Ereg> eregs) {
        List<Ereg> existionEregs = fetchEregData(null);
        List<Ereg> noneExistingEregs = eregs
                .stream()
                .filter(ereg -> existionEregs.stream().noneMatch(existing -> existing.equals(ereg)))
                .collect(Collectors.toList());

        Iterable<EregModel> createdEregs = repository.saveAll(
                noneExistingEregs
                        .stream()
                        .map(ereg -> new EregModel(
                                ereg,
                                ereg.getJuridiskEnhet() != null ? fetchEreg(ereg.getJuridiskEnhet()) : null,
                                ereg.getOpprinelse() != null
                                        ? kildeSystemAdapter.saveKildeSystem(ereg.getOpprinelse())
                                        : null,
                                ereg.getGruppe() != null
                                        ? gruppeAdapter.fetchGruppe(ereg.getGruppe())
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
