package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonRequest;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.mapper.strategy.LocalDateCustomMapping;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class PensjonAlderspensjonMappingStrategyTest {


    private MapperFacade mapperFacade;

    @BeforeEach
    void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new LocalDateCustomMapping(),
                new PensjonAlderspensjonMappingStrategy());
    }

    @Test
    void mapAlderspensjon_OK() {

        var pensjon = PensjonData.Alderspensjon.builder()
                .iverksettelsesdato(LocalDate.of(2023,1,11))
                .sivilstand("GIFT")
                .sivilstatusDatoFom(LocalDate.of(1988,1,2))
                .uttaksgrad(100)
                .build();
        var context = MappingContextUtils.getMappingContext();
        context.setProperty("ident", "123");
        context.setProperty("miljoer", List.of("q2"));

        var target = mapperFacade.map(pensjon, AlderspensjonRequest.class, context);

        assertThat(target.getIverksettelsesdato(), is(equalTo(LocalDate.of(2023,1,11))));
        assertThat(target.getSivilstand(), is(equalTo("GIFT")));
        assertThat(target.getSivilstatusDatoFom(), is(equalTo(LocalDate.of(1988,1,2))));
        assertThat(target.getUttaksgrad(), is(equalTo(100)));

        assertThat(target.getPid(), is(equalTo("123")));
        assertThat(target.getMiljoer(), hasItem("q2"));
    }
}