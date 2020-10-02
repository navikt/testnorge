package no.nav.registre.sdforvalter.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.sdforvalter.database.model.TpsIdentModel;
import no.nav.registre.sdforvalter.database.repository.TpsIdenterRepository;
import no.nav.registre.sdforvalter.domain.TpsIdent;
import no.nav.registre.sdforvalter.domain.TpsIdentListe;

@Slf4j
@Component
public class TpsIdenterAdapter extends FasteDataAdapter {
    private final TpsIdenterRepository repository;

    public TpsIdenterAdapter(OpprinnelseAdapter opprinnelseAdapter, GruppeAdapter gruppeAdapter, TpsIdenterRepository repository) {
        super(opprinnelseAdapter, gruppeAdapter);
        this.repository = repository;
    }

    private TpsIdentListe fetch() {
        return new TpsIdentListe(repository.findAll());
    }

    public TpsIdentListe fetchBy(String gruppe) {
        log.info("Henter tps identer med gruppe {}", gruppe);
        var liste = new TpsIdentListe(fetch().filterOnGruppe(gruppe));
        log.info("Fant {} person fra gruppe {}", liste.size(), gruppe);
        return liste;
    }

    public TpsIdentListe save(TpsIdentListe liste) {
        List<TpsIdent> list = liste.itemsNotIn(fetch());
        if (list.isEmpty()) {
            return new TpsIdentListe();
        }

        return new TpsIdentListe(repository.saveAll(list
                .stream()
                .map(item -> new TpsIdentModel(item, getOppinnelse(item), getGruppe(item)))
                .collect(Collectors.toList()))
        );
    }
}