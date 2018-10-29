package no.nav.dolly.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsTestident;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.testdata.builder.RsTestidentBuilder;
import no.nav.dolly.testdata.builder.TestidentBuilder;

@RunWith(MockitoJUnitRunner.class)
public class IdentServiceTest {

    private static final String STANDARD_IDENTER_1 = "en";
    private static final String STANDAR_IDENTER_2 = "to";

    private Testgruppe standardGruppe = new Testgruppe();

    @Mock
    IdentRepository identRepository;

    @Mock
    MapperFacade mapperFacade;

    @InjectMocks
    IdentService identService;

    Testgruppe testgruppe = new Testgruppe();

    @Before
    public void setup() {
        testgruppe.setId(1L);
    }

    @Test
    public void persisterTestidenter_kallerSavePaaAlleTestidenter() {
        RsTestident rsi1 = RsTestidentBuilder.builder().ident(STANDARD_IDENTER_1).build().convertToRealRsTestident();
        RsTestident rsi2 = RsTestidentBuilder.builder().ident(STANDAR_IDENTER_2).build().convertToRealRsTestident();
        List<RsTestident> rsTestidenter = Arrays.asList(rsi1, rsi2);

        Testident i1 = TestidentBuilder.builder().ident(STANDARD_IDENTER_1).build().convertToRealTestident();
        Testident i2 = TestidentBuilder.builder().ident(STANDAR_IDENTER_2).build().convertToRealTestident();
        List<Testident> testidenter = Arrays.asList(i1, i2);

        when(mapperFacade.mapAsList(rsTestidenter, Testident.class)).thenReturn(testidenter);

        identService.persisterTestidenter(rsTestidenter);

        verify(identRepository).saveAll(testidenter);
    }

    @Test(expected = ConstraintViolationException.class)
    public void persisterTestidenter_shouldThrowExceptionWhenADBConstraintIsBroken() {

        RsTestident rsi1 = RsTestidentBuilder.builder().ident(STANDARD_IDENTER_1).build().convertToRealRsTestident();
        RsTestident rsi2 = RsTestidentBuilder.builder().ident(STANDAR_IDENTER_2).build().convertToRealRsTestident();
        List<RsTestident> rsTestidenter = Arrays.asList(rsi1, rsi2);

        when(identRepository.saveAll(any())).thenThrow(DataIntegrityViolationException.class);

        identService.persisterTestidenter(rsTestidenter);
    }

    @Test
    public void saveIdentTilGruppe_saveAvIdentInnholderInputIdentstringOgTestgruppe() {
        when(identRepository.save(any())).thenReturn(new Testident());

        identService.saveIdentTilGruppe(STANDARD_IDENTER_1, standardGruppe);

        ArgumentCaptor<Testident> cap = ArgumentCaptor.forClass(Testident.class);
        verify(identRepository).save(cap.capture());

        Testident testident = cap.getValue();

        assertThat(testident.getIdent(), is(STANDARD_IDENTER_1));
        assertThat(testident.getTestgruppe(), is(standardGruppe));
    }

    @Test
    public void slettTestidenter_kallesRiktigAntallGanger() {
        RsTestident rsi1 = RsTestidentBuilder.builder().ident(STANDARD_IDENTER_1).build().convertToRealRsTestident();
        RsTestident rsi2 = RsTestidentBuilder.builder().ident(STANDAR_IDENTER_2).build().convertToRealRsTestident();
        List<RsTestident> rsTestidenter = Arrays.asList(rsi1, rsi2);

        identService.slettTestidenter(rsTestidenter);

        verify(identRepository, times(2)).deleteTestidentByIdent(anyString());
    }
}