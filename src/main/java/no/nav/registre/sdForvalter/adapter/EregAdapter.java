package no.nav.registre.sdForvalter.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
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

    private EregModel fetchModelByOrgnr(String orgnr) {
        return repository.findById(orgnr).orElseThrow(
                () -> new RuntimeException("Finner ikke orgnr = " + orgnr + " i ereg databasen.")
        );
    }

    public EregListe fetchByIds(Set<String> ids) {
        log.info("Henter organisasjoner fra orgnummere: {}", String.join(", ", ids));
        EregListe eregListe = new EregListe(repository.findAllById(ids));
        if (eregListe.getListe().size() < ids.size()) {
            log.warn("Fant bare {}/{} orgnummer.", eregListe.getListe().size(), ids.size());
        }
        return eregListe;
    }

    public EregListe fetchBy(String gruppe) {
        log.info("Henter ereg data med gruppe {}", gruppe);
        return new EregListe(fetch().filterOnGruppe(gruppe));
    }

    public Ereg fetchByOrgnr(String orgnr) {
        return new Ereg(fetchModelByOrgnr(orgnr));
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
        repository.saveAll(liste
                .stream()
                .map(item -> new EregModel(
                        item,
                        item.getJuridiskEnhet() != null ? fetchModelByOrgnr(item.getJuridiskEnhet()) : null,
                        getOppinnelse(item),
                        getGruppe(item)
                ))
                .collect(Collectors.toList())
        ).forEach(item -> persisted.add(new Ereg(item)));
        return persisted;
    }
}
