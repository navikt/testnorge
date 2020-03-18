package no.nav.registre.sdForvalter.adapter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.database.model.KrrModel;
import no.nav.registre.sdForvalter.database.repository.KrrRepository;
import no.nav.registre.sdForvalter.domain.Krr;
import no.nav.registre.sdForvalter.domain.KrrListe;

@Slf4j
@Component
public class KrrAdapter extends FasteDataAdapter {
    private final KrrRepository repository;

    public KrrAdapter(OpprinnelseAdapter opprinnelseAdapter, GruppeAdapter gruppeAdapter, KrrRepository repository) {
        super(opprinnelseAdapter, gruppeAdapter);
        this.repository = repository;
    }

    private KrrListe fetch() {
        return new KrrListe(repository.findAll());
    }

    public KrrListe fetchBy(String gruppe) {
        log.info("Henter krr med gruppe {}", gruppe);
        return new KrrListe(fetch().filterOnGruppe(gruppe));
    }

    public KrrListe save(KrrListe liste) {
        List<Krr> list = liste.itemsNotIn(fetch());
        if (list.isEmpty()) {
            return new KrrListe();
        }
        return new KrrListe(repository.saveAll(list
                .stream()
                .map(item -> new KrrModel(item, getOppinnelse(item), getGruppe(item)))
                .collect(Collectors.toList()))
        );
    }

}
