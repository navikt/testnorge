package no.nav.dolly.mapper.strategy;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.assertj.core.util.Sets.newHashSet;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerTeamAndGruppeIDs;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.dolly.common.TestidentBuilder;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
        mapper = MapperTestUtils.createMapperFacadeForMappingStrategy(new BrukerMappingStrategy(), new TeamMappingStrategy());
    }

    @Test
    public void mapBruker() {

        Bruker bruker1 = Bruker.builder().navIdent("ident").build();
        Bruker bruker2 = Bruker.builder().navIdent("ident")
                .favoritter(newHashSet(singletonList(Testgruppe.builder()
                        .id(2L)
                        .testidenter(singleton(TestidentBuilder.builder().ident("1").build().convertToRealTestident()))
                        .teamtilhoerighet(Team.builder()
                                .navn("team")
                                .id(1L)
                                .medlemmer(singletonList(bruker1))
                                .beskrivelse("besk")
                                .build())
                        .build())))
                .teams(newHashSet(asList(Team.builder().id(1L).navn("test").build())))
                .build();

        RsBrukerTeamAndGruppeIDs rsBruker = mapper.map(bruker2, RsBrukerTeamAndGruppeIDs.class);

        assertThat(rsBruker.getNavIdent(), is("ident"));
        assertThat(rsBruker.getFavoritter().get(0), is("2"));
        assertThat(rsBruker.getTeams(), hasSize(1));
        assertThat(rsBruker.getTeams().get(0).getId(), is(equalTo(1L)));
        assertThat(rsBruker.getTeams().get(0).getNavn(), is(equalTo("test")));
    }
}