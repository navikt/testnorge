package no.nav.dolly.mapper.strategy;

import static no.nav.dolly.domain.resultset.arenaforvalter.ArenaBrukertype.MED_SERVICEBEHOV;
import static no.nav.dolly.domain.resultset.arenaforvalter.ArenaBrukertype.UTEN_SERVICEBEHOV;
import static no.nav.dolly.domain.resultset.arenaforvalter.ArenaKvalifiseringsgruppe.IKVAL;
import static no.nav.dolly.domain.resultset.arenaforvalter.ArenaKvalifiseringsgruppe.VARIG;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.mapper.utils.MapperTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class ArenaMappingStrategyTest {

    private static final LocalDateTime TIME_NOW = LocalDateTime.of(2018, 1, 1, 0, 0);

    private MapperFacade mapperFacade;

    @Before
    public void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new ArenaMappingStrategy());
    }

    @Test
    public void arenaBrukerUtenServicebehovMedDato() {

        ArenaNyBruker arenaNyBruker = mapperFacade.map(Arenadata.builder()
                .arenaBrukertype(UTEN_SERVICEBEHOV)
                .inaktiveringDato(TIME_NOW)
                .build(), no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker.class);

        assertThat(arenaNyBruker.getUtenServicebehov().getStansDato(), is(equalTo(TIME_NOW.toLocalDate())));
        assertThat(arenaNyBruker.getArenaKvalifiseringsgruppe(), is(equalTo(IKVAL)));
    }

    @Test
    public void arenaBrukerUtenServicebehovUtenDato() {

        ArenaNyBruker arenaNyBruker = mapperFacade.map(Arenadata.builder()
                .arenaBrukertype(UTEN_SERVICEBEHOV)
                .build(), no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker.class);

        assertThat(arenaNyBruker.getUtenServicebehov().getStansDato(), is(nullValue()));
        assertThat(arenaNyBruker.getArenaKvalifiseringsgruppe(), is(equalTo(IKVAL)));
    }

    @Test
    public void arenaBrukerMedServicebehovVarig() {

        ArenaNyBruker arenaNyBruker = mapperFacade.map(Arenadata.builder()
                .arenaBrukertype(MED_SERVICEBEHOV)
                .arenaKvalifiseringsgruppe(VARIG)
                .build(), no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker.class);

        assertThat(arenaNyBruker.getUtenServicebehov(), is(nullValue()));
        assertThat(arenaNyBruker.getArenaKvalifiseringsgruppe(), is(equalTo(VARIG)));
    }
}