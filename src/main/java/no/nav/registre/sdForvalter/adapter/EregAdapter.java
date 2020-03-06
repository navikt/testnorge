package no.nav.registre.sdForvalter.adapter;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import no.nav.registre.sdForvalter.database.model.EregModel;
import no.nav.registre.sdForvalter.database.repository.EregRepository;
import no.nav.registre.sdForvalter.domain.Ereg;
import no.nav.registre.sdForvalter.domain.EregListe;

@Slf4j
@Component
@AllArgsConstructor
public class EregAdapter {
    private final EregRepository repository;
    private final GruppeAdapter gruppeAdapter;
    private final OpprinnelseAdapter opprinnelseAdapter;

    public EregListe fetchEregData(String gruppe) {
        if (gruppe != null) {
            log.info("Henter ereg med gruppe={}", gruppe);
        }
        return new EregListe(StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .filter(model -> gruppe == null
                        || model.getGruppeModel() != null
                        && model.getGruppeModel().getKode().equals(gruppe)
                )
                .collect(Collectors.toList())
        );
    }

    private EregModel fetchEreg(String orgnr) {
        return repository.findById(orgnr).orElseThrow(
                () -> new RuntimeException("Finner ikke orgnr = " + orgnr + " i ereg databasen.")
        );
    }

    public EregListe saveEregData(EregListe eregs) {
        List<Ereg> existionEregs = fetchEregData(null).getListe();
        List<Ereg> noneExistingEregs = eregs
                .getListe()
                .stream()
                .filter(ereg -> existionEregs.stream().noneMatch(existing -> existing.equals(ereg)))
                .collect(Collectors.toList());

        return new EregListe(repository.saveAll(
                noneExistingEregs
                        .stream()
                        .map(ereg -> new EregModel(
                                ereg,
                                ereg.getJuridiskEnhet() != null ? fetchEreg(ereg.getJuridiskEnhet()) : null,
                                ereg.getOpprinelse() != null
                                        ? opprinnelseAdapter.saveOpprinnelse(ereg.getOpprinelse())
                                        : null,
                                ereg.getGruppe() != null
                                        ? gruppeAdapter.fetchGruppe(ereg.getGruppe())
                                        : null
                        ))
                        .collect(Collectors.toList())
        ));
    }


}
