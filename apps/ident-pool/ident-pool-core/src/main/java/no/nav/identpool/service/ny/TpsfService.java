package no.nav.identpool.service.ny;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import no.nav.identpool.consumers.TpsfConsumer;
import no.nav.identpool.consumers.TpsfStatusResponse;
import no.nav.identpool.domain.TpsStatus;

@Service
@RequiredArgsConstructor
public class TpsfService {

    private final TpsfConsumer tpsfConsumer;

    public List<TpsStatus> checkAvailStatus(List<String> idents) {

        TpsfStatusResponse response = tpsfConsumer.getStatusFromTpsf(idents, true);

        return response.getStatusPaaIdenter().stream()
                .map(status -> TpsStatus.builder()
                        .ident(status.getIdent())
                        .inUse(!status.getEnv().isEmpty())
                        .build())
                .collect(Collectors.toList());
    }
}
