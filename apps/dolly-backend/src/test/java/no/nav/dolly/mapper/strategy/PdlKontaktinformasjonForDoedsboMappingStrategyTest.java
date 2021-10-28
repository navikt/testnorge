package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktinformasjonForDoedsbo;
import no.nav.dolly.bestilling.pdlforvalter.domain.Pdldata;
import no.nav.dolly.bestilling.pdlforvalter.mapper.PdlKontaktinformasjonForDoedsboMappingStrategy;
import no.nav.dolly.domain.resultset.pdlforvalter.PdlPersonnavn;
import no.nav.dolly.domain.resultset.pdlforvalter.RsPdldata;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.PdlSkifteform;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.RsPdlAdvokat;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.RsPdlKontaktinformasjonForDoedsbo;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.RsPdlKontaktpersonMedIdNummer;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.RsPdlOrganisasjon;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.RsRsPdlKontaktpersonUtenIdNummer;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
public class PdlKontaktinformasjonForDoedsboMappingStrategyTest {

    private static final String IDENT = "11111111111";
    private static final String FORNAVN = "Hans";
    private static final String MELLOMNAVN = "Petter";
    private static final String ETTERNAVN = "Andersen";
    private static final String ORGANISAJON_NAVN = "Tvilsom praksis AS";
    private static final String ORGANISAJON_NUMMER = "999000123";
    private static final String ADRESSELINJE_1 = "Utmyra 5";
    private static final String ADRESSELINJE_2 = "Postboks 375";
    private static final LocalDate FODSELSDATO = LocalDate.of(1985, 5, 7);
    private static final LocalDate UTSTEDT_DATO = LocalDate.now();
    private static final String LANDKODE = "POL";
    private static final String POSTNUMMER = "2345";
    private static final String POSTSTED = "BYRKJELO";

    private MapperFacade mapperFacade;

    @BeforeEach
    public void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new LocalDateCustomMapping(), new PdlKontaktinformasjonForDoedsboMappingStrategy());
    }

    @Test
    public void mapDoedsboOrganisasjonSomAdressat() {

        var result = mapperFacade.map(RsPdldata.builder()
                .kontaktinformasjonForDoedsbo(RsPdlKontaktinformasjonForDoedsbo.builder()
                        .adressat(RsPdlOrganisasjon.builder()
                                .kontaktperson(PdlPersonnavn.builder()
                                        .fornavn(FORNAVN)
                                        .mellomnavn(MELLOMNAVN)
                                        .etternavn(ETTERNAVN).build())
                                .organisasjonsnavn(ORGANISAJON_NAVN)
                                .organisasjonsnummer(ORGANISAJON_NUMMER).build())
                        .adresselinje1(ADRESSELINJE_1)
                        .adresselinje2(ADRESSELINJE_2)
                        .build()).build(), Pdldata.class);

        assertThat(result.getKontaktinformasjonForDoedsbo().getOrganisasjonSomKontakt().getOrganisasjonsnavn(), is(ORGANISAJON_NAVN));
        assertThat(result.getKontaktinformasjonForDoedsbo().getOrganisasjonSomKontakt().getOrganisasjonsnummer(), is(ORGANISAJON_NUMMER));
        assertThat(result.getKontaktinformasjonForDoedsbo().getOrganisasjonSomKontakt().getKontaktperson().getFornavn(), is(FORNAVN));
        assertThat(result.getKontaktinformasjonForDoedsbo().getOrganisasjonSomKontakt().getKontaktperson().getMellomnavn(), is(MELLOMNAVN));
        assertThat(result.getKontaktinformasjonForDoedsbo().getOrganisasjonSomKontakt().getKontaktperson().getEtternavn(), is(ETTERNAVN));
    }

    @Test
    public void mapDoedsboAdvokatSomAdressat() {

        Pdldata result = mapperFacade.map(RsPdldata.builder()
                .kontaktinformasjonForDoedsbo(RsPdlKontaktinformasjonForDoedsbo.builder()
                        .adressat(RsPdlAdvokat.builder()
                                .kontaktperson(PdlPersonnavn.builder()
                                        .fornavn(FORNAVN)
                                        .mellomnavn(MELLOMNAVN)
                                        .etternavn(ETTERNAVN).build())
                                .organisasjonsnavn(ORGANISAJON_NAVN)
                                .organisasjonsnummer(ORGANISAJON_NUMMER)

                                .build())
                        .build()).build(), Pdldata.class);

        assertThat(result.getKontaktinformasjonForDoedsbo().getAdvokatSomKontakt().getOrganisasjonsnavn(), is(ORGANISAJON_NAVN));
        assertThat(result.getKontaktinformasjonForDoedsbo().getAdvokatSomKontakt().getOrganisasjonsnummer(), is(ORGANISAJON_NUMMER));
        assertThat(result.getKontaktinformasjonForDoedsbo().getAdvokatSomKontakt().getKontaktperson().getFornavn(), is(FORNAVN));
        assertThat(result.getKontaktinformasjonForDoedsbo().getAdvokatSomKontakt().getKontaktperson().getMellomnavn(), is(MELLOMNAVN));
        assertThat(result.getKontaktinformasjonForDoedsbo().getAdvokatSomKontakt().getKontaktperson().getEtternavn(), is(ETTERNAVN));
    }

    @Test
    public void mapDoedsboKontaktpersonMedIdnummerSomAdressat() {

        Pdldata result = mapperFacade.map(RsPdldata.builder()
                .kontaktinformasjonForDoedsbo(RsPdlKontaktinformasjonForDoedsbo.builder()
                        .adressat(RsPdlKontaktpersonMedIdNummer.builder()
                                .idnummer(IDENT).build())
                        .build()).build(), Pdldata.class);

        assertThat(result.getKontaktinformasjonForDoedsbo().getPersonSomKontakt().getIdentifikasjonsnummer(), is(IDENT));
    }

    @Test
    public void mapDoedsboKontaktpersonUtenIdnummerSomAdressat() {

        Pdldata result = mapperFacade.map(RsPdldata.builder()
                .kontaktinformasjonForDoedsbo(RsPdlKontaktinformasjonForDoedsbo.builder()
                        .adressat(RsRsPdlKontaktpersonUtenIdNummer.builder()
                                .navn(PdlPersonnavn.builder()
                                        .fornavn(FORNAVN)
                                        .mellomnavn(MELLOMNAVN)
                                        .etternavn(ETTERNAVN).build())
                                .foedselsdato(FODSELSDATO.atStartOfDay()).build())
                        .build()).build(), Pdldata.class);

        assertThat(result.getKontaktinformasjonForDoedsbo().getPersonSomKontakt().getFoedselsdato(), is(FODSELSDATO));
        assertThat(result.getKontaktinformasjonForDoedsbo().getPersonSomKontakt().getNavn().getFornavn(), is(FORNAVN));
        assertThat(result.getKontaktinformasjonForDoedsbo().getPersonSomKontakt().getNavn().getMellomnavn(), is(MELLOMNAVN));
        assertThat(result.getKontaktinformasjonForDoedsbo().getPersonSomKontakt().getNavn().getEtternavn(), is(ETTERNAVN));
    }

    @Test
    public void mapDoedsboRemainingAttributes() {

        Pdldata result = mapperFacade.map(RsPdldata.builder()
                .kontaktinformasjonForDoedsbo(RsPdlKontaktinformasjonForDoedsbo.builder()
                        .adressat(RsPdlAdvokat.builder().build())
                        .adresselinje1(ADRESSELINJE_1)
                        .adresselinje2(ADRESSELINJE_2)
                        .utstedtDato(UTSTEDT_DATO.atStartOfDay())
                        .skifteform(PdlSkifteform.OFFENTLIG)
                        .landkode(LANDKODE)
                        .postnummer(POSTNUMMER)
                        .poststedsnavn(POSTSTED)
                        .build()).build(), Pdldata.class);

        assertThat(result.getKontaktinformasjonForDoedsbo().getAdresse().getAdresselinje1(), is(ADRESSELINJE_1));
        assertThat(result.getKontaktinformasjonForDoedsbo().getAdresse().getAdresselinje2(), is(ADRESSELINJE_2));
        assertThat(result.getKontaktinformasjonForDoedsbo().getAttestutstedelsesdato(), is(UTSTEDT_DATO));
        assertThat(result.getKontaktinformasjonForDoedsbo().getSkifteform(), is(PdlKontaktinformasjonForDoedsbo.PdlSkifteform.OFFENTLIG));
        assertThat(result.getKontaktinformasjonForDoedsbo().getAdresse().getLandkode(), is(LANDKODE));
        assertThat(result.getKontaktinformasjonForDoedsbo().getAdresse().getPostnummer(), is(POSTNUMMER));
        assertThat(result.getKontaktinformasjonForDoedsbo().getAdresse().getPoststedsnavn(), is(POSTSTED));
    }
}