package no.nav.testnav.apps.tenorsearchservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tenorsearchservice.consumers.PdlDataConsumer;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.DollyTagDTO;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorOversiktResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PdlFilterService {

    private final PdlDataConsumer pdlDataConsumer;

    public Mono<TenorOversiktResponse> filterPdlPerson(TenorOversiktResponse oversikt) {

        if (oversikt.getS)
        return pdlDataConsumer.hasPdlDollyTag(ident);
    }
}
