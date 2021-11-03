package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Testident.Master;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;

import java.util.concurrent.Callable;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor
public class OriginatorCommand implements Callable<OriginatorCommand.Originator> {

    private final RsDollyBestillingRequest bestillingRequest;
    private final MapperFacade mapperFacade;

    @Override
    public Originator call() {

        if (nonNull(bestillingRequest.getPdldata()) && nonNull((bestillingRequest.getPdldata().getOpprettNyPerson()))) {

            var context = new MappingContext.Factory().getContext();
            context.setProperty("navSyntetiskIdent", bestillingRequest.getNavSyntetiskIdent());

            return Originator.builder()
                    .pdlBestilling(mapperFacade.map(bestillingRequest.getPdldata(), BestillingRequestDTO.class, context))
                    .master(Master.PDL)
                    .build();

        } else if (nonNull(bestillingRequest.getTpsf())) {

            var context = new MappingContext.Factory().getContext();
            context.setProperty("navSyntetiskIdent", bestillingRequest.getNavSyntetiskIdent());

            return Originator.builder()
                    .tpsfBestilling(mapperFacade.map(bestillingRequest.getTpsf(), TpsfBestilling.class, context))
                    .master(Master.TPSF)
                    .build();

        } else {

            var bestilling =  new TpsfBestilling();
            bestilling.setAntall(1);
            bestilling.setNavSyntetiskIdent(bestillingRequest.getNavSyntetiskIdent());

            return Originator.builder()
                    .tpsfBestilling(bestilling)
                    .master(Master.TPSF)
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Originator {

        private BestillingRequestDTO pdlBestilling;
        private TpsfBestilling tpsfBestilling;
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
