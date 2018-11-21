package no.nav.dolly.mapper.stratergy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsTestident;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.dolly.testdata.builder.TestgruppeBuilder;
import no.nav.dolly.testdata.builder.TestidentBuilder;

public class TestidentMappingTest {

    private MapperFacade mapper;

    @Before
    public void setUpHappyPath() {
        mapper = MapperTestUtils.createMapperFacadeForMappingStrategy(new TestgruppeMappingStrategy(), new TeamMappingStrategy());
    }

    @Test
    public void mapToRsTestidentIncludingTestgruppe(){
        Bruker bruker = Bruker.builder().navIdent("ident").build();

        Testgruppe testgruppe = TestgruppeBuilder.builder()
                .sistEndretAv(bruker)
                .datoEndret(LocalDate.of(2000, 1, 1))
                .opprettetAv(bruker)
                .id(2L)
                .navn("gruppe")
                .build()
                .convertToRealTestgruppe();

        Testident testident = TestidentBuilder.builder().ident("1").testgruppe(testgruppe).build().convertToRealTestident();

        RsTestident rsTestident = mapper.map(testident, RsTestident.class);

        assertThat(rsTestident.getIdent(), is("1"));
    }
}
