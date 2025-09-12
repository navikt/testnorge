package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonVedtakRequest;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.mapper.strategy.LocalDateCustomMapping;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;

@ExtendWith(MockitoExtension.class)
class PensjonAlderspensjonVedtakMappingStrategyTest {


    private MapperFacade mapperFacade;

    @BeforeEach
    void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new LocalDateCustomMapping(),
                new PensjonAlderspensjonVedtakMappingStrategy());
    }

    @Test
    void mapAlderspensjonVerdierFraRequest_OK() {

        var pensjon = PensjonData.Alderspensjon.builder()
                .uttaksgrad(100)
                .kravFremsattDato(LocalDate.of(2023, 12, 4))
                .iverksettelsesdato(LocalDate.of(2024, 1, 1))
                .saksbehandler("Z112233")
                .attesterer("Z445566")
                .navEnhetId("1111")
                .build();

        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", "123");
        context.setProperty("miljoer", Set.of("q2"));

        var target = mapperFacade.map(pensjon, AlderspensjonVedtakRequest.class, context);

        assertThat(target.getKravFremsattDato(), is(equalTo(LocalDate.of(2023, 12, 4))));
        assertThat(target.getIverksettelsesdato(), is(equalTo(LocalDate.of(2024, 1, 1))));
        assertThat(target.getUttaksgrad(), is(equalTo(100)));
        assertThat(target.getSaksbehandler(), is(equalTo("Z112233")));
        assertThat(target.getAttesterer(), is(equalTo("Z445566")));
        assertThat(target.getNavEnhetId(), is(equalTo("1111")));

        assertThat(target.getFnr(), is(equalTo("123")));
        assertThat(target.getMiljoer(), hasItem("q2"));
    }

    @Test
    void mapAlderspensjonDefaultVerdier_OK() {

        var pensjon = PensjonData.Alderspensjon.builder()
                .uttaksgrad(100)
                .build();
        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", "123");
        context.setProperty("miljoer", Set.of("q2"));
        context.setProperty("navEnhetId", "1234");

        var target = mapperFacade.map(pensjon, AlderspensjonVedtakRequest.class, context);

        assertThat(target.getKravFremsattDato(), is(equalTo(LocalDate.now())));
        assertThat(target.getIverksettelsesdato(), is(equalTo(LocalDate.now()
                .with(TemporalAdjusters.firstDayOfNextMonth()))));
        assertThat(target.getUttaksgrad(), is(equalTo(100)));
        assertThat(target.getSaksbehandler(), matchesPattern("Z[0-9]{6}"));
        assertThat(target.getAttesterer(), matchesPattern("Z[0-9]{6}"));
        assertThat(target.getNavEnhetId(), is(equalTo("1234")));

        assertThat(target.getFnr(), is(equalTo("123")));
        assertThat(target.getMiljoer(), hasItem("q2"));
    }
}
