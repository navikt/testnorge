package no.nav.dolly.bestilling.skattekort.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.skattekort.domain.Forskuddstrekk;
import no.nav.dolly.bestilling.skattekort.domain.Resultatstatus;
import no.nav.dolly.bestilling.skattekort.domain.Skattekort;
import no.nav.dolly.bestilling.skattekort.domain.Skattekortmelding;
import no.nav.dolly.bestilling.skattekort.domain.SokosSkattekortRequest;
import no.nav.dolly.bestilling.skattekort.domain.Tilleggsopplysning;
import no.nav.dolly.bestilling.skattekort.domain.Trekkode;
import no.nav.dolly.bestilling.skattekort.domain.Trekktabell;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SkattekortMappingStrategyTest {

    private static final String IDENT = "01010112345";
    private static final Integer INNTEKTSAAR = 2026;

    private MapperFacade mapperFacade;
    private ObjectMapper objectMapper;

    private Skattekortmelding createSkattekortmeldingWithComplexForskuddstrekk() {
        return Skattekortmelding.builder()
                .inntektsaar(INNTEKTSAAR)
                .resultatPaaForespoersel(Resultatstatus.SKATTEKORTOPPLYSNINGER_OK)
                .skattekort(Skattekort.builder()
                        .skattekortidentifikator(998704L)
                        .forskuddstrekk(List.of(
                                Forskuddstrekk.builder()
                                        .trekktabell(Trekktabell.builder()
                                                .trekkode(Trekkode.LOENN_FRA_NAV)
                                                .tabellnummer("8020")
                                                .prosentsats(40)
                                                .antallMaanederForTrekk(12)
                                                .build())
                                        .build()
                        ))
                        .build())
                .tilleggsopplysning(List.of(Tilleggsopplysning.OPPHOLD_PAA_SVALBARD))
                .build();
    }

    private MappingContext createMappingContext(String ident) {
        var context = MappingContextUtils.getMappingContext();
        context.setProperty("ident", ident);
        return context;
    }

    @Nested
    @DisplayName("Skattekortmelding to SokosSkattekortRequest mapping with complex frontend structure")
    class SokosSkattekortRequestMappingTest {

        @Test
        void shouldMapComplexFrontendStructureToSokosFormat() {
            var source = createSkattekortmeldingWithComplexForskuddstrekk();
            var context = createMappingContext(IDENT);

            var result = mapperFacade.map(source, SokosSkattekortRequest.class, context);

            assertThat(result.getFnr()).isEqualTo(IDENT);
            assertThat(result.getSkattekort()).isNotNull();
            assertThat(result.getSkattekort().getInntektsaar()).isEqualTo(INNTEKTSAAR);
            assertThat(result.getSkattekort().getForskuddstrekkList()).isNotNull();
        }

        @Test
        void shouldMapForskuddstrekkWithTrekktabel() {
            var source = createSkattekortmeldingWithComplexForskuddstrekk();
            var context = createMappingContext(IDENT);

            var result = mapperFacade.map(source, SokosSkattekortRequest.class, context);

            assertThat(result.getSkattekort().getForskuddstrekkList())
                    .isNotEmpty()
                    .as("Forskuddstrekk list should contain the trekktabell data");
        }

        @Test
        void shouldSerializeComplexStructureToValidJson() throws Exception {
            var source = createSkattekortmeldingWithComplexForskuddstrekk();
            var context = createMappingContext(IDENT);

            var mapped = mapperFacade.map(source, SokosSkattekortRequest.class, context);
            String json = objectMapper.writeValueAsString(mapped);

            assertThat(json)
                    .as("JSON must contain fnr field")
                    .contains("\"fnr\":\"" + IDENT + "\"");
            assertThat(json)
                    .as("JSON must contain inntektsaar field")
                    .contains("\"inntektsaar\":" + INNTEKTSAAR);
            assertThat(json)
                    .as("JSON must contain resultatForSkattekort enum value")
                    .contains("\"resultatForSkattekort\":\"skattekortopplysningerOK\"");
            assertThat(json)
                    .as("JSON must contain skattekort object")
                    .contains("\"skattekort\"");
            assertThat(json)
                    .as("JSON must contain forskuddstrekkList array")
                    .contains("\"forskuddstrekkList\"");
            assertThat(json)
                    .as("JSON must contain flat tabell field (not tabellnummer)")
                    .contains("\"tabell\":\"8020\"");
            assertThat(json)
                    .as("JSON must contain flat trekkode field")
                    .contains("\"trekkode\":\"loennFraNAV\"");
            assertThat(json)
                    .as("JSON must contain flat prosentSats field (not prosentsats)")
                    .contains("\"prosentSats\"");
            assertThat(json)
                    .as("JSON must contain flat antallMndForTrekk field (not antallMaanederForTrekk)")
                    .contains("\"antallMndForTrekk\"");
        }

        @Test
        void shouldDeserializeComplexJsonFromMapping() throws Exception {
            var source = createSkattekortmeldingWithComplexForskuddstrekk();
            var context = createMappingContext(IDENT);

            var mapped = mapperFacade.map(source, SokosSkattekortRequest.class, context);
            String json = objectMapper.writeValueAsString(mapped);
            SokosSkattekortRequest deserialized = objectMapper.readValue(json, SokosSkattekortRequest.class);

            assertThat(deserialized.getFnr()).isEqualTo(IDENT);
            assertThat(deserialized.getSkattekort().getInntektsaar()).isEqualTo(INNTEKTSAAR);
            assertThat(deserialized.getSkattekort().getResultatForSkattekort())
                    .isEqualTo("skattekortopplysningerOK");
        }

        @Test
        void shouldIncludeAllRequiredFieldsForSokosCompatibility() throws Exception {
            var source = createSkattekortmeldingWithComplexForskuddstrekk();
            var context = createMappingContext(IDENT);

            var mapped = mapperFacade.map(source, SokosSkattekortRequest.class, context);
            String json = objectMapper.writeValueAsString(mapped);

            System.out.println("Generated JSON for Sokos API:");
            System.out.println(json);

            assertThat(json)
                    .as("JSON must contain fnr field required by Sokos API")
                    .contains("\"fnr\"");
            assertThat(json)
                    .as("JSON must contain skattekort object required by Sokos API")
                    .contains("\"skattekort\"");
            assertThat(json)
                    .as("JSON must contain inntektsaar field inside skattekort")
                    .contains("\"inntektsaar\":" + INNTEKTSAAR);
            assertThat(json)
                    .as("JSON must contain resultatForSkattekort as enum value")
                    .contains("\"resultatForSkattekort\":\"skattekortopplysningerOK\"");
            assertThat(json)
                    .as("JSON must contain tilleggsopplysningList with correct enum values")
                    .contains("\"tilleggsopplysningList\":[\"oppholdPaaSvalbard\"]");

            SokosSkattekortRequest deserialized = objectMapper.readValue(json, SokosSkattekortRequest.class);
            assertThat(deserialized.getSkattekort().getInntektsaar())
                    .as("Must deserialize inntektsaar correctly from JSON")
                    .isEqualTo(INNTEKTSAAR);
            assertThat(deserialized.getFnr())
                    .as("Must deserialize fnr correctly from JSON")
                    .isEqualTo(IDENT);
            assertThat(deserialized.getSkattekort().getTilleggsopplysningList())
                    .as("Must deserialize tilleggsopplysningList correctly")
                    .isNotNull()
                    .hasSize(1);
        }
    }

    @BeforeEach
    void setUp() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new SkattekortMappingStrategy());
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }
}
