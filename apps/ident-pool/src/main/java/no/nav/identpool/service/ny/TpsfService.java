package no.nav.identpool.service.ny;

import lombok.RequiredArgsConstructor;
import no.nav.identpool.consumers.TpsfConsumer;
import no.nav.identpool.consumers.TpsfStatusResponse;
import no.nav.identpool.domain.TpsStatus;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
@RequiredArgsConstructor
public class TpsfService {

    private final TpsfConsumer tpsfConsumer;

    public Set<TpsStatus> checkAvailStatus(Set<String> idents, Boolean syntetisk) {

        if (isTrue(syntetisk)) {
            return idents.stream()
                    .map(ident -> TpsStatus.builder()
                            .ident(ident)
                            .inUse(false)
                            .build())
                    .collect(Collectors.toSet());
        }

        TpsfStatusResponse response = tpsfConsumer.getStatusFromTpsf(idents, true);

        return response.getStatusPaaIdenter().stream()
                .map(status -> TpsStatus.builder()
                        .ident(status.getIdent())
                        .inUse(!status.getEnv().isEmpty())
                        .build())
                .collect(Collectors.toSet());
    }
}
