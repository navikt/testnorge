package no.nav.testnav.apps.tenorsearchservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tenorsearchservice.consumers.PdlDataConsumer;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorOversiktResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class PdlFilterService {

    private final PdlDataConsumer pdlDataConsumer;

    public Mono<TenorOversiktResponse> filterPdlPerson(TenorOversiktResponse oversikt) {

        if (oversikt.getStatus() == HttpStatus.OK && nonNull(oversikt.getData().getPersoner())) {

            return pdlDataConsumer.hasPdlDollyTag(oversikt.getData().getPersoner().stream()
                            .map(TenorOversiktResponse.Person::getId)
                            .toList())
                    .map(dollyTags -> {
                        var oversiktDTO = oversikt.copy();
                        var personer = oversiktDTO.getData().getPersoner().stream()
                                .filter(person -> dollyTags.stream()
                                        .noneMatch(tag -> tag.getIdent().equals(person.getId()) && tag.hasDollyTag()))
                                .toList();
                        oversiktDTO.getData().setPersoner(personer);
                        oversiktDTO.getData().setRader(personer.size());
                        oversiktDTO.getData().setTreff(oversikt.getData().getTreff() - (dollyTags.size() - personer.size()));
                        return oversiktDTO;
                    });
        } else {
            return Mono.just(oversikt);
        }
    }
}
