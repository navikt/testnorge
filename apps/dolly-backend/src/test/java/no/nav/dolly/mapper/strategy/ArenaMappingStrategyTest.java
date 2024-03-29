package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.arenaforvalter.mapper.ArenaMappingStrategy;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.arenaforvalter.RsArenaAap;
import no.nav.dolly.domain.resultset.arenaforvalter.RsArenaAap115;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static java.util.Collections.singletonList;
import static no.nav.dolly.domain.resultset.arenaforvalter.ArenaBrukertype.MED_SERVICEBEHOV;
import static no.nav.dolly.domain.resultset.arenaforvalter.ArenaBrukertype.UTEN_SERVICEBEHOV;
import static no.nav.dolly.domain.resultset.arenaforvalter.ArenaKvalifiseringsgruppe.IKVAL;
import static no.nav.dolly.domain.resultset.arenaforvalter.ArenaKvalifiseringsgruppe.VARIG;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class ArenaMappingStrategyTest {

    private static final LocalDateTime TIME_NOW = LocalDateTime.now();
    private static final LocalDateTime OLD_TIMES = LocalDateTime.of(2018, 1, 1, 0, 0);
    private static final LocalDateTime PAST_TIME = LocalDateTime.of(2018, 3, 1, 0, 0);

    private MapperFacade mapperFacade;

    @BeforeEach
    public void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new LocalDateCustomMapping(), new ArenaMappingStrategy());
    }

    @Test
    void arenaBrukerUtenServicebehovMedDato() {

        ArenaNyBruker arenaNyBruker = mapperFacade.map(Arenadata.builder()
                .arenaBrukertype(UTEN_SERVICEBEHOV)
                .inaktiveringDato(TIME_NOW)
                .build(), no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker.class);

        assertThat(arenaNyBruker.getUtenServicebehov().getStansDato(), is(equalTo(TIME_NOW.toLocalDate())));
        assertThat(arenaNyBruker.getKvalifiseringsgruppe(), is(equalTo(IKVAL)));
    }

    @Test
    void arenaBrukerUtenServicebehovUtenDato() {

        ArenaNyBruker arenaNyBruker = mapperFacade.map(Arenadata.builder()
                .arenaBrukertype(UTEN_SERVICEBEHOV)
                .build(), no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker.class);

        assertThat(arenaNyBruker.getUtenServicebehov().getStansDato(), is(notNullValue()));
        assertThat(arenaNyBruker.getKvalifiseringsgruppe(), is(equalTo(IKVAL)));
    }

    @Test
    void arenaBrukerMedServicebehovVarig() {

        ArenaNyBruker arenaNyBruker = mapperFacade.map(Arenadata.builder()
                .arenaBrukertype(MED_SERVICEBEHOV)
                .kvalifiseringsgruppe(VARIG)
                .build(), no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker.class);

        assertThat(arenaNyBruker.getUtenServicebehov(), is(nullValue()));
        assertThat(arenaNyBruker.getAktiveringsDato(), is(nullValue()));
        assertThat(arenaNyBruker.getKvalifiseringsgruppe(), is(equalTo(VARIG)));
    }

    @Test
    void arenaBrukerMedAap() {

        ArenaNyBruker arenaNyBruker = mapperFacade.map(Arenadata.builder()
                .arenaBrukertype(MED_SERVICEBEHOV)
                .kvalifiseringsgruppe(VARIG)
                .aap115(singletonList(RsArenaAap115.builder()
                        .fraDato(OLD_TIMES)
                        .build()))
                .aap(singletonList(RsArenaAap.builder()
                        .fraDato(PAST_TIME)
                        .tilDato(TIME_NOW)
                        .build()))
                .build(), no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker.class);

        assertThat(arenaNyBruker.getUtenServicebehov(), is(nullValue()));
        assertThat(arenaNyBruker.getKvalifiseringsgruppe(), is(equalTo(VARIG)));
        assertThat(arenaNyBruker.getAap115(), is(nullValue()));
        assertThat(arenaNyBruker.getAap(), is(nullValue()));
        assertThat(arenaNyBruker.getAktiveringsDato(), is(equalTo(OLD_TIMES.toLocalDate())));
    }
}