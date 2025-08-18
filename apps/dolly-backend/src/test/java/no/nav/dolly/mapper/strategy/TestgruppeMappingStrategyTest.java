package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.dolly.service.BrukerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


class TestgruppeMappingStrategyTest {

    private static final String BRUKERID = "123";

    @MockitoBean
    private BestillingElasticRepository bestillingElasticRepository;

    @MockitoBean
    private BrukerService brukerService;
    @MockitoBean
    private ElasticsearchOperations elasticsearchOperations;

    private MapperFacade mapper;

    @BeforeEach
    void setup() {

        mapper = MapperTestUtils.createMapperFacadeForMappingStrategy(new TestgruppeMappingStrategy());
    }

    @Test
    void mappingFromTestgruppeToRsTestgruppe() {

        var bruker = Bruker.builder().id(1L).brukerId(BRUKERID).build();

        var testgruppe = Testgruppe.builder()
                .sistEndretAv(bruker)
                .datoEndret(LocalDate.of(2000, 1, 1))
                .opprettetAv(bruker)
                .id(2L)
                .navn("gruppe")
                .build();

        var context = MappingContextUtils.getMappingContext();
        context.setProperty("bruker", bruker);
        context.setProperty("antallIdenter", 1);
        context.setProperty("antallBestillinger", 1);
        context.setProperty("antallIBruk", 1);

        var rsTestgruppe = mapper.map(testgruppe, RsTestgruppe.class, context);

        assertThat(rsTestgruppe.getNavn(), is("gruppe"));
        assertThat(rsTestgruppe.getDatoEndret().getYear(), is(2000));
        assertThat(rsTestgruppe.getDatoEndret().getMonthValue(), is(1));
        assertThat(rsTestgruppe.getDatoEndret().getDayOfMonth(), is(1));
        assertThat(rsTestgruppe.getAntallIdenter(), is(1));
        assertThat(rsTestgruppe.getAntallBestillinger(), is(1));
        assertThat(rsTestgruppe.getAntallIBruk(), is(1));
    }
}