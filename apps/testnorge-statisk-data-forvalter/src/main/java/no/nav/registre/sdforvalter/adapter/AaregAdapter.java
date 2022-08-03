package no.nav.registre.sdforvalter.adapter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.sdforvalter.database.model.AaregModel;
import no.nav.registre.sdforvalter.database.repository.AaregRepository;
import no.nav.registre.sdforvalter.domain.Aareg;
import no.nav.registre.sdforvalter.domain.AaregListe;

@Slf4j
@Component
public class AaregAdapter extends FasteDataAdapter {
    private final AaregRepository repository;

    public AaregAdapter(OpprinnelseAdapter opprinnelseAdapter, GruppeAdapter gruppeAdapter, AaregRepository repository) {
        super(opprinnelseAdapter, gruppeAdapter);
        this.repository = repository;
    }

    private AaregListe fetch() {
        return new AaregListe(repository.findAll());
    }

    public AaregListe fetchBy(String gruppe) {
        log.info("Henter aaregdata med gruppe {}", gruppe);
        List<AaregModel> aaregModels = repository.findByGruppeModel(getGruppe(gruppe));
        List<Aareg> liste = aaregModels.stream().map(Aareg::new).toList();

        log.info("Fant {} aareg-personer fra gruppe {}", liste.size(), gruppe);
        return new AaregListe(liste);
    }

    public AaregListe save(AaregListe liste) {
        List<Aareg> list = liste.itemsNotIn(fetch());
        if (list.isEmpty()) {
            return new AaregListe();
        }
        return new AaregListe(repository.saveAll(list
                .stream()
                .map(aareg -> new AaregModel(aareg, getOppinnelse(aareg), getGruppe(aareg)))
                .toList())
        );
    }
}
