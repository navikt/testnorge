package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.common.TestidentBuilder;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.domain.resultset.entity.testident.RsTestident;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import org.apache.http.entity.ContentType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestgruppeMappingStrategyTest {

    private final static String BRUKERID = "123";
    private final static String BRUKERNAVN = "BRUKER";
    private final static String EPOST = "@@@@";

    private MapperFacade mapper;

    @BeforeClass
    public static void setup() {
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(Jwt.withTokenValue("test")
                .claim("oid", BRUKERID)
                .claim("name", BRUKERNAVN)
                .claim("epost", EPOST)
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON)
                .build()));
    }

    @Before
    public void setUpHappyPath() {
        mapper = MapperTestUtils.createMapperFacadeForMappingStrategy(new TestgruppeMappingStrategy());
    }

    @Test
    public void mappingFromTestgruppeToRsTestgruppe() {
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