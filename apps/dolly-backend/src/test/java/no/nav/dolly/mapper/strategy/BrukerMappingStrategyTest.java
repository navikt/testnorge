package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
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
        var testgruppe1 = Testgruppe.builder().id(1L).build();
        var testgruppe2 = Testgruppe.builder().id(2L).build();

        var brukerfavoritter = List.of(testgruppe1, testgruppe2);
        var brukerInfo = new UserInfoExtended(bruker.getBrukerId(), "567", "hvem der?",
                bruker.getBrukernavn(), bruker.getEpost(),
                bruker.getBrukertype() == Bruker.Brukertype.BANKID, List.of("claim1", "claim2"));

        var context = MappingContextUtils.getMappingContext();
        context.setProperty("brukerInfo", brukerInfo);
        context.setProperty("favoritter", brukerfavoritter);

        var rsBruker = mapper.map(bruker, RsBruker.class, context);

        assertThat(rsBruker.getBrukerId(), is("123"));
        assertThat(rsBruker.getBrukernavn(), is("<brukernavn>"));
        assertThat(rsBruker.getEpost(), is("<email>"));
        assertThat(rsBruker.getBrukertype(), is(Bruker.Brukertype.AZURE));
        assertThat(rsBruker.getFavoritter().getFirst().getId(), is(testgruppe1.getId()));
        assertThat(rsBruker.getFavoritter().getLast().getId(), is(testgruppe2.getId()));
        assertThat(rsBruker.getGrupper(), containsInAnyOrder("claim1", "claim2"));
    }
}
