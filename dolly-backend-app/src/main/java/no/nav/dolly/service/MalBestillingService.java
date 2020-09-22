package no.nav.dolly.service;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingWrapper;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerAndGruppeId;

@Service
@RequiredArgsConstructor
public class MalBestillingService {

    private static final String COMMON = "FELLES";

    private final BestillingService bestillingService;
    private final MapperFacade mapperFacade;

    public RsMalBestillingWrapper getMalBestillinger() {

        RsMalBestillingWrapper malBestillingWrapper = new RsMalBestillingWrapper();

        List<Bestilling> bestillinger = bestillingService.fetchMalBestillinger();
        bestillinger.forEach(bestilling -> {

            RsMalBestillingWrapper.RsMalBestilling malBestilling = RsMalBestillingWrapper.RsMalBestilling.builder()
                    .malNavn(bestilling.getMalBestillingNavn())
                    .bruker(mapperFacade.map(nonNull(bestilling.getBruker()) ? bestilling.getBruker() :
                            Bruker.builder().brukerId(COMMON).brukernavn(COMMON).build(), RsBrukerAndGruppeId.class))
                    .id(bestilling.getId())
                    .bestilling(mapperFacade.map(bestilling, RsMalBestillingWrapper.RsBestilling.class))
                    .build();

            malBestillingWrapper.getMalbestillinger().putIfAbsent(getUserId(bestilling.getBruker()),
                    new TreeSet(Comparator.comparing(RsMalBestillingWrapper.RsMalBestilling::getMalNavn)
                            .thenComparing(RsMalBestillingWrapper.RsMalBestilling::getId)));
            malBestillingWrapper.getMalbestillinger().get(getUserId(bestilling.getBruker())).add(malBestilling);
        });

        return malBestillingWrapper;
    }

    private static String getUserId(Bruker bruker) {

        return nonNull(bruker) ? resolveId(bruker) : COMMON;
    }

    private static String resolveId(Bruker bruker) {

        return isNotBlank(bruker.getBrukernavn()) ? bruker.getBrukernavn() : bruker.getNavIdent();
    }
}
