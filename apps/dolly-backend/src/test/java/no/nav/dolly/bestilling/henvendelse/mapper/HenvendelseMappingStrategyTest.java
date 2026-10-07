package no.nav.dolly.bestilling.henvendelse.mapper;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.henvendelse.dto.HenvendelseMeldingRequest;
import no.nav.dolly.bestilling.henvendelse.dto.HenvendelseSamtalereferatRequest;
import no.nav.dolly.domain.resultset.henvendelse.RsHenvendelse;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class HenvendelseMappingStrategyTest {

    private static final String AKTOR_ID = "synthetic-aktor";
    private static final String CONTEXT_ENHET = "0315";
    private static final String EXPLICIT_ENHET = "1234";

    private MapperFacade mapperFacade;
    private MappingContext context;

    @BeforeEach
    void setup() {

        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new HenvendelseMappingStrategy());
        context = MappingContextUtils.getMappingContext();
        context.setProperty("aktorId", AKTOR_ID);
        context.setProperty("enhet", CONTEXT_ENHET);
    }

    @Test
    void shouldMapMeldingAndPreserveExplicitEnhet() {

        var source = new RsHenvendelse.Melding("ARBD", "DAG", EXPLICIT_ENHET, "message", true, "message-chain");

        var result = mapperFacade.map(source, HenvendelseMeldingRequest.class, context);

        assertThat(result.getAktorId()).isEqualTo(AKTOR_ID);
        assertThat(result.getType()).isEqualTo("melding");
        assertThat(result.getTemagruppe()).isEqualTo(source.getTemagruppe());
        assertThat(result.getTema()).isEqualTo(source.getTema());
        assertThat(result.getEnhet()).isEqualTo(EXPLICIT_ENHET);
        assertThat(result.getFritekst()).isEqualTo(source.getFritekst());
        assertThat(result.getKjedeId()).isEqualTo(source.getKjedeId());
        assertThat(result.getTildelMeg()).isTrue();
    }

    @Test
    void shouldMapSamtalereferatAndPreserveExplicitEnhet() {

        var source = new RsHenvendelse.Samtalereferat(
                "ARBD", "DAG", EXPLICIT_ENHET, "call summary", "summary-chain");

        var result = mapperFacade.map(source, HenvendelseSamtalereferatRequest.class, context);

        assertThat(result.getAktorId()).isEqualTo(AKTOR_ID);
        assertThat(result.getType()).isEqualTo("samtalereferat");
        assertThat(result.getTemagruppe()).isEqualTo(source.getTemagruppe());
        assertThat(result.getTema()).isEqualTo(source.getTema());
        assertThat(result.getEnhet()).isEqualTo(EXPLICIT_ENHET);
        assertThat(result.getFritekst()).isEqualTo(source.getFritekst());
        assertThat(result.getKjedeId()).isEqualTo(source.getKjedeId());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void shouldUseContextEnhetWhenMeldingEnhetIsBlank(String enhet) {

        var source = new RsHenvendelse.Melding();
        source.setEnhet(enhet);

        var result = mapperFacade.map(source, HenvendelseMeldingRequest.class, context);

        assertThat(result.getEnhet()).isEqualTo(CONTEXT_ENHET);
        assertThat(source.getEnhet()).isEqualTo(enhet);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void shouldUseContextEnhetWhenSamtalereferatEnhetIsBlank(String enhet) {

        var source = new RsHenvendelse.Samtalereferat();
        source.setEnhet(enhet);

        var result = mapperFacade.map(source, HenvendelseSamtalereferatRequest.class, context);

        assertThat(result.getEnhet()).isEqualTo(CONTEXT_ENHET);
        assertThat(source.getEnhet()).isEqualTo(enhet);
    }
}
