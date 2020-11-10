package no.nav.registre.sdforvalter.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import no.nav.registre.sdforvalter.database.model.EregModel;
import no.nav.registre.sdforvalter.database.model.EregTagModel;
import no.nav.registre.sdforvalter.database.model.TagModel;
import no.nav.registre.sdforvalter.database.repository.EregRepository;
import no.nav.registre.sdforvalter.domain.Ereg;
import no.nav.registre.sdforvalter.domain.EregListe;

@Slf4j
@Component
public class EregAdapter extends FasteDataAdapter {
    private final EregRepository repository;
    private final EregTagAdapter eregTagAdapter;
    private final TagsAdapter tagsAdapter;

    public EregAdapter(
            OpprinnelseAdapter opprinnelseAdapter,
            GruppeAdapter gruppeAdapter,
            EregRepository repository,
            EregTagAdapter eregTagAdapter,
            TagsAdapter tagsAdapter
    ) {
        super(opprinnelseAdapter, gruppeAdapter);
        this.repository = repository;
        this.eregTagAdapter = eregTagAdapter;
        this.tagsAdapter = tagsAdapter;
    }

    private EregModel fetchModelByOrgnr(String orgnr) {
        return orgnr == null ? null : repository.findById(orgnr).orElse(null);
    }


    public EregListe fetchByIds(Set<String> ids) {
        log.info("Henter organisasjoner fra orgnummere: {}", String.join(", ", ids));
        List<Ereg> list = StreamSupport
                .stream(repository.findAllById(ids).spliterator(), false)
                .map(value -> new Ereg(
                        value,
                        eregTagAdapter.findAllTagsBy(value.getOrgnr())
                )).collect(Collectors.toList());

        EregListe eregListe = new EregListe(list);
        if (eregListe.getListe().size() < ids.size()) {
            log.warn("Fant bare {}/{} orgnummer.", eregListe.getListe().size(), ids.size());
        }
        return eregListe;
    }

    public EregListe fetchBy(String gruppe) {
        log.info("Henter ereg data med gruppe {}", gruppe);
        List<EregModel> eregModels = repository.findByGruppeModel(getGruppe(gruppe));
        List<Ereg> liste = eregModels.stream().map(eregModel -> {
            List<TagModel> tagModels = eregTagAdapter.findAllTagsBy(eregModel.getOrgnr());
            return new Ereg(eregModel, tagModels);
        }).collect(Collectors.toList());

        log.info("Fant {} orgnr fra gruppe {}", liste.size(), gruppe);
        return new EregListe(liste);
    }

    public Ereg fetchByOrgnr(String orgnr) {
        EregModel model = fetchModelByOrgnr(orgnr);
        return model == null ? null : new Ereg(model, eregTagAdapter.findAllTagsBy(orgnr));
    }

    public EregListe save(EregListe liste) {
        List<Ereg> list = liste.getListe();
        if (list.isEmpty()) {
            log.info("Fant ingen nye ereg");
            return new EregListe();
        }
        return new EregListe(save(new ArrayList<>(), list));
    }

    private List<Ereg> save(final List<Ereg> saved, final List<Ereg> notSaved) {
        if (notSaved.isEmpty()) {
            return saved;
        }

        List<Ereg> dependency = notSaved
                .stream()
                .filter(value -> value.getJuridiskEnhet() != null)
                .filter(value -> notSaved.stream().anyMatch(containsJuridiskEnhet(value)))
                .collect(Collectors.toList());

        List<Ereg> noDependency = notSaved
                .stream()
                .filter(value -> !dependency.contains(value))
                .collect(Collectors.toList());

        return save(persist(noDependency), dependency);
    }

    private Predicate<Ereg> containsJuridiskEnhet(final Ereg value) {
        return other -> value.getJuridiskEnhet().equals(other.getOrgnr());
    }

    private List<Ereg> persist(final List<Ereg> liste) {
        List<Ereg> persisted = new ArrayList<>();

        for (Ereg ereg : liste) {
            EregModel model = fetchModelByOrgnr(ereg.getJuridiskEnhet());

            EregModel eregModel = repository.save(new EregModel(
                    ereg,
                    ereg.getJuridiskEnhet() != null ? model : null,
                    getOppinnelse(ereg),
                    getGruppe(ereg)
            ));
            List<TagModel> tagModels = ereg.getTags().stream().map(tagsAdapter::save).collect(Collectors.toList());
            tagModels.forEach(tagModel -> eregTagAdapter.save(new EregTagModel(null, eregModel, tagModel)));

            persisted.add(new Ereg(eregModel, tagModels));
        }
        return persisted;
    }
}
