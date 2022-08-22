package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.domain.jpa.Testident.Master;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@RequiredArgsConstructor
public class AvailCheckCommand implements Callable<List<AvailCheckCommand.AvailStatus>> {

    private final String opprettFraIdenter;
    private final PdlDataConsumer pdlDataConsumer;

    @Override
    public List<AvailStatus> call() {

        var checkedIdenter = pdlDataConsumer.identCheck(List.of(opprettFraIdenter.split(",")));
        return checkedIdenter.stream()
                .map(status -> AvailStatus.builder()
                        .ident(status.getIdent())
                        .available(isTrue(status.getAvailable()))
                        .message(status.getStatus())
                        .master(Master.PDLF)
                        .build())
                .collect(Collectors.toList());
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
