package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingWrapper;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class MalBestillingService {

    private static final String COMMON = "FELLES";
    private static final String ALLE = "ALLE";

    private final BestillingService bestillingService;
    private final MapperFacade mapperFacade;

    public RsMalBestillingWrapper getMalBestillinger() {

        RsMalBestillingWrapper malBestillingWrapper = new RsMalBestillingWrapper();

        List<Bestilling> bestillinger = bestillingService.fetchMalBestillinger();
        malBestillingWrapper.getMalbestillinger().putIfAbsent(ALLE,
                new TreeSet<>(Comparator.comparing(RsMalBestillingWrapper.RsMalBestilling::getMalNavn)
                        .thenComparing(RsMalBestillingWrapper.RsMalBestilling::getId)));
        bestillinger.forEach(bestilling -> {

            RsMalBestillingWrapper.RsMalBestilling malBestilling = RsMalBestillingWrapper.RsMalBestilling.builder()
                    .malNavn(bestilling.getMalBestillingNavn())
                    .bruker(mapperFacade.map(nonNull(bestilling.getBruker()) ? bestilling.getBruker() :
                            Bruker.builder().brukerId(COMMON).brukernavn(COMMON).build(), RsBrukerUtenFavoritter.class))
                    .id(bestilling.getId())
                    .bestilling(mapperFacade.map(bestilling, RsMalBestillingWrapper.RsBestilling.class))
                    .build();

            malBestillingWrapper.getMalbestillinger().putIfAbsent(getUserId(bestilling.getBruker()),
                    new TreeSet<>(Comparator.comparing(RsMalBestillingWrapper.RsMalBestilling::getMalNavn)
                            .thenComparing(RsMalBestillingWrapper.RsMalBestilling::getId)));
            malBestillingWrapper.getMalbestillinger().get(getUserId(bestilling.getBruker())).add(malBestilling);
            malBestillingWrapper.getMalbestillinger().get(ALLE).add(malBestilling);
        });

        return malBestillingWrapper;
    }

    private static String getUserId(Bruker bruker) {

        return nonNull(bruker) ? resolveId(bruker) : COMMON;
    }

    private static String resolveId(Bruker bruker) {

        if (nonNull(bruker.getEidAv())) {
            return bruker.getEidAv().getBrukernavn();

        } else if (isNotBlank(bruker.getBrukernavn())) {
            return bruker.getBrukernavn();

        } else {
            return bruker.getNavIdent();
        }
    }
}
