package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.inntektstub.RsInntekter;
import no.nav.dolly.domain.resultset.inntektstub.RsInntektsinformasjon;
import no.nav.dolly.repository.BestillingRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class InntektService {

    private final BestillingRepository bestillingRepository;
    private final JsonMapper jsonMapper;
    private final MapperFacade mapperFacade;

    private static final DateTimeFormatter YEAR_MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    public Mono<Void> deleteInntekt(String ident, YearMonth periode) {

        return bestillingRepository.findBestillingerByIdent(ident)
                .flatMap(bestilling -> Mono.just(jsonMapper.readValue(bestilling.getBestKriterier(), RsDollyBestilling.class))
                        .filter(dollyBestilling -> !dollyBestilling.getInntekter().isEmpty() &&
                                                   dollyBestilling.getInntekter().stream()
                                                           .anyMatch(inntekt -> IntStream.range(0, normaliserAntallMaaneder(inntekt.getAntallMaaneder()))
                                                                   .mapToObj(i -> YearMonth.parse(inntekt.getStartAarMaaned(), YEAR_MONTH_FORMAT).plusMonths(i))
                                                                   .anyMatch(periode::equals)) ||

                                                   nonNull(dollyBestilling.getInntektstub()) &&
                                                   !dollyBestilling.getInntektstub().getInntektsinformasjon().isEmpty() &&
                                                   dollyBestilling.getInntektstub().getInntektsinformasjon().stream()
                                                           .anyMatch(inntekt -> IntStream.range(0, normaliserAntallMaaneder(inntekt.getAntallMaaneder()))
                                                                   .mapToObj(i -> YearMonth.parse(inntekt.getSisteAarMaaned(), YEAR_MONTH_FORMAT).minusMonths(i))
                                                                   .anyMatch(periode::equals)))
                        .flatMap(dollyBestilling -> {
                            var oppdatertBestilling = ekskluderPeriode(dollyBestilling, periode);
                            bestilling.setBestKriterier(jsonMapper.writeValueAsString(oppdatertBestilling));

                            return bestillingRepository.save(bestilling);
                        }))
                .then();
    }

    private RsDollyBestilling ekskluderPeriode(
            RsDollyBestilling dollyBestilling,
            YearMonth periode) {

        dollyBestilling.setInntekter(
                dollyBestilling.getInntekter().stream()
                        .flatMap(inntekt -> ekskluderPeriode(inntekt, periode).stream())
                        .toList());

        if (nonNull(dollyBestilling.getInntektstub())) {
            dollyBestilling.getInntektstub().setInntektsinformasjon(
                    dollyBestilling.getInntektstub().getInntektsinformasjon().stream()
                            .flatMap(inntekt -> ekskluderPeriode(inntekt, periode).stream())
                            .toList());
        }

        return dollyBestilling;
    }

    private List<RsInntekter> ekskluderPeriode(
            RsInntekter inntekt,
            YearMonth periode) {

        var start = YearMonth.parse(inntekt.getStartAarMaaned(), YEAR_MONTH_FORMAT);
        var antallMaaneder = normaliserAntallMaaneder(inntekt.getAntallMaaneder());
        var slutt = start.plusMonths(antallMaaneder - 1L);

        if (periode.isBefore(start) || periode.isAfter(slutt)) {
            return List.of(inntekt);
        }

        var perioder = new ArrayList<RsInntekter>();

        var antallFoer = Math.toIntExact(ChronoUnit.MONTHS.between(start, periode));
        if (antallFoer > 0) {
            perioder.add(kopier(inntekt, start, antallFoer));
        }

        var antallEtter = Math.toIntExact(ChronoUnit.MONTHS.between(periode, slutt));
        if (antallEtter > 0) {
            perioder.add(kopier(inntekt, periode.plusMonths(1), antallEtter));
        }

        return perioder;
    }

    private RsInntekter kopier(RsInntekter inntekt, YearMonth start, int antallFoer) {

        var kopi = mapperFacade.map(inntekt, RsInntekter.class);
        kopi.setStartAarMaaned(start.format(YEAR_MONTH_FORMAT));
        kopi.setAntallMaaneder(antallFoer);
        return kopi;
    }

    private List<RsInntektsinformasjon> ekskluderPeriode(
            RsInntektsinformasjon inntekt,
            YearMonth periode) {

        var slutt = YearMonth.parse(inntekt.getSisteAarMaaned(), YEAR_MONTH_FORMAT);
        var antallMaaneder = normaliserAntallMaaneder(inntekt.getAntallMaaneder());
        var start = slutt.minusMonths(antallMaaneder - 1L);

        if (periode.isBefore(start) || periode.isAfter(slutt)) {
            return List.of(inntekt);
        }

        var perioder = new ArrayList<RsInntektsinformasjon>();

        var antallFoer = Math.toIntExact(ChronoUnit.MONTHS.between(start, periode));
        if (antallFoer > 0) {
            perioder.add(kopier(inntekt, periode.minusMonths(1), antallFoer));
        }

        var antallEtter = Math.toIntExact(ChronoUnit.MONTHS.between(periode, slutt));
        if (antallEtter > 0) {
            perioder.add(kopier(inntekt, slutt, antallEtter));
        }

        return perioder;
    }

    private RsInntektsinformasjon kopier(RsInntektsinformasjon inntekt, YearMonth slutt, int antallEtter) {

        var kopi = mapperFacade.map(inntekt, RsInntektsinformasjon.class);
        kopi.setSisteAarMaaned(slutt.format(YEAR_MONTH_FORMAT));
        kopi.setAntallMaaneder(antallEtter);
        return kopi;
    }

    private int normaliserAntallMaaneder(Integer antallMaaneder) {
        return isNull(antallMaaneder) || antallMaaneder <= 0 ? 1 : antallMaaneder;
    }
}
