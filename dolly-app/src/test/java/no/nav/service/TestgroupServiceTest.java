package no.nav.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

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
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TestgroupServiceTest {
	List<Long> testidenter = Arrays.asList(12312312312L, 22222222222L);
	@Mock
	IdentRepository identRepository;
	
	@Mock
	GruppeRepository gruppeRepository;
	
	@InjectMocks
	TestgroupService testgroupService;
	
	Testgruppe testgruppe = new Testgruppe();
	
	@Before
	public void setup() {
		testgruppe.setId(1L);
	}
	
	@Test
	public void shouldPersistereTestidenterPaaTestgruppe() {
		when(gruppeRepository.findById(any())).thenReturn(testgruppe);
		
		testgroupService.persisterTestidenter(1L, testidenter);
		Mockito.verify(identRepository).save(eq(new Testident(testidenter.get(0), testgruppe)));
		Mockito.verify(identRepository).save(eq(new Testident(testidenter.get(1), testgruppe)));
	}
	
	@Test(expected = RuntimeException.class)
	public void shouldThrowExceptionWhenTestgruppeDoesNotExist() {
		when(gruppeRepository.findById(any())).thenReturn(null);
		
		testgroupService.persisterTestidenter(1L, testidenter);
	}
}