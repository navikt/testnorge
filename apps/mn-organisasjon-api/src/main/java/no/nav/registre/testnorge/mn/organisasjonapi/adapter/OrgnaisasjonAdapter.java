package no.nav.registre.testnorge.mn.organisasjonapi.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.mn.organisasjonapi.consumer.OrganisasjonConsumer;
import no.nav.registre.testnorge.mn.organisasjonapi.domain.Organisasjon;
import no.nav.registre.testnorge.mn.organisasjonapi.repository.OrgansiasjonRepository;
import no.nav.registre.testnorge.mn.organisasjonapi.repository.model.OrganisasjonModel;

@Component
@RequiredArgsConstructor
public class OrgnaisasjonAdapter {
    private final OrgansiasjonRepository repository;
    private final OrganisasjonConsumer consumer;

    public void save(Organisasjon organisasjon) {
        consumer.saveOrganisasjon(organisasjon.toDTO());
        repository.save(organisasjon.toModel());
    }

    public List<Organisasjon> getAllBy(Boolean active) {
        return fetchBy(active == null ? findAll() : findBy(active));
    }

    public Organisasjon getBy(String orgnummer) {
        var modelOptional = repository.findById(orgnummer);
        if (modelOptional.isEmpty()) {
            return null;
        }
        OrganisasjonModel organisasjonModel = modelOptional.get();
        OrganisasjonDTO organisjon = consumer.getOrganisjon(orgnummer);
        if (organisjon == null) {
            return null;
        }
        return new Organisasjon(organisjon, organisasjonModel.getActive());
    }

    private Map<String, OrganisasjonModel> findAll() {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .collect(Collectors.toMap(
                        OrganisasjonModel::getOrgnummer,
                        value -> value
                ));
    }

    private Map<String, OrganisasjonModel> findBy(boolean active) {
        return repository.findAllByActive(active).stream()
                .collect(Collectors.toMap(
                        OrganisasjonModel::getOrgnummer,
                        value -> value
                ));
    }


    private List<Organisasjon> fetchBy(Map<String, OrganisasjonModel> map) {
        List<OrganisasjonDTO> organisjoner = consumer.getOrganisjoner(map.keySet());
        return organisjoner
                .stream()
                .map(value -> new Organisasjon(value, map.get(value.getOrgnummer()).getActive()))
                .collect(Collectors.toList());
    }
}
