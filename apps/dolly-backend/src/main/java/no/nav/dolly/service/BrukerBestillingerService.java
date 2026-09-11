package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.dto.DashboardAdferdDTO;
import no.nav.dolly.domain.dto.MinSideBestillingerDTO;
import no.nav.dolly.domain.projection.BestillingBrukerFragment;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.util.BrukeradferdUtils;
import no.nav.testnav.libs.reactivesecurity.action.GetUserInfo;
import no.nav.testnav.libs.securitycore.domain.UserInfoExtended;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import tools.jackson.databind.json.JsonMapper;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrukerBestillingerService {

    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final String NY_BESTILLING = "NYBESTILLING";
    private static final String GJENOPPRETTING = "GJENOPPRETTING";

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

    public Flux<MinSideBestillingerDTO> getBestillingerDetaljert(YearMonth periode) {

        return getUserInfo.call()
                .map(UserInfoExtended::id)
                .flatMapMany(id -> bestillingRepository.findKriterierByBrukerIdOrderByIdDesc(id,
                        periode.format(YEAR_MONTH_FORMATTER)))
                .groupBy(BestillingBrukerFragment::getDato)
                .flatMap(Flux::collectList)
                .map(adferd -> MinSideBestillingerDTO.builder()
                        .dato(adferd.getFirst().getDato())
                        .kriterier(getAkkumulerteKriterier(adferd))
                        .build())
                .sort(Comparator.comparing(MinSideBestillingerDTO::getDato));
    }

    private List<DashboardAdferdDTO.Entry> getAkkumulerteKriterier(List<BestillingBrukerFragment> kriterier) {

        return kriterier.stream()
                .map(kriterium -> {
                    var bestilling = jsonMapper.readValue(kriterium.getBestKriterier(), RsDollyBestilling.class);
                    return BrukeradferdUtils.getAntallAdferd(bestilling, kriterium.getAntall());
                })
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum))
                .entrySet().stream()
                .map(entry -> DashboardAdferdDTO.Entry.builder()
                        .fagsystem(entry.getKey().split("=")[0])
                        .detaljer(entry.getKey().split("=").length>1 ?
                                Arrays.stream(entry.getKey().split("=")[1].split(","))
                                        .filter(StringUtils::isNotBlank)
                                        .collect(Collectors.toMap(s -> s.split(":")[0], s -> s.split(":")[1]))
                                : null)
                        .antall(entry.getValue())
                        .build())
                .toList();
    }
}
