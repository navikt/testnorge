package no.nav.registre.sdForvalter.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.database.model.EregModel;
import no.nav.registre.sdForvalter.database.repository.EregRepository;
import no.nav.registre.sdForvalter.domain.Ereg;
import no.nav.registre.sdForvalter.domain.EregListe;

@Slf4j
@Component
public class EregAdapter extends FasteDataAdapter {
    private final EregRepository repository;

    public EregAdapter(OpprinnelseAdapter opprinnelseAdapter, GruppeAdapter gruppeAdapter, EregRepository repository) {
        super(opprinnelseAdapter, gruppeAdapter);
        this.repository = repository;
    }

    private EregListe fetch() {
        return new EregListe(repository.findAll());
    }

    private EregModel fetchByOrgnr(String orgnr) {
        return repository.findById(orgnr).orElseThrow(
                () -> new RuntimeException("Finner ikke orgnr = " + orgnr + " i ereg databasen.")
        );
    }

    public EregListe fetchBy(String gruppe) {
        log.info("Henter ereg data med gruppe {}", gruppe);
        return new EregListe(fetch().filterOnGruppe(gruppe));
    }

    public EregListe save(EregListe liste) {
        List<Ereg> list = liste.itemsNotIn(fetch());
        if (list.isEmpty()) {
            log.info("Fant ingen nye ereg");
            return new EregListe();
        }
        return new EregListe(repository.saveAll(list
                .stream()
                .map(item -> new EregModel(
                        item,
                        item.getJuridiskEnhet() != null ? fetchByOrgnr(item.getJuridiskEnhet()) : null,
                        getOppinnelse(item),
                        getGruppe(item)
                ))
                .collect(Collectors.toList()))
        );
    }
}
