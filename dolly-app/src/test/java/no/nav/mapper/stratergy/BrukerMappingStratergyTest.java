package no.nav.mapper.stratergy;

import ma.glasnost.orika.MapperFacade;
import no.nav.api.resultSet.RsBruker;
import no.nav.jpa.Bruker;
import no.nav.mapper.utils.MapperTestUtils;
import no.nav.testdata.BrukerBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class BrukerMappingStratergyTest {

    private MapperFacade mapper;

    @Before
    public void setUpHappyPath() {
        mapper = MapperTestUtils.createMapperFacadeForMappingStrategy(new BrukerMappingStratergy());
    }

    @Test
    public void testy(){
        BrukerBuilder pojo = BrukerBuilder.builder().navIdent("ident").build();
        Bruker bruker = pojo.convertToRealBruker();

        RsBruker rs = mapper.map(bruker, RsBruker.class);

        assertThat(bruker.getNavIdent(), is("ident"));
        assertThat(rs.getNavIdent(), is("ident"));
    }
}