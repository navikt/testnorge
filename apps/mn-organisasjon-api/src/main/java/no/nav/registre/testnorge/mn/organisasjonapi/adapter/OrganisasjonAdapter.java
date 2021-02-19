package no.nav.registre.testnorge.mn.organisasjonapi.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.mn.organisasjonapi.consumer.OrganisasjonConsumer;
import no.nav.registre.testnorge.mn.organisasjonapi.domain.Organisasjon;
import no.nav.registre.testnorge.mn.organisasjonapi.repository.OrganisasjonRepository;
import no.nav.registre.testnorge.mn.organisasjonapi.repository.model.OrganisasjonModel;

@Component
@RequiredArgsConstructor
public class OrganisasjonAdapter {
    private final OrganisasjonRepository repository;
    private final OrganisasjonConsumer consumer;

    public void save(Organisasjon organisasjon, String miljo) {
        Optional<OrganisasjonModel> model = findModelBy(organisasjon.getOrgnummer(), miljo);

        repository.save(organisasjon.toModel(model.map(OrganisasjonModel::getId).orElse(null), miljo));
    }

    public List<Organisasjon> getAllBy(Boolean active, String miljo) {
        return fetchBy(active == null ? findAll(miljo) : findBy(active, miljo), miljo);
    }


    public Optional<OrganisasjonModel> findModelBy(String orgnummer, String miljo) {
        return repository.findByOrgnummerAndEnvironment(orgnummer, miljo);
    }


    public Organisasjon findBy(String orgnummer, String miljo) {
        var modelOptional = findModelBy(orgnummer, miljo);
        if (modelOptional.isEmpty()) {
            return null;
        }
        OrganisasjonDTO organisjon = consumer.getOrganisjon(orgnummer, miljo);
        if (organisjon == null) {
            return null;
        }
        return new Organisasjon(organisjon, modelOptional.get().getActive());
    }

    public void deleteBy(String orgnummer, String miljo) {
        repository.deleteByOrgnummerAndEnvironment(orgnummer, miljo);
    }

    public void deleteBy(String miljo) {
        repository.deleteByEnvironment(miljo);
    }

    private Map<String, OrganisasjonModel> findAll(String miljo) {
        return repository.findAllByEnvironment(miljo).stream()
                .collect(Collectors.toMap(
                        OrganisasjonModel::getOrgnummer,
                        value -> value
                ));
    }

    private Map<String, OrganisasjonModel> findBy(boolean active, String miljo) {
        return repository.findAllByActiveAndEnvironment(active, miljo).stream()
                .collect(Collectors.toMap(
                        OrganisasjonModel::getOrgnummer,
                        value -> value
                ));
    }

    private List<Organisasjon> fetchBy(Map<String, OrganisasjonModel> map, String miljo) {
        List<OrganisasjonDTO> organisasjoner = consumer.getOrganisasjoner(map.keySet(), miljo);
        return organisasjoner
                .stream()
                .map(value -> new Organisasjon(value, map.get(value.getOrgnummer()).getActive()))
                .collect(Collectors.toList());
    }
}
