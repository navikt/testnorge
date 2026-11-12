package no.nav.dolly.bestilling.service;

import lombok.experimental.UtilityClass;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.util.TestnorgeIdentUtility.isTestnorgeIdent;
import static org.apache.commons.lang3.StringUtils.isBlank;

@UtilityClass
public class PdlRequestBuilderUtility {

    public static BestillingRequestDTO prepRequest(RsDollyUtvidetBestilling bestillingRequest, MapperFacade mapperFacade) {

        return prepRequest(bestillingRequest, null, mapperFacade);
    }

    public static BestillingRequestDTO prepRequest(RsDollyUtvidetBestilling bestillingRequest,
                                                   String ident, MapperFacade mapperFacade) {

        if (isNull(bestillingRequest.getPdldata())) {
            bestillingRequest.setPdldata(new PdlPersondata());
        }

        if (isNull(bestillingRequest.getPdldata().getOpprettNyPerson()) && isBlank(ident)) {

            bestillingRequest.getPdldata().setOpprettNyPerson(new PdlPersondata.PdlPerson());
        }

        if (nonNull(bestillingRequest.getPdldata().getOpprettNyPerson()) &&
                isNull(bestillingRequest.getPdldata().getOpprettNyPerson().getSyntetisk())) {

            bestillingRequest.getPdldata().getOpprettNyPerson().setSyntetisk(true);
        }

        if (isTestnorgeIdent(ident)) {

            var bestilling = mapperFacade.map(bestillingRequest, RsDollyUtvidetBestilling.class);
            var pdldata = PdlMasterCleanerUtility.clean(bestilling.getPdldata());
            return mapperFacade.map(pdldata, BestillingRequestDTO.class);

        } else {

            var context = MappingContextUtils.getMappingContext();
            context.setProperty("navSyntetiskIdent", bestillingRequest.getNavSyntetiskIdent());
            context.setProperty("opprettFraIdent", ident);

            return mapperFacade.map(bestillingRequest.getPdldata(), BestillingRequestDTO.class, context);
        }
    }
}
