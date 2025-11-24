package no.nav.dolly.bestilling.arbeidssoekerregisteret.mapper;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.arbeidssoekerregisteret.dto.ArbeidssoekerregisteretRequest;
import no.nav.dolly.domain.resultset.arbeidssoekerregistrering.RsArbeidssoekerregisteret;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class ArbeidssoekerregisteretMappingStrategyTest {

    private static final LocalDate GJELDER_FRA_DATO = LocalDate.now();
    private static final LocalDate GJELDER_TIL_DATO = LocalDate.now().plusYears(1);

    private MapperFacade mapperFacade;

    @BeforeEach
    void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new ArbeidssoekerregisteretMappingStrategy());
    }

    @Test
    void mapArbeidssoekerregisteretRequest() {

        var rsArbeidssoekerregisteret = buildRsArbeidssoekerregisteret();

        var context = MappingContextUtils.getMappingContext();
        context.setProperty("ident", "12345678901");
        var arbeidssoekerregisteretRequest = mapperFacade.map(rsArbeidssoekerregisteret, ArbeidssoekerregisteretRequest.class, context);

        assertThat(arbeidssoekerregisteretRequest.getIdentitetsnummer(), is(equalTo("12345678901")));
        assertThat(arbeidssoekerregisteretRequest.getUtfoertAv(), is(equalTo("utfoertAv")));
        assertThat(arbeidssoekerregisteretRequest.getAarsak(), is(equalTo("aarsak")));
        assertThat(arbeidssoekerregisteretRequest.getKilde(), is(equalTo("kilde")));
        assertThat(arbeidssoekerregisteretRequest.getNuskode(), is(equalTo("nuskode")));
        assertThat(arbeidssoekerregisteretRequest.getUtdanningBestaatt() ,is(true));
        assertThat(arbeidssoekerregisteretRequest.getUtdanningGodkjent() ,is(true));
        assertThat(arbeidssoekerregisteretRequest.getJobbsituasjonsbeskrivelse(), is(equalTo("jobbsituasjonbeskrivelse")));
        assertThat(arbeidssoekerregisteretRequest.getJobbsituasjonsdetaljer().getGjelderFraDato(), is(equalTo(GJELDER_FRA_DATO)));
        assertThat(arbeidssoekerregisteretRequest.getJobbsituasjonsdetaljer().getGjelderTilDato(), is(equalTo(GJELDER_TIL_DATO)));
        assertThat(arbeidssoekerregisteretRequest.getJobbsituasjonsdetaljer().getStillingStyrk08(), is(equalTo(1234)));
        assertThat(arbeidssoekerregisteretRequest.getJobbsituasjonsdetaljer().getStillingstittel(), is(equalTo("stillingstittel")));
        assertThat(arbeidssoekerregisteretRequest.getJobbsituasjonsdetaljer().getStillingsprosent(), is(equalTo(100)));
        assertThat(arbeidssoekerregisteretRequest.getJobbsituasjonsdetaljer().getSisteDagMedLoenn(), is(equalTo(GJELDER_FRA_DATO)));
        assertThat(arbeidssoekerregisteretRequest.getJobbsituasjonsdetaljer().getSisteArbeidsdag(), is(equalTo(GJELDER_TIL_DATO)));
        assertThat(arbeidssoekerregisteretRequest.getHelsetilstandHindrerArbeid(), is(true));
        assertThat(arbeidssoekerregisteretRequest.getAndreForholdHindrerArbeid(), is(true));
    }

    private RsArbeidssoekerregisteret buildRsArbeidssoekerregisteret() {

        return RsArbeidssoekerregisteret.builder()
                .utfoertAv("utfoertAv")
                .aarsak("aarsak")
                .kilde("kilde")
                .nuskode("nuskode")
                .utdanningBestaatt(true)
                .utdanningGodkjent(true)
                .jobbsituasjonsbeskrivelse("jobbsituasjonbeskrivelse")
                .jobbsituasjonsdetaljer(RsArbeidssoekerregisteret.JobbsituasjonDetaljer.builder()
                        .gjelderFraDato(GJELDER_FRA_DATO)
                        .gjelderTilDato(GJELDER_TIL_DATO)
                        .stillingStyrk08(1234)
                        .stillingstittel("stillingstittel")
                        .stillingsprosent(100)
                        .sisteDagMedLoenn(GJELDER_FRA_DATO)
                        .sisteArbeidsdag(GJELDER_TIL_DATO)
                        .build())
                .helsetilstandHindrerArbeid(true)
                .andreForholdHindrerArbeid(true)
                .build();
    }
}