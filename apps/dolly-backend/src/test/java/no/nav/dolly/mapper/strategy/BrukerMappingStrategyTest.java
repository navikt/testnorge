package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.common.TestidentBuilder;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.dolly.service.BrukerService;
import no.nav.dolly.util.CurrentAuthentication;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class BrukerMappingStrategyTest {

    private MapperFacade mapper;

    @Mock
    private GetUserInfo getUserInfo;

    @Mock
    private BrukerService brukerService;

    @BeforeEach
    public void setUpHappyPath() {
        mapper = MapperTestUtils.createMapperFacadeForMappingStrategy(new BrukerMappingStrategy(getUserInfo, brukerService));
    }

    @Test
    void mapBruker() {

        try (MockedStatic<CurrentAuthentication> mocked = Mockito.mockStatic(CurrentAuthentication.class)) {
            Bruker mockBruker = new Bruker();

            Bruker bruker = Bruker.builder().brukerId("ident")
                    .favoritter(new HashSet<>(singletonList(Testgruppe.builder()
                            .id(2L)
                            .testidenter(List.of(TestidentBuilder.builder().ident("1").build().convertToRealTestident()))
                            .build())))
                    .build();

            mocked.when(() -> CurrentAuthentication.getAuthUser(any(GetUserInfo.class))).thenReturn(mockBruker);
            mocked.when(() -> brukerService.fetchBrukerOrTeamBruker(any())).thenReturn(bruker);

            RsBruker rsBruker = mapper.map(bruker, RsBruker.class);

            assertThat(rsBruker.getBrukerId(), is("ident"));
            assertThat(rsBruker.getFavoritter().getFirst().getId(), is(2L));
        }
    }

    @Test
    void mapBrukerWithAllFields() {
        try (var mocked = Mockito.mockStatic(CurrentAuthentication.class)) {
            var mockBruker = new Bruker();

            var bruker = Bruker.builder()
                    .brukerId("ident123")
                    .epost("test@nav.no")
                    .brukertype(Bruker.Brukertype.AZURE)
                    .favoritter(new HashSet<>(singletonList(
                            Testgruppe.builder()
                                    .id(2L)
                                    .navn("Test Gruppe")
                                    .hensikt("Testing")
                                    .testidenter(List.of(TestidentBuilder.builder()
                                            .ident("1")
                                            .build()
                                            .convertToRealTestident()))
                                    .build())))
                    .build();

            mocked.when(() -> CurrentAuthentication.getAuthUser(any(GetUserInfo.class))).thenReturn(mockBruker);
            mocked.when(() -> brukerService.fetchBrukerOrTeamBruker(any())).thenReturn(bruker);

            var rsBruker = mapper.map(bruker, RsBruker.class);

            assertThat(rsBruker.getBrukerId(), is("ident123"));
            assertThat(rsBruker.getEpost(), is("test@nav.no"));
            assertThat(rsBruker.getBrukertype(), is(Bruker.Brukertype.AZURE));

            assertThat(rsBruker.getFavoritter().size(), is(1));
            assertThat(rsBruker.getFavoritter().getFirst().getId(), is(2L));
            assertThat(rsBruker.getFavoritter().getFirst().getNavn(), is("Test Gruppe"));
            assertThat(rsBruker.getFavoritter().getFirst().getHensikt(), is("Testing"));
        }
    }
}
