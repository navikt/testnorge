package no.nav.dolly.bestilling.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
@RequiredArgsConstructor
public class AvailCheckService {

    private final PdlDataConsumer pdlDataConsumer;

    public Flux<AvailStatus> checkAvailable(String opprettFraIdenter) {

        return pdlDataConsumer.identCheck(List.of(opprettFraIdenter.split(",")))
                .map(status -> AvailStatus.builder()
                        .ident(status.getIdent())
                        .available(isTrue(status.getAvailable()))
                        .message(status.getStatus())
                        .build());
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailStatus {
        private String ident;
        private boolean available;
        private String message;
    }
}
