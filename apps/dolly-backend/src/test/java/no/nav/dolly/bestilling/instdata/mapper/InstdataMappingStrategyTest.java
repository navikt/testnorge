package no.nav.dolly.bestilling.instdata.mapper;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.instdata.domain.InstdataDTO;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.mapper.strategy.LocalDateCustomMapping;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class InstdataMappingStrategyTest {

    private static final String IDENT = "12345678901";

    private MapperFacade mapperFacade;
    private MappingContext context;

    @BeforeEach
    void setUp() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(
                new LocalDateCustomMapping(), new InstdataMappingStrategy());
        context = MappingContextUtils.getMappingContext();
        context.setProperty("ident", IDENT);
    }

    @Test
    void shouldMapIdentFromContextAndSetRegistrertAv() {

        var result = mapperFacade.map(
                RsInstdata.builder().institusjonstype(RsInstdata.InstdataInstitusjonstype.AS).build(),
                InstdataDTO.class, context);

        assertThat(result.getNorskident(), is(equalTo(IDENT)));
        assertThat(result.getRegistrertAv(), is(equalTo("Dolly")));
    }

    @ParameterizedTest
    @CsvSource({
            "AS, A, 1877666",
            "FO, S, 9700072",
            "HS, R, 1879731"
    })
    void shouldMapOppholdstypeAndInstitusjonsnummerFromInstitusjonstype(
            RsInstdata.InstdataInstitusjonstype institusjonstype, String oppholdstype, String institusjonsnummer) {

        var result = mapperFacade.map(
                RsInstdata.builder().institusjonstype(institusjonstype).build(),
                InstdataDTO.class, context);

        assertThat(result.getOppholdstype(), is(equalTo(oppholdstype)));
        assertThat(result.getInstitusjonsnummer(), is(equalTo(institusjonsnummer)));
    }

    @Test
    void shouldMapInstitusjonstypeByDefault() {

        var result = mapperFacade.map(
                RsInstdata.builder().institusjonstype(RsInstdata.InstdataInstitusjonstype.FO).build(),
                InstdataDTO.class, context);

        assertThat(result.getInstitusjonstype(), is(equalTo(InstdataDTO.InstdataInstitusjonstype.FO)));
    }

    @Test
    void shouldMapDatesFromLocalDateTimeToLocalDate() {

        var result = mapperFacade.map(
                RsInstdata.builder()
                        .institusjonstype(RsInstdata.InstdataInstitusjonstype.HS)
                        .startdato(LocalDateTime.of(2020, 1, 2, 10, 30))
                        .forventetSluttdato(LocalDateTime.of(2021, 3, 4, 8, 0))
                        .sluttdato(LocalDateTime.of(2022, 5, 6, 23, 59))
                        .build(),
                InstdataDTO.class, context);

        assertThat(result.getStartdato(), is(equalTo(LocalDate.of(2020, 1, 2))));
        assertThat(result.getForventetSluttdato(), is(equalTo(LocalDate.of(2021, 3, 4))));
        assertThat(result.getSluttdato(), is(equalTo(LocalDate.of(2022, 5, 6))));
    }

    @Test
    void shouldLeaveDatesNullWhenNotProvided() {

        var result = mapperFacade.map(
                RsInstdata.builder().institusjonstype(RsInstdata.InstdataInstitusjonstype.AS).build(),
                InstdataDTO.class, context);

        assertThat(result.getStartdato(), is(nullValue()));
        assertThat(result.getForventetSluttdato(), is(nullValue()));
        assertThat(result.getSluttdato(), is(nullValue()));
    }
}
