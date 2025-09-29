package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonSamboerRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonSivilstandWrapper;
import no.nav.dolly.mapper.strategy.LocalDateCustomMapping;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.testnav.libs.data.pdlforvalter.v1.SivilstandDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class PensjonSamboerMappingStrategyTest {

    private static final String IDENT_1 = "11111111111";
    private static final String IDENT_2 = "22222222222";
    private static final String IDENT_3 = "33333333333";
    private static final String IDENT_4 = "44444444444";
    private static final LocalDateTime SIVILSTAND_DATO = LocalDateTime.now();
    private static final LocalDateTime SIVILSTAND_DATO_EARLIEST = LocalDateTime.of(2000, 10, 10, 0, 0);
    private static final LocalDateTime SIVILSTAND_DATO_MIDDLE = LocalDateTime.of(2010, 10, 10, 0, 0);
    private static final LocalDateTime SIVILSTAND_DATO_LATEST = LocalDateTime.of(2020, 10, 10, 0, 0);


    private MapperFacade mapperFacade;

    private static SivilstandDTO buildSivilstand() {

        return SivilstandDTO.builder()
                .id(1)
                .type(SivilstandDTO.Sivilstand.SAMBOER)
                .relatertVedSivilstand(IDENT_2)
                .sivilstandsdato(SIVILSTAND_DATO)
                .build();
    }

    private static List<SivilstandDTO> buildMultipleSivilstand() {

        var sivilstandOne = SivilstandDTO.builder()
                .id(1)
                .type(SivilstandDTO.Sivilstand.SAMBOER)
                .relatertVedSivilstand(IDENT_2)
                .sivilstandsdato(SIVILSTAND_DATO_EARLIEST)
                .build();
        var sivilstandTwo = SivilstandDTO.builder()
                .id(2)
                .type(SivilstandDTO.Sivilstand.GIFT)
                .relatertVedSivilstand(IDENT_3)
                .sivilstandsdato(SIVILSTAND_DATO_MIDDLE)
                .build();
        var sivilstandThree = SivilstandDTO.builder()
                .id(3)
                .type(SivilstandDTO.Sivilstand.SAMBOER)
                .relatertVedSivilstand(IDENT_4)
                .sivilstandsdato(SIVILSTAND_DATO_LATEST)
                .build();

        return List.of(sivilstandOne, sivilstandTwo, sivilstandThree);
    }

    @BeforeEach
    void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new LocalDateCustomMapping(),
                new PensjonSamboerMappingStrategy());
    }

    @Test
    void mapSamboerMedSivilstandDato_OK() {

        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", IDENT_1);
        var resultat = (List<PensjonSamboerRequest>) mapperFacade.map(PensjonSivilstandWrapper.builder()
                .sivilstander(List.of(buildSivilstand()))
                .build(), List.class, context);

        assertThat(resultat, containsInAnyOrder(
                allOf(hasProperty("pidBruker", is(equalTo(IDENT_1))),
                        hasProperty("pidSamboer", is(equalTo(IDENT_2))),
                        hasProperty("datoFom", is(equalTo(SIVILSTAND_DATO.toLocalDate()))),
                        hasProperty("registrertAv", is(equalTo("Dolly")))),
                allOf(hasProperty("pidBruker", is(equalTo(IDENT_2))),
                        hasProperty("pidSamboer", is(equalTo(IDENT_1))),
                        hasProperty("datoFom", is(equalTo(SIVILSTAND_DATO.toLocalDate()))),
                        hasProperty("registrertAv", is(equalTo("Dolly"))))));
    }

    @Test
    void mapSamboerMedRegistretDato_OK() {

        var sivilstand = buildSivilstand();
        sivilstand.setSivilstandsdato(null);
        sivilstand.setBekreftelsesdato(SIVILSTAND_DATO);

        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", IDENT_1);
        var resultat = (List<PensjonSamboerRequest>) mapperFacade.map(PensjonSivilstandWrapper.builder()
                .sivilstander(List.of(sivilstand))
                .build(), List.class, context);

        assertThat(resultat, containsInAnyOrder(
                allOf(hasProperty("pidBruker", is(equalTo(IDENT_1))),
                        hasProperty("pidSamboer", is(equalTo(IDENT_2))),
                        hasProperty("datoFom", is(equalTo(SIVILSTAND_DATO.toLocalDate()))),
                        hasProperty("registrertAv", is(equalTo("Dolly")))),
                allOf(hasProperty("pidBruker", is(equalTo(IDENT_2))),
                        hasProperty("pidSamboer", is(equalTo(IDENT_1))),
                        hasProperty("datoFom", is(equalTo(SIVILSTAND_DATO.toLocalDate()))),
                        hasProperty("registrertAv", is(equalTo("Dolly"))))));
    }

    @Test
    void mapSamboerUtenDato_OK() {

        var sivilstand = buildSivilstand();
        sivilstand.setSivilstandsdato(null);

        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", IDENT_1);
        var resultat = (List<PensjonSamboerRequest>) mapperFacade.map(PensjonSivilstandWrapper.builder()
                .sivilstander(List.of(sivilstand))
                .build(), List.class, context);

        assertThat(resultat, containsInAnyOrder(
                allOf(hasProperty("pidBruker", is(equalTo(IDENT_1))),
                        hasProperty("pidSamboer", is(equalTo(IDENT_2))),
                        hasProperty("datoFom", is(equalTo(SIVILSTAND_DATO.toLocalDate()))),
                        hasProperty("registrertAv", is(equalTo("Dolly")))),
                allOf(hasProperty("pidBruker", is(equalTo(IDENT_2))),
                        hasProperty("pidSamboer", is(equalTo(IDENT_1))),
                        hasProperty("datoFom", is(equalTo(SIVILSTAND_DATO.toLocalDate()))),
                        hasProperty("registrertAv", is(equalTo("Dolly"))))));
    }

    @Test
    void mapSamboerOgSenereGift_OK() {

        var sivilstandSamboer = buildSivilstand();
        var sivilstandGift = buildSivilstand();
        sivilstandGift.setType(SivilstandDTO.Sivilstand.GIFT);
        sivilstandGift.setSivilstandsdato(SIVILSTAND_DATO.plusYears(1));
        sivilstandGift.setId(2);

        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", IDENT_1);
        var resultat = (List<PensjonSamboerRequest>) mapperFacade.map(PensjonSivilstandWrapper.builder()
                .sivilstander(List.of(sivilstandSamboer, sivilstandGift))
                .build(), List.class, context);

        assertThat(resultat, containsInAnyOrder(
                allOf(hasProperty("pidBruker", is(equalTo(IDENT_1))),
                        hasProperty("pidSamboer", is(equalTo(IDENT_2))),
                        hasProperty("datoFom", is(equalTo(SIVILSTAND_DATO.toLocalDate()))),
                        hasProperty("datoTom", is(equalTo(SIVILSTAND_DATO.toLocalDate().plusYears(1).minusDays(1)))),
                        hasProperty("registrertAv", is(equalTo("Dolly")))),
                allOf(hasProperty("pidBruker", is(equalTo(IDENT_2))),
                        hasProperty("pidSamboer", is(equalTo(IDENT_1))),
                        hasProperty("datoFom", is(equalTo(SIVILSTAND_DATO.toLocalDate()))),
                        hasProperty("datoTom", is(equalTo(SIVILSTAND_DATO.toLocalDate().plusYears(1).minusDays(1)))),
                        hasProperty("registrertAv", is(equalTo("Dolly"))))));
    }


    @Test
    void mapMultipleSivilstand_OK() {

        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", IDENT_1);
        var resultat = (List<PensjonSamboerRequest>) mapperFacade.map(PensjonSivilstandWrapper.builder()
                .sivilstander(buildMultipleSivilstand())
                .build(), List.class, context);

        assertThat(resultat, containsInAnyOrder(
                allOf(hasProperty("pidBruker", is(equalTo(IDENT_1))),
                        hasProperty("pidSamboer", is(equalTo(IDENT_4))),
                        hasProperty("datoFom", is(equalTo(SIVILSTAND_DATO_LATEST.toLocalDate()))),
                        hasProperty("datoTom", nullValue()),
                        hasProperty("registrertAv", is(equalTo("Dolly")))),
                allOf(hasProperty("pidBruker", is(equalTo(IDENT_4))),
                        hasProperty("pidSamboer", is(equalTo(IDENT_1))),
                        hasProperty("datoFom", is(equalTo(SIVILSTAND_DATO_LATEST.toLocalDate()))),
                        hasProperty("datoTom", nullValue()),
                        hasProperty("registrertAv", is(equalTo("Dolly"))))));
    }
}