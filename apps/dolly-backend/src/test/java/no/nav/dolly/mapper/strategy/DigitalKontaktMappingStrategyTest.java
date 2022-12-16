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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class DigitalKontaktMappingStrategyTest {

    private static final String EPOST = "test@nav.no";
    private static final String MOBIL = "99990000";
    private static final String SPRAAK = "NO";
    private static final boolean RESERVERT = true;
    private static final LocalDateTime GYLDIG_FRA = LocalDateTime.of(2018, 1, 1, 0, 0);
    private static final ZonedDateTime Z_GYLDIG_FRA = ZonedDateTime.of(GYLDIG_FRA, ZoneId.systemDefault());

    private MapperFacade mapperFacade;

    private MappingContext context;

    @BeforeEach
    public void setup() {
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
    void mapMobil_OK() {

        var result = mapperFacade.map(RsDigitalKontaktdata.builder()
                .gyldigFra(GYLDIG_FRA)
                .mobil(MOBIL)
                .build(), DigitalKontaktdata.class, context);

        assertThat(result.getMobil(), is(equalTo(MOBIL)));
        assertThat(result.getMobilOppdatert(), is(equalTo(Z_GYLDIG_FRA)));
        assertThat(result.getMobilVerifisert(), is(equalTo(Z_GYLDIG_FRA)));
    }

    @Test
    void mapEpost_OK() {

        var result = mapperFacade.map(RsDigitalKontaktdata.builder()
                .gyldigFra(GYLDIG_FRA)
                .epost(EPOST)
                .build(), DigitalKontaktdata.class, context);

        assertThat(result.getEpost(), is(equalTo(EPOST)));
        assertThat(result.getEpostOppdatert(), is(equalTo(Z_GYLDIG_FRA)));
        assertThat(result.getEpostVerifisert(), is(equalTo(Z_GYLDIG_FRA)));
    }

    @Test
    void mapSpraak_OK() {

        var result = mapperFacade.map(RsDigitalKontaktdata.builder()
                .gyldigFra(GYLDIG_FRA)
                .spraak(SPRAAK)
                .build(), DigitalKontaktdata.class, context);

        assertThat(result.getSpraak(), is(equalTo(SPRAAK)));
        assertThat(result.getSpraakOppdatert(), is(equalTo(Z_GYLDIG_FRA)));
    }
}