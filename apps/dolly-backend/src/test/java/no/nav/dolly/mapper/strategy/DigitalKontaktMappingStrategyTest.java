package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.krrstub.mapper.DigitalKontaktMappingStrategy;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static no.nav.dolly.util.DateZoneUtil.CET;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class DigitalKontaktMappingStrategyTest {

    private static final String EPOST = "test@nav.no";
    private static final String MOBIL_INCORRECT = "99990000";
    private static final String MOBIL_INCORRECT_SPACE = "+47 99990000";
    private static final String MOBIL_CORRECT = "+4799990000";
    private static final String SPRAAK = "NO";
    private static final boolean RESERVERT = true;
    private static final LocalDate GYLDIG_FRA = LocalDate.of(2018, 1, 1);
    private static final ZonedDateTime Z_GYLDIG_FRA = ZonedDateTime.of(GYLDIG_FRA.atStartOfDay(), ZoneId.of("UTC"));
    private static final LocalDate DATE_NOW = LocalDate.now(CET);

    private MapperFacade mapperFacade;

    private MappingContext context;

    @BeforeEach
    void setup() {

        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new DigitalKontaktMappingStrategy());

        context = new MappingContext.Factory().getContext();
        context.setProperty("bestilling", new RsDollyUtvidetBestilling());
    }

    @Test
    void mapReservert_OK() {

        var result = mapperFacade.map(RsDigitalKontaktdata.builder()
                .reservert(RESERVERT)
                .gyldigFra(GYLDIG_FRA)
                .build(), DigitalKontaktdata.class, context);

        assertThat(result.isReservert(), is(equalTo(RESERVERT)));
        assertThat(result.getEpost(), is(nullValue()));
        assertThat(result.getEpostOppdatert(), is(nullValue()));
        assertThat(result.getEpostVerifisert(), is(nullValue()));
        assertThat(result.getMobil(), is(nullValue()));
        assertThat(result.getMobilOppdatert(), is(nullValue()));
        assertThat(result.getMobilVerifisert(), is(nullValue()));
        assertThat(result.getGyldigFra(), is(equalTo(Z_GYLDIG_FRA)));
    }

    @Test
    void mapMobil_Incorrect_OK() {

        var result = mapperFacade.map(RsDigitalKontaktdata.builder()
                .gyldigFra(GYLDIG_FRA)
                .mobil(MOBIL_INCORRECT)
                .build(), DigitalKontaktdata.class, context);

        assertThat(result.getMobil(), is(equalTo(MOBIL_CORRECT)));
        assertThat(result.getMobilOppdatert().toLocalDate(), is(equalTo(DATE_NOW)));
    }

    @Test
    void mapMobil_WithSpace_OK() {

        var result = mapperFacade.map(RsDigitalKontaktdata.builder()
                .gyldigFra(GYLDIG_FRA)
                .mobil(MOBIL_INCORRECT_SPACE)
                .build(), DigitalKontaktdata.class, context);

        assertThat(result.getMobil(), is(equalTo(MOBIL_CORRECT)));
        assertThat(result.getMobilOppdatert().toLocalDate(), is(equalTo(DATE_NOW)));
    }

    @Test
    void mapEpost_OK() {

        var result = mapperFacade.map(RsDigitalKontaktdata.builder()
                .gyldigFra(GYLDIG_FRA)
                .epost(EPOST)
                .build(), DigitalKontaktdata.class, context);

        assertThat(result.getEpost(), is(equalTo(EPOST)));
        assertThat(result.getEpostOppdatert().toLocalDate(), is(equalTo(DATE_NOW)));
    }

    @Test
    void mapSpraak_OK() {

        var result = mapperFacade.map(RsDigitalKontaktdata.builder()
                .gyldigFra(GYLDIG_FRA)
                .spraak(SPRAAK)
                .build(), DigitalKontaktdata.class, context);

        assertThat(result.getSpraak(), is(equalTo(SPRAAK)));
        assertThat(result.getSpraakOppdatert().toLocalDate(), is(equalTo(DATE_NOW)));
    }
}