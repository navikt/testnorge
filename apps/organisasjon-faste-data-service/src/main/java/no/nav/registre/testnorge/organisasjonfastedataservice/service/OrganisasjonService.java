package no.nav.registre.testnorge.organisasjonfastedataservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.organisasjonfastedataservice.domain.Gruppe;
import no.nav.registre.testnorge.organisasjonfastedataservice.domain.Organisasjon;
import no.nav.registre.testnorge.organisasjonfastedataservice.repository.OrganisasjonRepository;

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
        var list = repository.findByGruppe(gruppe);
        return list.stream().map(Organisasjon::new).collect(Collectors.toList());
    }

    public Optional<Organisasjon> getOrganisasjon(String orgnummer) {
        var model = repository.findById(orgnummer);
        if (model.isEmpty()) {
            log.warn("Fant ikke organisasjon {}.", orgnummer);
        }
        return model.map(Organisasjon::new);
    }
}
