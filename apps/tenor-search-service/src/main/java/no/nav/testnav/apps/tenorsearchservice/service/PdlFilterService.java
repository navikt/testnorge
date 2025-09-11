package no.nav.testnav.apps.tenorsearchservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tenorsearchservice.consumers.DollyBackendConsumer;
import no.nav.testnav.apps.tenorsearchservice.consumers.PdlDataConsumer;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.DollyBackendSelector;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorOversiktResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static no.nav.testnav.apps.tenorsearchservice.consumers.dto.DollyTagsDTO.hasDollyTag;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
@RequiredArgsConstructor
public class PdlFilterService {

    private final PdlDataConsumer pdlDataConsumer;
    private final DollyBackendConsumer dollyBackendConsumer;

    public Mono<TenorOversiktResponse> filterPdlPerson(TenorOversiktResponse oversikt, DollyBackendSelector selector) {

        if (oversikt.getStatus() != HttpStatus.OK ||
                oversikt.getData().getPersoner().isEmpty()) {

            return Mono.just(oversikt);
        }

        var identer = oversikt.getData().getPersoner().stream()
                .map(TenorOversiktResponse.Person::getId)
                .toList();

        return Mono.zip(
                        dollyBackendConsumer.getFinnesInfo(identer, selector),
                        pdlDataConsumer.hasPdlDollyTag(identer)
                )
                .map(kilde -> {
                    var oversiktDTO = oversikt.copy();
                    var personer = oversiktDTO.getData().getPersoner().stream()
                            .filter(person -> !hasDollyTag(kilde.getT2().getPersonerTags().get(person.getId())) ||
                                    isTrue(kilde.getT1().getIBruk().get(person.getId())))
                            .map(person -> TenorOversiktResponse.Person.builder()
                                    .id(person.getId())
                                    .fornavn(person.getFornavn())
                                    .etternavn(person.getEtternavn())
                                    .tenorRelasjoner(person.getTenorRelasjoner())
                                    .iBruk(kilde.getT1().getIBruk().get(person.getId()))
                                    .build())
                            .toList();
                    oversiktDTO.getData().setPersoner(personer);
                    oversiktDTO.getData().setRader(personer.size());

                    if (oversiktDTO.getData().getTreff() <= kilde.getT2().getPersonerTags().size()) {
                        oversiktDTO.getData().setTreff(oversiktDTO.getData().getTreff() -
                                (kilde.getT2().getPersonerTags().size() - personer.size()));
                    }

                    return oversiktDTO;
                });
    }
}
