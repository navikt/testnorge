package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.MockedJwtAuthenticationTokenUtils;
import no.nav.dolly.common.TestidentBuilder;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.domain.resultset.entity.testident.RsTestident;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.libs.reactivesecurity.action.GetUserInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DollySpringBootTest
class TestgruppeMappingStrategyTest {

    private static final String BRUKERID = "123";
    @MockitoBean
    private BestillingElasticRepository bestillingElasticRepository;
    @MockitoBean
    private ElasticsearchOperations elasticsearchOperations;
    private MapperFacade mapper;

    @BeforeEach
    public void setUpHappyPath() {
//        mapper = MapperTestUtils.createMapperFacadeForMappingStrategy(new TestgruppeMappingStrategy(new GetUserInfo("dummy")));
        MockedJwtAuthenticationTokenUtils.setJwtAuthenticationToken();
    }

    @Test
    void mappingFromTestgruppeToRsTestgruppe() {
        var bruker = Bruker.builder().brukerId(BRUKERID).build();
        var testident = TestidentBuilder.builder().ident("1").build().convertToRealTestident();
        var identer = singletonList(testident);

        var testgruppe = Testgruppe.builder()
                .sistEndretAv(bruker)
                .datoEndret(LocalDate.of(2000, 1, 1))
                .opprettetAv(bruker)
                .id(2L)
                .navn("gruppe")
                .build();

        testident.setGruppeId(testgruppe.getId());

        List<RsTestident> rsIdenter = mapper.mapAsList(identer, RsTestident.class);
        var rsTestgruppe = mapper.map(testgruppe, RsTestgruppe.class);

        assertThat(rsTestgruppe.getNavn(), is("gruppe"));
        assertThat(rsTestgruppe.getDatoEndret().getYear(), is(2000));
        assertThat(rsTestgruppe.getDatoEndret().getMonthValue(), is(1));
        assertThat(rsTestgruppe.getDatoEndret().getDayOfMonth(), is(1));

        assertThat(rsIdenter.size(), is(1));
        assertThat(rsIdenter.get(0).getIdent(), is("1"));
    }
}