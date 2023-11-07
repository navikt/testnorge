package no.nav.registre.testnorge.organisasjonfastedataservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.organisasjonfastedataservice.domain.Organisasjon;
import no.nav.registre.testnorge.organisasjonfastedataservice.repository.OrganisasjonRepository;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.Gruppe;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganisasjonService {
    private final OrganisasjonRepository repository;

    public void save(Organisasjon organisasjon, Gruppe gruppe) {
        repository.save(organisasjon.toModel(gruppe));
    }

    public void delete(String orgnummer) {
        repository.deleteById(orgnummer);
    }

    public List<Organisasjon> getOrganisasjoner(Gruppe gruppe) {
        log.info("Henter alle organisasjoner med gruppe {}...", gruppe);
        var list = repository.findAllByGruppe(gruppe);
        var organisasjoner = list.stream().map(Organisasjon::new).collect(Collectors.toList());
        log.info("Fant {} organisasjoner med gruppe {}.", organisasjoner.size(), gruppe);
        return organisasjoner;
    }

    public List<Organisasjon> getOrganisasjoner() {
        log.info("Henter alle organisasjoner...");
        var list = repository.findAll();
        var organisasjoner = StreamSupport
                .stream(list.spliterator(), false)
                .map(Organisasjon::new)
                .toList();
        log.info("Fant {} organisasjoner.", organisasjoner.size());
        return organisasjoner;
    }

    public Optional<Organisasjon> getOrganisasjon(String orgnummer) {
        log.info("Henter organisasjon med orgnummer {}...", orgnummer);
        var model = repository.findById(orgnummer);
        if (model.isEmpty()) {
            log.warn("Fant ikke organisasjon {}.", orgnummer);
        }
        return model.map(Organisasjon::new);
    }
}
