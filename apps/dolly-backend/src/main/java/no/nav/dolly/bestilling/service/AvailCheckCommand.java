package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.pdlforvalter.domain.Pdldata;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Testident.Master;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@RequiredArgsConstructor
public class AvailCheckCommand implements Callable<List<AvailCheckCommand.AvailStatus>> {

    private final String opprettFraIdenter;
    private final PdlPersondata pdlPerson;
    private final TpsfService tpsfService;
    private final PdlDataConsumer pdlDataConsumer;

    @Override
    public List<AvailStatus> call() {

        if (nonNull(pdlPerson) && nonNull(pdlPerson.getOpprettNyPerson())) {
            var checkedIdenter = pdlDataConsumer.identCheck(List.of(opprettFraIdenter.split(",")));
            return checkedIdenter.stream()
                    .map(status -> AvailStatus.builder()
                            .ident(status.getIdent())
                            .available(isTrue(status.getAvailable()))
                            .message(status.getStatus())
                            .master(Master.PDL)
                            .build())
                    .collect(Collectors.toList());

        } else {
            var tilgjengeligeIdenter = tpsfService.checkEksisterendeIdenter(
                    new ArrayList<>(List.of(opprettFraIdenter.split(","))));
            return tilgjengeligeIdenter.getStatuser().stream()
                    .map(status -> AvailStatus.builder()
                            .ident(status.getIdent())
                            .available(status.isAvailable())
                            .message(status.getStatus())
                            .master(Master.TPSF)
                            .build())
                    .collect(Collectors.toList());
        }
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
        public boolean isPdl() {
            return getMaster() == Master.PDL;
        }
    }
}
