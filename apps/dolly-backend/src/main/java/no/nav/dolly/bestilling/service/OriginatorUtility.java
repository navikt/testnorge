package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.jpa.Testident.Master;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.testnav.libs.data.pdlforvalter.v1.BestillingRequestDTO;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.util.TestnorgeIdentUtility.isTestnorgeIdent;
import static org.apache.commons.lang3.StringUtils.isBlank;

@UtilityClass
public class OriginatorUtility {

    public static Originator prepOriginator(RsDollyUtvidetBestilling bestillingRequest, Testident testident, MapperFacade mapperFacade) {

        return prepOriginator(bestillingRequest, testident, null, mapperFacade);
    }

    public static Originator prepOriginator(RsDollyUtvidetBestilling bestillingRequest, String ident, MapperFacade mapperFacade) {

        return prepOriginator(bestillingRequest, null, ident, mapperFacade);
    }

    public static Originator prepOriginator(RsDollyUtvidetBestilling bestillingRequest, MapperFacade mapperFacade) {

        return prepOriginator(bestillingRequest, null, null, mapperFacade);
    }

    public static Originator prepOriginator(RsDollyUtvidetBestilling bestillingRequest, Testident testident,
                                            String ident, MapperFacade mapperFacade) {

        if (isNull(bestillingRequest.getPdldata())) {
            bestillingRequest.setPdldata(new PdlPersondata());
        }

        if (isNull(bestillingRequest.getPdldata().getOpprettNyPerson()) &&
                isNull(testident) && isBlank(ident)) {

            bestillingRequest.getPdldata().setOpprettNyPerson(new PdlPersondata.PdlPerson());
        }

        if (nonNull(bestillingRequest.getPdldata().getOpprettNyPerson()) &&
                isNull(bestillingRequest.getPdldata().getOpprettNyPerson().getSyntetisk())) {

            bestillingRequest.getPdldata().getOpprettNyPerson().setSyntetisk(true);
        }

        if (isTestnorgeIdent(ident) || nonNull(testident) && testident.isPdl()) {

            var bestilling = mapperFacade.map(bestillingRequest, RsDollyUtvidetBestilling.class);
            var pdldata = PdlMasterCleanerUtility.clean(bestilling.getPdldata());
            return Originator.builder()
                    .pdlBestilling(mapperFacade.map(pdldata, BestillingRequestDTO.class))
                    .ident(nonNull(testident) ? testident.getIdent() : ident)
                    .master(Master.PDL)
                    .build();

        } else {

            var context = MappingContextUtils.getMappingContext();
            context.setProperty("navSyntetiskIdent", bestillingRequest.getNavSyntetiskIdent());
            context.setProperty("opprettFraIdent", ident);

            return Originator.builder()
                    .pdlBestilling(mapperFacade.map(bestillingRequest.getPdldata(), BestillingRequestDTO.class, context))
                    .ident(nonNull(testident) ? testident.getIdent() : ident)
                    .master(Master.PDLF)
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Originator {

        private BestillingRequestDTO pdlBestilling;
        private String ident;
        private Master master;

        @JsonIgnore
        public boolean isPdl() {
            return getMaster() == Master.PDL;
        }
    }
}
