package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.MapperFacade;
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

public class DigitalKontaktMappingStrategyTest {

    private static final String EPOST = "test@nav.no";
    private static final String MOBIL = "99990000";
    private static final String SPRAAK = "NO";
    private static final boolean RESERVERT = true;
    private static final LocalDateTime GYLDIG_FRA = LocalDateTime.of(2018, 1, 1, 0, 0);
    private static final ZonedDateTime Z_GYLDIG_FRA = ZonedDateTime.of(GYLDIG_FRA, ZoneId.systemDefault());

    private MapperFacade mapperFacade;

    @BeforeEach
    public void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new DigitalKontaktMappingStrategy());
    }

    @Test
    public void mapReservert_OK() {

        DigitalKontaktdata result = mapperFacade.map(RsDigitalKontaktdata.builder()
                .reservert(RESERVERT)
                .gyldigFra(GYLDIG_FRA)
                .build(), DigitalKontaktdata.class);

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
    public void mapMobil_OK() {

        DigitalKontaktdata result = mapperFacade.map(RsDigitalKontaktdata.builder()
                .gyldigFra(GYLDIG_FRA)
                .mobil(MOBIL)
                .build(), DigitalKontaktdata.class);

        assertThat(result.getMobil(), is(equalTo(MOBIL)));
        assertThat(result.getMobilOppdatert(), is(equalTo(Z_GYLDIG_FRA)));
        assertThat(result.getMobilVerifisert(), is(equalTo(Z_GYLDIG_FRA)));
    }

    @Test
    public void mapEpost_OK() {

        DigitalKontaktdata result = mapperFacade.map(RsDigitalKontaktdata.builder()
                .gyldigFra(GYLDIG_FRA)
                .epost(EPOST)
                .build(), DigitalKontaktdata.class);

        assertThat(result.getEpost(), is(equalTo(EPOST)));
        assertThat(result.getEpostOppdatert(), is(equalTo(Z_GYLDIG_FRA)));
        assertThat(result.getEpostVerifisert(), is(equalTo(Z_GYLDIG_FRA)));
    }

    @Test
    public void mapSpraak_OK() {

        DigitalKontaktdata result = mapperFacade.map(RsDigitalKontaktdata.builder()
                .gyldigFra(GYLDIG_FRA)
                .spraak(SPRAAK)
                .build(), DigitalKontaktdata.class);

        assertThat(result.getSpraak(), is(equalTo(SPRAAK)));
        assertThat(result.getSpraakOppdatert(), is(equalTo(Z_GYLDIG_FRA)));
    }
}