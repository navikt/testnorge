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
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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

        if (nonNull(testident) && testident.isPdlf() ||
                nonNull((bestillingRequest.getPdldata().getOpprettNyPerson())) ||
                isNotBlank(ident)) {

            var context = MappingContextUtils.getMappingContext();
            context.setProperty("navSyntetiskIdent", bestillingRequest.getNavSyntetiskIdent());
            context.setProperty("opprettFraIdent", ident);

            return Originator.builder()
                    .pdlBestilling(mapperFacade.map(bestillingRequest.getPdldata(), BestillingRequestDTO.class, context))
                    .ident(nonNull(testident) ? testident.getIdent() : ident)
                    .master(Master.PDLF)
                    .build();

        } else {

            var persondata = mapperFacade.map(bestillingRequest, PdlPersondata.class);
            PdlMasterCleanerUtility.clean(persondata.getPerson());
            return Originator.builder()
                    .pdlBestilling(mapperFacade.map(persondata, BestillingRequestDTO.class))
                    .ident(nonNull(testident) ? testident.getIdent() : ident)
                    .master(Master.PDL)
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
