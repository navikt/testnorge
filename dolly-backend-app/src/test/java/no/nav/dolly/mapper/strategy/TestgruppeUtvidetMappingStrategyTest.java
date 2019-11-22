package no.nav.dolly.mapper.strategy;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import java.util.Arrays;
import org.assertj.core.util.Sets;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppeUtvidet;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

@RunWith(MockitoJUnitRunner.class)
public class TestgruppeUtvidetMappingStrategyTest {

    private static final String CURRENT_BRUKER_IDENT = "1";
    private static final String IDENT1 = "1";
    private static final String IDENT2 = "2";
    private static final Testident TESTIDENT1 = Testident.builder().ident(IDENT1).build();
    private static final Testident TESTIDENT2 = Testident.builder().ident(IDENT2).build();
    private static Authentication authentication;
    private MapperFacade mapper;

    @BeforeClass
    public static void beforeClass() {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(
                new OidcTokenAuthentication(CURRENT_BRUKER_IDENT, null, null, null));
    }

    @AfterClass
    public static void afterClass() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private static Testgruppe buildTestgruppe() {
        return Testgruppe.builder()
                .testidenter(Sets.newHashSet(Arrays.asList(TESTIDENT1, TESTIDENT2)))
                .opprettetAv(Bruker.builder().brukerId(IDENT1).build())
                .sistEndretAv(Bruker.builder().brukerId(IDENT2).build())
                .build();
    }

    @Before
    public void setUpHappyPath() {
        mapper = MapperTestUtils.createMapperFacadeForMappingStrategy(new TestgruppeUtvidetMappingStrategy(), new TestgruppeMappingStrategy());
    }

    @Test
    public void mapTestgruppe2UtvvidetTestgruppe() {

        RsTestgruppeUtvidet testgruppeUtvidet = mapper.map(buildTestgruppe(), RsTestgruppeUtvidet.class);

        assertThat(testgruppeUtvidet.getOpprettetAvNavIdent(), is(equalTo(IDENT1)));
        assertThat(testgruppeUtvidet.getSistEndretAvNavIdent(), is(equalTo(IDENT2)));
        assertThat(testgruppeUtvidet.getTestidenter(), hasSize(2));
        assertThat(testgruppeUtvidet.getAntallIdenter(), is(equalTo(2)));
    }
}