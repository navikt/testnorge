package no.nav.testnav.apps.importfratpsfservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.importfratpsfservice.consumer.PdlForvalterConsumer;
import no.nav.testnav.apps.importfratpsfservice.consumer.TpsfConsumer;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsIdentStatusDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TpsfService {

    private final TpsfConsumer tpsfConsumer;
    private final PdlForvalterConsumer pdlForvalterConsumer;

    public List<TpsIdentStatusDTO> importIdenter(Long skdgruppeId, Long dollyGruppeId) {

        return null;
    }
}
