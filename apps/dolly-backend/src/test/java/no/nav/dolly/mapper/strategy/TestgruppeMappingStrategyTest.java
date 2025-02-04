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
import no.nav.dolly.libs.nais.DollySpringBootTest;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
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
        mapper = MapperTestUtils.createMapperFacadeForMappingStrategy(new TestgruppeMappingStrategy(new GetUserInfo("dummy")));
        MockedJwtAuthenticationTokenUtils.setJwtAuthenticationToken();
    }

    @Test
    void mappingFromTestgruppeToRsTestgruppe() {
        Bruker bruker = Bruker.builder().brukerId(BRUKERID).build();
        Testident testident = TestidentBuilder.builder().ident("1").build().convertToRealTestident();
        List<Testident> identer = singletonList(testident);

        Testgruppe testgruppe = Testgruppe.builder()
                .sistEndretAv(bruker)
                .datoEndret(LocalDate.of(2000, 1, 1))
                .opprettetAv(bruker)
                .id(2L)
                .testidenter(identer)
                .navn("gruppe")
                .build();

        testident.setTestgruppe(testgruppe);

        List<RsTestident> rsIdenter = mapper.mapAsList(identer, RsTestident.class);
        RsTestgruppe rs = mapper.map(testgruppe, RsTestgruppe.class);

        assertThat(rs.getNavn(), is("gruppe"));
        assertThat(rs.getDatoEndret().getYear(), is(2000));
        assertThat(rs.getDatoEndret().getMonthValue(), is(1));
        assertThat(rs.getDatoEndret().getDayOfMonth(), is(1));
        assertThat(rs.getOpprettetAv().getBrukerId(), is(bruker.getBrukerId()));
        assertThat(rs.getSistEndretAv().getBrukerId(), is(bruker.getBrukerId()));

        assertThat(rsIdenter.size(), is(1));
        assertThat(rsIdenter.get(0).getIdent(), is("1"));
    }
}