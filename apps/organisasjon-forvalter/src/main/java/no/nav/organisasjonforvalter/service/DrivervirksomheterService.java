package no.nav.organisasjonforvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.organisasjonforvalter.dto.responses.UnderenhetResponse;
import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import no.nav.organisasjonforvalter.jpa.repository.OrganisasjonRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DrivervirksomheterService {

    private final OrganisasjonRepository organisasjonRepository;

    public List<UnderenhetResponse> getUnderenheter(String brukerid) {

        List<Organisasjon> organisasjoner = organisasjonRepository.findAllByBrukerId(brukerid);

        return organisasjoner.stream()
                .map(org -> getOrganisasjon(org, new ArrayList<>()))
                .flatMap(Collection::stream)
                .map(org -> UnderenhetResponse.builder()
                        .orgnummer(org.getOrganisasjonsnummer())
                        .orgnavn(org.getOrganisasjonsnavn())
                        .enhetstype(org.getEnhetstype())
                        .build())
                .collect(Collectors.toList());
    }

    private List<Organisasjon> getOrganisasjon(Organisasjon organisasjon, List<Organisasjon> result) {

        if (organisasjon.hasAnsatte()) {
            result.add(organisasjon);

        } else {
            organisasjon.getUnderenheter().stream()
                    .forEach(org -> getOrganisasjon(org, result));
        }

        return result;
    }
}
