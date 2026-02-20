package no.nav.organisasjonforvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.organisasjonforvalter.dto.responses.UnderenhetResponse;
import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import no.nav.organisasjonforvalter.jpa.repository.OrganisasjonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DrivervirksomheterService {

    private final OrganisasjonRepository organisasjonRepository;

    public List<UnderenhetResponse> getUnderenheter(String brukerid) {

        List<Organisasjon> organisasjoner = organisasjonRepository.findDriftsenheterByBrukerId(brukerid);

        return organisasjoner.stream()
                .map(org -> UnderenhetResponse.builder()
                        .orgnummer(org.getOrganisasjonsnummer())
                        .orgnavn(org.getOrganisasjonsnavn())
                        .enhetstype(org.getEnhetstype())
                        .build())
                .toList();
    }
}
