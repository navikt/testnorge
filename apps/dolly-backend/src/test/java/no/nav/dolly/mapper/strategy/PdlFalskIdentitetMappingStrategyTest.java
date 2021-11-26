package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.pdlforvalter.mapper.PdlFalskIdentitetMappingStrategy;
import no.nav.dolly.domain.resultset.pdlforvalter.PdlPersonnavn;
import no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet.PdlFalskIdentitet;
import no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet.PdlKjoenn;
import no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet.PdlRettIdentitetErUkjent;
import no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet.PdlRettIdentitetVedIdentifikasjonsnummer;
import no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet.RsPdlFalskIdentitet;
import no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet.RsPdlRettIdentitetVedOpplysninger;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

@ExtendWith(MockitoExtension.class)
public class PdlFalskIdentitetMappingStrategyTest {

    private static final LocalDateTime FOEDSEL_DATO = LocalDateTime.of(1989, 8, 7, 0, 0);
    private static final String IDENT = "11111111111";
    private static final String FORNAVN = "Hans";
    private static final String MELLOMNAVN = "Georg";
    private static final String ETTERNAVN = "Dahl";
    private static final String STSBRGSK_1 = "AUS";
    private static final String STSBRGSK_2 = "GER";

    private MapperFacade mapperFacade;

    @BeforeEach
    public void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new LocalDateCustomMapping(), new PdlFalskIdentitetMappingStrategy());
    }

    @Test
    public void rettIdentitetUkjent_OK() {

        PdlFalskIdentitet falskIdentitet = mapperFacade.map(RsPdlFalskIdentitet.builder()
                .rettIdentitet(PdlRettIdentitetErUkjent.builder()
                        .rettIdentitetErUkjent(true)
                        .build())
                .build(), PdlFalskIdentitet.class);

        assertThat(falskIdentitet.getRettIdentitet().getRettIdentitetErUkjent(), is(equalTo(true)));
    }

    @Test
    public void rettIdentitetVedIdentifikasjonsnummer_OK() {

        PdlFalskIdentitet falskIdentitet = mapperFacade.map(RsPdlFalskIdentitet.builder()
                .rettIdentitet(PdlRettIdentitetVedIdentifikasjonsnummer.builder()
                        .rettIdentitetVedIdentifikasjonsnummer(IDENT)
                        .build())
                .build(), PdlFalskIdentitet.class);

        assertThat(falskIdentitet.getRettIdentitet().getRettIdentitetVedIdentifikasjonsnummer(), is(equalTo(IDENT)));
    }

    @Test
    public void rettIdentitetVedOpplysninger_OK() {

        PdlFalskIdentitet falskIdentitet = mapperFacade.map(RsPdlFalskIdentitet.builder()
                .rettIdentitet(RsPdlRettIdentitetVedOpplysninger.builder()
                        .foedselsdato(FOEDSEL_DATO)
                        .kjoenn(PdlKjoenn.MANN)
                        .personnavn(PdlPersonnavn.builder()
                                .fornavn(FORNAVN)
                                .mellomnavn(MELLOMNAVN)
                                .etternavn(ETTERNAVN)
                                .build())
                        .statsborgerskap(List.of(STSBRGSK_1, STSBRGSK_2))
                        .build())
                .build(), PdlFalskIdentitet.class);

        assertThat(falskIdentitet.getRettIdentitet().getRettIdentitetVedOpplysninger().getFoedselsdato(), is(equalTo(FOEDSEL_DATO.toLocalDate())));
        assertThat(falskIdentitet.getRettIdentitet().getRettIdentitetVedOpplysninger().getKjoenn(), is(equalTo(PdlKjoenn.MANN)));
        assertThat(falskIdentitet.getRettIdentitet().getRettIdentitetVedOpplysninger().getPersonnavn().getFornavn(), is(equalTo(FORNAVN)));
        assertThat(falskIdentitet.getRettIdentitet().getRettIdentitetVedOpplysninger().getPersonnavn().getMellomnavn(), is(equalTo(MELLOMNAVN)));
        assertThat(falskIdentitet.getRettIdentitet().getRettIdentitetVedOpplysninger().getPersonnavn().getEtternavn(), is(equalTo(ETTERNAVN)));
        assertThat(falskIdentitet.getRettIdentitet().getRettIdentitetVedOpplysninger().getStatsborgerskap(), containsInAnyOrder(STSBRGSK_1, STSBRGSK_2));
    }
}