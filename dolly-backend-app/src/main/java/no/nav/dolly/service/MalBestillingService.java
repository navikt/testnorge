package no.nav.dolly.service;

import static java.util.Objects.nonNull;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingWrapper;

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
                    .brukerId(bestilling.getUserId())
                    .id(bestilling.getId())
                    .bestilling(mapperFacade.map(bestilling, RsMalBestillingWrapper.RsBestilling.class))
                    .build();

            malBestillingWrapper.getMalbestillinger().putIfAbsent(getUserId(bestilling.getUserId()),
                    new TreeSet(Comparator.comparing(RsMalBestillingWrapper.RsMalBestilling::getMalNavn)
                            .thenComparing(RsMalBestillingWrapper.RsMalBestilling::getId)));
            malBestillingWrapper.getMalbestillinger().get(getUserId(bestilling.getUserId())).add(malBestilling);
        });

        return malBestillingWrapper;
    }

    private static String getUserId(String userId) {

        return nonNull(userId) ? userId : COMMON;
    }
}
