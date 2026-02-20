package no.nav.organisasjonforvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.organisasjonforvalter.dto.responses.UnderenhetResponse;
import no.nav.organisasjonforvalter.jpa.repository.OrganisasjonRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DrivervirksomheterService {

    private final OrganisasjonRepository organisasjonRepository;

    public Mono<List<UnderenhetResponse>> getUnderenheter(String brukerid) {

        return Mono.fromCallable(() -> organisasjonRepository.findDriftsenheterByBrukerId(brukerid))
                .subscribeOn(Schedulers.boundedElastic())
                .map(organisasjoner -> organisasjoner.stream()
                        .map(org -> UnderenhetResponse.builder()
                                .orgnummer(org.getOrganisasjonsnummer())
                                .orgnavn(org.getOrganisasjonsnavn())
                                .enhetstype(org.getEnhetstype())
                                .build())
                        .toList());
    }
}
