package no.nav.dolly.mapper.strategy;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.assertj.core.util.Sets.newHashSet;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.common.TestidentBuilder;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

public class BrukerMappingStrategyTest {

    private static final String CURRENT_BRUKER_IDENT = "NAV1";
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

    @Before
    public void setUpHappyPath() {
        mapper = MapperTestUtils.createMapperFacadeForMappingStrategy(new BrukerMappingStrategy());
    }

    @Test
    public void mapBruker() {

        Bruker bruker1 = Bruker.builder().brukerId("ident").build();
        Bruker bruker2 = Bruker.builder().brukerId("ident")
                .favoritter(newHashSet(singletonList(Testgruppe.builder()
                        .id(2L)
                        .testidenter(singleton(TestidentBuilder.builder().ident("1").build().convertToRealTestident()))
                        .build())))
                .build();

        RsBruker rsBruker = mapper.map(bruker2, RsBruker.class);

        assertThat(rsBruker.getNavIdent(), is("ident"));
        assertThat(rsBruker.getFavoritter().get(0), is("2"));
    }
}