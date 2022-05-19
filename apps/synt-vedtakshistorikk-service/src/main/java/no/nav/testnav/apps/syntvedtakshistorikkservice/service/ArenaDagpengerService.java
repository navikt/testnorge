package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.SyntDagpengerConsumer;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.DagpengevedtakDTO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArenaDagpengerService {

    private final SyntDagpengerConsumer syntDagpengerConsumer;

    public DagpengevedtakDTO getSyntetiskDagpengevedtak(String rettighet) {
        return syntDagpengerConsumer.syntetiserDagpengevedtak(rettighet);
    }

}
