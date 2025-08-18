package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.common.TestidentBuilder;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.entity.testident.RsTestident;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class TestidentMappingTest {

    private MapperFacade mapper;

    @MockitoBean
    private BrukerService brukerService;

    @BeforeEach
   void setUpHappyPath() {

        mapper = MapperTestUtils.createMapperFacadeForMappingStrategy(new TestgruppeMappingStrategy());
    }

    @Test
    void mapToRsTestidentIncludingTestgruppe() {

        var bruker = new Bruker();

        var testgruppe = Testgruppe.builder()
                .sistEndretAv(bruker)
                .datoEndret(LocalDate.of(2000, 1, 1))
                .opprettetAv(bruker)
                .id(2L)
                .navn("gruppe")
                .build();

        var testident = TestidentBuilder.builder().ident("1").testgruppe(testgruppe).build().convertToRealTestident();

        var rsTestident = mapper.map(testident, RsTestident.class);

        assertThat(rsTestident.getIdent(), is("1"));
    }
}
