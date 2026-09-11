package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.dto.MinSideBestillingerDTO;
import no.nav.dolly.domain.projection.BestillingBrukerFragment;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.util.BrukeradferdUtils;
import no.nav.testnav.libs.reactivesecurity.action.GetUserInfo;
import no.nav.testnav.libs.securitycore.domain.UserInfoExtended;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import tools.jackson.databind.json.JsonMapper;

import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrukerBestillingerService {

    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final String NY_BESTILLING = "NYBESTILLING";
    private static final String GJENOPPRETTING = "GJENOPPRETTING";
    private static final String YEAR_MONTH_FORMAT = "%4d-%02d";

    private final BestillingRepository bestillingRepository;
    private final GetUserInfo getUserInfo;
    private final JsonMapper jsonMapper;

    public Flux<MinSideBestillingerDTO> getBestillinger() {

        return getUserInfo.call()
                .map(UserInfoExtended::id)
                .flatMapMany(bestillingRepository::findByBrukerIdOrderByIdDesc)
                .groupBy(bestilling -> YearMonth.from(bestilling.getDato()).format(YEAR_MONTH_FORMATTER))
                .flatMap(Flux::collectList)
                .map(bestillinger -> MinSideBestillingerDTO.builder()
                        .periode(YearMonth.from(bestillinger.getFirst().getDato()))
                        .antallNyBestillinger(bestillinger.stream()
                                .filter(best -> NY_BESTILLING.equals(best.getBestillingtype()))
                                .count())
                        .antallGjenopprettinger(bestillinger.stream()
                                .filter(best -> GJENOPPRETTING.equals(best.getBestillingtype()))
                                .count())
                        .antallNyePersoner(bestillinger.stream()
                                .filter(best -> NY_BESTILLING.equals(best.getBestillingtype()))
                                .mapToInt(BestillingBrukerFragment::getAntall)
                                .sum())
                        .build())
                .sort(Comparator.comparing(MinSideBestillingerDTO::getPeriode).reversed());
    }

    public Flux<MinSideBestillingerDTO> getBestillingerDetaljert(int year, Month month) {

        return getUserInfo.call()
                .map(UserInfoExtended::id)
                .flatMapMany(id -> bestillingRepository.findKriterierByBrukerIdOrderByIdDesc(id,
                        YEAR_MONTH_FORMAT.formatted(year, month.getValue())))
                .groupBy(BestillingBrukerFragment::getDato)
                .flatMap(Flux::collectList)
                .map(adferd -> MinSideBestillingerDTO.builder()
                        .dato(adferd.getFirst().getDato())
                        .kriterier(BrukeradferdUtils.getAkkumulerteKriterier(adferd,
                                        BestillingBrukerFragment::getBestKriterier, BestillingBrukerFragment::getAntall, jsonMapper)
                                .stream()
                                .map(kriterium -> MinSideBestillingerDTO.Entry.builder()
                                        .fagsystem(kriterium.fagsystem())
                                        .antall(kriterium.antall())
                                        .detaljer(kriterium.detaljer())
                                        .build())
                                .toList())
                        .build())
                .sort(Comparator.comparing(MinSideBestillingerDTO::getDato));
    }
}
