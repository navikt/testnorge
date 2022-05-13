package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingWrapper;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingWrapper.RsMalBestilling;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class MalBestillingService {

    private static final String ANONYM = "FELLES";
    private static final String ALLE = "ALLE";

    private final BestillingService bestillingService;
    private final MapperFacade mapperFacade;

    private static String getBruker(Bruker bruker) {

        if (isNull(bruker)) {
            return ANONYM;
        }
        return switch (bruker.getBrukertype()) {
            case AZURE, BANKID -> bruker.getBrukernavn();
            case BASIC -> bruker.getNavIdent();
        };
    }

    public RsMalBestillingWrapper getMalBestillinger() {

        RsMalBestillingWrapper malBestillingWrapper = new RsMalBestillingWrapper();

        List<Bestilling> bestillinger = bestillingService.fetchMalBestillinger();

        var malBestillinger = bestillinger.parallelStream()
                .collect(Collectors.groupingBy(bestilling -> getBruker(bestilling.getBruker())))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().parallelStream()
                        .map(bestilling1 -> RsMalBestilling.builder()
                                .bestilling(mapperFacade.map(bestilling1, RsMalBestillingWrapper.RsBestilling.class))
                                .malNavn(bestilling1.getMalBestillingNavn())
                                .id(bestilling1.getId())
                                .bruker(mapperFacade.map(nonNull(bestilling1.getBruker()) ?
                                        bestilling1.getBruker() :
                                        Bruker.builder().brukerId(ANONYM).brukernavn(ANONYM).build(), RsBrukerUtenFavoritter.class))
                                .build())
                        .toList()));

        malBestillingWrapper.getMalbestillinger().putAll(malBestillinger);
        malBestillingWrapper.getMalbestillinger().put(ALLE, malBestillinger.values().stream()
                .flatMap(Collection::stream)
                        .sorted(Comparator.comparing(RsMalBestilling::getMalNavn))
                        .toList());

        return malBestillingWrapper;
    }

    public List<RsMalBestilling> getMalbestillingByNavnAndUser(String brukerId, String malNavn) {

        List<Bestilling> bestillinger = bestillingService.fetchMalbestillingByNavnAndUser(brukerId, malNavn);
        return bestillinger.stream().map(bestilling -> RsMalBestilling.builder()
                .malNavn(bestilling.getMalBestillingNavn())
                .id(bestilling.getId())
                .build()).toList();
    }
}
