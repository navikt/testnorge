package no.nav.registre.sdForvalter.adapter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.repository.AaregRepository;
import no.nav.registre.sdForvalter.domain.Aareg;
import no.nav.registre.sdForvalter.domain.AaregListe;

@Slf4j
@Component
public class AaregAdapter extends FasteDataApdater {
    private final AaregRepository repository;

    public AaregAdapter(OpprinnelseAdapter opprinnelseAdapter, GruppeAdapter gruppeAdapter, AaregRepository repository) {
        super(opprinnelseAdapter, gruppeAdapter);
        this.repository = repository;
    }

    private AaregListe fetch() {
        return new AaregListe(repository.findAll());
    }

    public AaregListe fetchBy(String gruppe) {
        log.info("Henter aareg data med gruppe {}", gruppe);
        return new AaregListe(fetch().filterOnGruppe(gruppe));
    }

    public AaregListe save(AaregListe liste) {
        List<Aareg> list = liste.itemsNotIn(fetch());
        if (list.isEmpty()) {
            return new AaregListe();
        }
        return new AaregListe(repository.saveAll(list
                .stream()
                .map(aareg -> new AaregModel(aareg, getOppinnelse(aareg), getGruppe(aareg)))
                .collect(Collectors.toList()))
        );
    }
}
