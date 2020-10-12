package no.nav.registre.sdforvalter.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import no.nav.registre.sdforvalter.database.model.TagModel;
import no.nav.registre.sdforvalter.database.model.TpsIdentModel;
import no.nav.registre.sdforvalter.database.model.TpsIdentTagModel;
import no.nav.registre.sdforvalter.database.repository.TpsIdenterRepository;
import no.nav.registre.sdforvalter.domain.TpsIdent;
import no.nav.registre.sdforvalter.domain.TpsIdentListe;

@Slf4j
@Component
public class TpsIdenterAdapter extends FasteDataAdapter {
    private final TpsIdenterRepository tpsIdenterRepository;
    private final TpsIdentTagAdapter tpsIdentTagAdapter;
    private final TagsAdapter tagsAdapter;

    public TpsIdenterAdapter(
            OpprinnelseAdapter opprinnelseAdapter,
            GruppeAdapter gruppeAdapter,
            TpsIdenterRepository tpsIdenterRepository,
            TpsIdentTagAdapter tpsIdentTagAdapter,
            TagsAdapter tagsAdapter
    ) {
        super(opprinnelseAdapter, gruppeAdapter);
        this.tpsIdenterRepository = tpsIdenterRepository;
        this.tpsIdentTagAdapter = tpsIdentTagAdapter;
        this.tagsAdapter = tagsAdapter;
    }

    private TpsIdentListe fetch() {
        List<TpsIdent> list = StreamSupport
                .stream(tpsIdenterRepository.findAll().spliterator(), false)
                .map(value -> new TpsIdent(
                        value,
                        tpsIdentTagAdapter.findAllTagsByIdent(value.getFnr())
                )).collect(Collectors.toList());
        return new TpsIdentListe(list);
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

        List<TpsIdent> tpsIdents = new ArrayList<>();

        for (TpsIdent tpsIdent : list) {
            TpsIdentModel tpsIdentModel = tpsIdenterRepository.save(
                    new TpsIdentModel(tpsIdent, getOppinnelse(tpsIdent), getGruppe(tpsIdent))
            );
            List<TagModel> tagModels = tpsIdent.getTags() == null ? Collections.emptyList() : tpsIdent.getTags().stream().map(tagsAdapter::save).collect(Collectors.toList());
            tagModels.forEach(tagModel -> tpsIdentTagAdapter.save(
                    new TpsIdentTagModel(null, tpsIdentModel, tagModel)
            ));
            tpsIdents.add(new TpsIdent(tpsIdentModel, tagModels));
        }
        return new TpsIdentListe(tpsIdents);
    }
}
