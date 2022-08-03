package no.nav.registre.sdforvalter.adapter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.sdforvalter.database.model.KrrModel;
import no.nav.registre.sdforvalter.database.repository.KrrRepository;
import no.nav.registre.sdforvalter.domain.Krr;
import no.nav.registre.sdforvalter.domain.KrrListe;

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
        log.info("Henter krrdata med gruppe {}", gruppe);
        List<KrrModel> krrModels = repository.findByGruppeModel(getGruppe(gruppe));
        List<Krr> liste = krrModels.stream().map(Krr::new).toList();

        log.info("Fant {} krr-personer fra gruppe {}", liste.size(), gruppe);
        return new KrrListe(liste);
    }

    public KrrListe save(KrrListe liste) {
        List<Krr> list = liste.itemsNotIn(fetch());
        if (list.isEmpty()) {
            return new KrrListe();
        }
        return new KrrListe(repository.saveAll(list
                .stream()
                .map(item -> new KrrModel(item, getOppinnelse(item), getGruppe(item)))
                .toList())
        );
    }

}
