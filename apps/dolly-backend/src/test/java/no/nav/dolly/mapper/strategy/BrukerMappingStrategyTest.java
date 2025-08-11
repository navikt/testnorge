package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.BrukerFavoritter;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerAndClaims;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.testnav.libs.securitycore.domain.UserInfoExtended;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

class BrukerMappingStrategyTest {

    private MapperFacade mapper;

    @BeforeEach
    void setup() {

        mapper = MapperTestUtils.createMapperFacadeForMappingStrategy(new BrukerMappingStrategy());
    }

    @Test
    void mapBruker() {

        var bruker = Bruker.builder()
                .brukerId("123")
                .brukernavn("<brukernavn>")
                .epost("<email>")
                .brukertype(Bruker.Brukertype.AZURE)
                .build();

        var brukerfavoritter = List.of(BrukerFavoritter.builder().gruppeId(2L).build(),
                BrukerFavoritter.builder().gruppeId(3L).build());
        var brukerInfo = new UserInfoExtended(bruker.getBrukerId(), "567", "hvem der?",
                bruker.getBrukernavn(), bruker.getEpost(),
                bruker.getBrukertype() == Bruker.Brukertype.BANKID, List.of("claim1", "claim2"));

        var context = MappingContextUtils.getMappingContext();
        context.setProperty("brukerInfo", brukerInfo);
        context.setProperty("favoritter", brukerfavoritter);

        var rsBruker = mapper.map(bruker, RsBrukerAndClaims.class, context);

        assertThat(rsBruker.getBrukerId(), is("123"));
        assertThat(rsBruker.getBrukernavn(), is("<brukernavn>"));
        assertThat(rsBruker.getEpost(), is("<email>"));
        assertThat(rsBruker.getBrukertype(), is(Bruker.Brukertype.AZURE));
        assertThat(rsBruker.getFavoritter(), containsInAnyOrder("2", "3"));
        assertThat(rsBruker.getGrupper(), containsInAnyOrder("claim1", "claim2"));
    }
}
