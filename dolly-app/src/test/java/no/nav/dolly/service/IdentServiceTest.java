package no.nav.dolly.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsTestident;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.repository.TestGruppeRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.testdata.builder.RsTestidentBuilder;
import no.nav.dolly.testdata.builder.TestidentBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class IdentServiceTest {

	@Mock
	IdentRepository identRepository;
	
	@Mock
    TestGruppeRepository testGruppeRepository;

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
		RsTestident rsi1 = RsTestidentBuilder.builder().ident("en").build().convertToRealRsTestident();
		RsTestident rsi2 = RsTestidentBuilder.builder().ident("to").build().convertToRealRsTestident();
		List<RsTestident> rsTestidenter = Arrays.asList(rsi1, rsi2);

		Testident i1 = TestidentBuilder.builder().ident("en").build().convertToRealTestident();
		Testident i2 = TestidentBuilder.builder().ident("to").build().convertToRealTestident();
		List<Testident> testidenter = Arrays.asList(i1, i2);

		when(mapperFacade.mapAsList(rsTestidenter, Testident.class)).thenReturn(testidenter);

		identService.persisterTestidenter(1L, rsTestidenter);

		verify(identRepository).saveAll(testidenter);
	}

	@Test(expected = ConstraintViolationException.class)
	public void persisterTestidenter_shouldThrowExceptionWhenADBConstraintIsBroken() {

		RsTestident rsi1 = RsTestidentBuilder.builder().ident("en").build().convertToRealRsTestident();
		RsTestident rsi2 = RsTestidentBuilder.builder().ident("to").build().convertToRealRsTestident();
		List<RsTestident> rsTestidenter = Arrays.asList(rsi1, rsi2);

		when(identRepository.saveAll(any())).thenThrow(DataIntegrityViolationException.class);

		identService.persisterTestidenter(1L, rsTestidenter);
	}
	
}