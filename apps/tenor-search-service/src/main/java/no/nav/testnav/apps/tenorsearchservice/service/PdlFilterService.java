package no.nav.testnav.apps.tenorsearchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tenorsearchservice.consumers.PdlDataConsumer;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorOversiktResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdlFilterService {

    private final PdlDataConsumer pdlDataConsumer;

    public Mono<TenorOversiktResponse> filterPdlPerson(TenorOversiktResponse oversikt, Integer antall) {

        if (oversikt.getStatus() == HttpStatus.OK && nonNull(oversikt.getData().getPersoner())) {


            return pdlDataConsumer.hasPdlDollyTag(oversikt.getData().getPersoner().stream()
                            .map(TenorOversiktResponse.Person::getId)
                            .toList())
                    .map(dollyTags -> {
                        log.info("dollyTags: {}", dollyTags);
                        var oversiktDTO = oversikt.copy();
                        var personer = oversiktDTO.getData().getPersoner().stream()
                                .filter(person -> dollyTags.stream()
                                        .noneMatch(tag -> tag.getIdent().equals(person.getId()) && tag.hasDollyTag()))
                                .toList();
                        oversiktDTO.getData().setPersoner(personer);
                        oversiktDTO.getData().setRader(personer.size());
                        oversiktDTO.getData().setTreff(oversikt.getData().getTreff() - (antall - personer.size()));
                        return oversiktDTO;
                    });
        } else {
            return Mono.just(oversikt);
        }
    }
}
