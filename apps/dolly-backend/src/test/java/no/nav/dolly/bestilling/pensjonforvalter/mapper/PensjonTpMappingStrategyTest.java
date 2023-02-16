package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTpForholdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTpYtelseRequest;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.mapper.strategy.LocalDateCustomMapping;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class PensjonTpMappingStrategyTest {

    private MapperFacade mapperFacade;

    @BeforeEach
    void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new LocalDateCustomMapping(), new PensjonTpMappingStrategy());
    }

    @Test
    void mapTpOrdning_OK() {

        var tpOrdning = PensjonData.TpOrdning.builder()
                .ordning("Denne ordningen")
                .build();
        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", "123");
        context.setProperty("miljoer", Set.of("q2"));

        var target = mapperFacade.map(tpOrdning, PensjonTpForholdRequest.class, context);

        assertThat(target.getOrdning(), is(equalTo("Denne ordningen")));
        assertThat(target.getFnr(), is(equalTo("123")));
        assertThat(target.getMiljoer(), hasItem("q2"));
    }

    @Test
    void mapTpYtelse_OK() {

        var tpOrdning = PensjonData.TpYtelse.builder()
                .datoInnmeldtYtelseFom(LocalDate.of(2015, 1, 1))
                .datoYtelseIverksattFom(LocalDate.of(2018, 1, 1))
                .datoYtelseIverksattTom(LocalDate.of(2022, 12, 31))
                .type(PensjonData.TpYtelseType.AFP)
                .build();
        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", "123");
        context.setProperty("miljoer", Set.of("q2"));
        context.setProperty("ordning", "Denne ordningen");

        var target = mapperFacade.map(tpOrdning, PensjonTpYtelseRequest.class, context);

        assertThat(target.getOrdning(), is(equalTo("Denne ordningen")));
        assertThat(target.getFnr(), is(equalTo("123")));
        assertThat(target.getMiljoer(), hasItem("q2"));
        assertThat(target.getDatoInnmeldtYtelseFom(), is(equalTo(LocalDate.of(2015, 1, 1))));
        assertThat(target.getDatoYtelseIverksattFom(), is(equalTo(LocalDate.of(2018, 1, 1))));
        assertThat(target.getDatoYtelseIverksattTom(), is(equalTo(LocalDate.of(2022, 12, 31))));
    }
}