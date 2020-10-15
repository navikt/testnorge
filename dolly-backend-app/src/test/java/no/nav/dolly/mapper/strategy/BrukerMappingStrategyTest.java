package no.nav.dolly.mapper.strategy;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashSet;
import org.junit.Before;
import org.junit.Test;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.common.TestidentBuilder;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
import no.nav.dolly.mapper.utils.MapperTestUtils;

public class BrukerMappingStrategyTest {

    private MapperFacade mapper;

    @Before
    public void setUpHappyPath() {
        mapper = MapperTestUtils.createMapperFacadeForMappingStrategy(new BrukerMappingStrategy());
    }

    @Test
    public void mapBruker() {

        Bruker bruker = Bruker.builder().brukerId("ident")
                .favoritter(new HashSet<>(singletonList(Testgruppe.builder()
                        .id(2L)
                        .testidenter(singleton(TestidentBuilder.builder().ident("1").build().convertToRealTestident()))
                        .build())))
                .build();

        RsBruker rsBruker = mapper.map(bruker, RsBruker.class);

        assertThat(rsBruker.getBrukerId(), is("ident"));
        assertThat(rsBruker.getFavoritter().get(0).getId(), is(2L));
    }
}