package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonUforetrygdRequest;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.mapper.strategy.LocalDateCustomMapping;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;

@ExtendWith(MockitoExtension.class)
class PensjonUforetrygdMappingStrategyTest {

    private static final String IDENT = "ident";
    private static final String FNR_1 = "12345678901";
    private static final String PERSONDATA = "persondata";
    private static final LocalDate UFORE_TIDSPUNKT = LocalDate.of(2015, 1, 1);
    private static final String ANSATT = "Z991234";
    private MapperFacade mapperFacade;

    @BeforeEach
    void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new LocalDateCustomMapping(),
                new PensjonUforetrygdMappingStrategy());
    }

    @Test
    void uforetrygdDatoIkkeSatt_setterDagensDato() {

        var context = new MappingContext.Factory().getContext();
        context.setProperty(PERSONDATA, Collections.emptyList());
        var resultat = mapperFacade.map(new PensjonData.Uforetrygd(), PensjonUforetrygdRequest.class, context);

        assertThat(resultat.getOnsketVirkningsDato(), is(equalTo(getNesteMaaned())));
        assertThat(resultat.getKravFremsattDato(), is(equalTo(LocalDate.now())));
        assertThat(resultat.getUforetidspunkt(), is(equalTo(getForrigeMaaned())));
    }

    @Test
    void uforetrygdDatoSattPaaEtAvFeltene_setterOverigeDatoer() {

        var context = new MappingContext.Factory().getContext();
        context.setProperty(PERSONDATA, Collections.emptyList());
        var resultat = mapperFacade.map(PensjonData.Uforetrygd.builder()
                .onsketVirkningsDato(UFORE_TIDSPUNKT)
                .build(), PensjonUforetrygdRequest.class, context);

        assertThat(resultat.getOnsketVirkningsDato(), is(equalTo(UFORE_TIDSPUNKT)));
        assertThat(resultat.getKravFremsattDato(), is(equalTo(LocalDate.now())));
        assertThat(resultat.getUforetidspunkt(), is(equalTo(getForrigeMaaned())));
    }

    @Test
    void saksbekandlerOgAttestererIkkeSatt_setterDolly() {

        var context = new MappingContext.Factory().getContext();
        context.setProperty(PERSONDATA, Collections.emptyList());
        var resultat = mapperFacade.map(new PensjonData.Uforetrygd(), PensjonUforetrygdRequest.class, context);

        assertThat(resultat.getSaksbehandler(), stringContainsInOrder("Z", "9"));
        assertThat(resultat.getAttesterer(),  stringContainsInOrder("Z", "9"));
    }

    @Test
    void saksbehandlerEllerAttestertHarVerdi_setterDenAndreAnsatte() {

        var context = new MappingContext.Factory().getContext();
        context.setProperty(PERSONDATA, Collections.emptyList());
        var resultat = mapperFacade.map(PensjonData.Uforetrygd.builder()
                .saksbehandler(ANSATT)
                .build(), PensjonUforetrygdRequest.class, context);

        assertThat(resultat.getSaksbehandler(), is(equalTo(ANSATT)));
        assertThat(resultat.getAttesterer(),  stringContainsInOrder("Z", "9"));
    }

    private static LocalDate getForrigeMaaned() {

        var forrigeMaaned = LocalDate.now().minusMonths(1);
        return LocalDate.of(forrigeMaaned.getYear(), forrigeMaaned.getMonth(), 1);
    }

    private static LocalDate getNesteMaaned() {

        var nesteMaaned = LocalDate.now().plusMonths(1);
        return LocalDate.of(nesteMaaned.getYear(), nesteMaaned.getMonth(), 1);
    }
}