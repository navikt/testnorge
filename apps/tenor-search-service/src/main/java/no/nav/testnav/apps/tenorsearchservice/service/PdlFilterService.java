package no.nav.testnav.apps.tenorsearchservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tenorsearchservice.consumers.PdlDataConsumer;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.BestillingIndexSelector;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorOversiktResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static java.util.Objects.isNull;
import static no.nav.testnav.apps.tenorsearchservice.consumers.dto.DollyTagsDTO.hasArenaSyntTag;
import static no.nav.testnav.apps.tenorsearchservice.consumers.dto.DollyTagsDTO.hasDollyTag;

@Service
@RequiredArgsConstructor
public class PdlFilterService {

    private final PdlDataConsumer pdlDataConsumer;
    private final OpenSearchQueryService openSearchQueryService;

    public Mono<TenorOversiktResponse> filterPdlPerson(TenorOversiktResponse oversikt,
                                                       BestillingIndexSelector selector,
                                                       String orgnr) {

        if (oversikt.getStatus() != HttpStatus.OK ||
                isNull(oversikt.getData()) ||
                oversikt.getData().getPersoner().isEmpty()) {

            return Mono.just(oversikt);
        }

        var identer = oversikt.getData().getPersoner().stream()
                .map(TenorOversiktResponse.Person::getId)
                .toList();

        return Mono.zip(
                        openSearchQueryService.execQuery(identer, selector, orgnr),
                        pdlDataConsumer.hasPdlDollyTag(identer)
                )
                .map(kilde -> {
                    var oversiktDTO = oversikt.copy();
                    var iBruk = kilde.getT1().getIdenter();
                    var personer = oversiktDTO.getData().getPersoner().stream()
                            .filter(person -> !hasDollyTag(kilde.getT2().getPersonerTags().get(person.getId())) ||
                                    iBruk.contains(person.getId()))
                            .map(person -> TenorOversiktResponse.Person.builder()
                                    .id(person.getId())
                                    .fornavn(person.getFornavn())
                                    .etternavn(person.getEtternavn())
                                    .tenorRelasjoner(person.getTenorRelasjoner())
                                    .iBruk(iBruk.contains(person.getId()))
                                    .iArenaSynt(hasArenaSyntTag(kilde.getT2().getPersonerTags().get(person.getId())))
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
