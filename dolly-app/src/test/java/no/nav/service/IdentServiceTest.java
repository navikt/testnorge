package no.nav.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import no.nav.exceptions.DollyFunctionalException;
import no.nav.jpa.Testgruppe;
import no.nav.jpa.Testident;
import no.nav.repository.GruppeRepository;
import no.nav.repository.IdentRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


@RunWith(MockitoJUnitRunner.class)
public class IdentServiceTest {
	Set<Long> testidenter = new HashSet<>(Arrays.asList(12312312312L, 22222222222L));
	
	@Mock
	IdentRepository identRepository;
	
	@Mock
	GruppeRepository gruppeRepository;
	
	@InjectMocks
	IdentService identService;
	
	Testgruppe testgruppe = new Testgruppe();
	
	@Before
	public void setup() {
		testgruppe.setId(1L);
	}
	
	@Test
	public void shouldPersistereTestidenterPaaTestgruppe() {
		when(gruppeRepository.findById(any())).thenReturn(testgruppe);
		
		identService.persisterTestidenter(1L, testidenter);
		for (Long ident:testidenter) {
			Mockito.verify(identRepository).save(eq(new Testident(ident, testgruppe)));
		}
	}
	
	@Test(expected = DollyFunctionalException.class)
	public void shouldThrowExceptionWhenTestgruppeDoesNotExist() {
		when(gruppeRepository.findById(any())).thenReturn(null);
		
		identService.persisterTestidenter(1L, testidenter);
	}
	
}