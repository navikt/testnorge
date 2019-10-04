package no.nav.dolly.mapper.strategy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.entity.testident.RsTestident;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.dolly.common.TestidentBuilder;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

public class TestidentMappingTest {

    private MapperFacade mapper;

    @Before
    public void setUpHappyPath() {
        mapper = MapperTestUtils.createMapperFacadeForMappingStrategy(new TestgruppeMappingStrategy(), new TeamMappingStrategy());
    }

    @Test
    public void mapToRsTestidentIncludingTestgruppe() {
        Bruker bruker = Bruker.builder().navIdent("ident").build();

        Testgruppe testgruppe = Testgruppe.builder()
                .sistEndretAv(bruker)
                .datoEndret(LocalDate.of(2000, 1, 1))
                .opprettetAv(bruker)
                .id(2L)
                .navn("gruppe")
                .build();

        Testident testident = TestidentBuilder.builder().ident("1").testgruppe(testgruppe).build().convertToRealTestident();

        RsTestident rsTestident = mapper.map(testident, RsTestident.class);

        assertThat(rsTestident.getIdent(), is("1"));
    }
}
