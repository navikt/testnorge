package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.domain.jpa.Testident.Master;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.Callable;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@RequiredArgsConstructor
public class AvailCheckCommand implements Callable<Flux<AvailCheckCommand.AvailStatus>> {

    private final String opprettFraIdenter;
    private final PdlDataConsumer pdlDataConsumer;

    @Override
    public Flux<AvailStatus> call() {

        return pdlDataConsumer.identCheck(List.of(opprettFraIdenter.split(",")))
                .map(status -> AvailStatus.builder()
                        .ident(status.getIdent())
                        .available(isTrue(status.getAvailable()))
                        .message(status.getStatus())
                        .master(Master.PDLF)
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
        private Master master;

        @JsonIgnore
        public boolean isTpsf() {
            return getMaster() == Master.TPSF;
        }

        @JsonIgnore
        public boolean isPdlf() {
            return getMaster() == Master.PDLF;
        }
    }
}
