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

        List<TpsIdentModel> tpsIdentModels = tpsIdenterRepository.findByGruppeModel(getGruppe(gruppe));
        List<TpsIdent> liste = tpsIdentModels.stream().map(tpsIdentModel -> {
            List<TagModel> tagModels = fetchTagsByIdent(tpsIdentModel.getFnr());
            return new TpsIdent(tpsIdentModel, tagModels);
        }).collect(Collectors.toList());

        log.info("Fant {} personer fra gruppe {}", liste.size(), gruppe);
        return new TpsIdentListe(liste);
    }

    public TpsIdent fetchByIdent(String ident) {
        log.info("Henter tps-ident {}", ident);
        var tpsIdent = tpsIdenterRepository.findByFnr(ident);
        var tags = fetchTagsByIdent(ident);
        return tpsIdent != null ? new TpsIdent(tpsIdent, tags) : null;
    }

    public List<TagModel> fetchTagsByIdent(String ident) {
        return tpsIdentTagAdapter.findAllTagsByIdent(ident);
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
