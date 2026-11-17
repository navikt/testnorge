package no.nav.dolly.mapper;

import no.nav.dolly.bestilling.inntektstub.domain.Inntekt;
import no.nav.dolly.bestilling.inntektstub.mapper.InntektsinformasjonMappingStrategy;
import no.nav.dolly.domain.resultset.inntektstub.RsInntekt;
import no.nav.dolly.domain.resultset.inntektstub.RsTilleggsinformasjon;
import no.nav.dolly.mapper.strategy.LocalDateCustomMapping;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MapperFacadeConfigTest {

    @Test
    void shouldUseRegisteredConvertersForNestedMappingsWithExplicitContext() {
        var mapperFacade = new MapperFacadeConfig().mapperFacade(
                List.of(new InntektsinformasjonMappingStrategy()),
                List.of(new LocalDateCustomMapping()));
        var source = RsInntekt.builder()
                .tilleggsinformasjon(RsTilleggsinformasjon.builder()
                        .etterbetalingsperiode(RsTilleggsinformasjon.Periode.builder()
                                .startdato(LocalDateTime.of(2016, 2, 29, 23, 45))
                                .build())
                        .build())
                .build();
        var context = MappingContextUtils.getMappingContext();
        context.setProperty("periode", YearMonth.of(2016, 3));

        var result = mapperFacade.map(source, Inntekt.class, context);

        assertThat(result.getTilleggsinformasjon().getEtterbetalingsperiode().getStartdato())
                .isEqualTo(LocalDate.of(2016, 2, 29));
        assertThat(result.getTilleggsinformasjon().getEtterbetalingsperiode().getSluttdato()).isNull();
    }
}
