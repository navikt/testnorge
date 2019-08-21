package no.nav.registre.core.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import no.udi.mt_1067_nav_data.v1.JaNeiUavklart;
import no.udi.mt_1067_nav_data.v1.SoknadOmBeskyttelseUnderBehandling;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BeskyttleseUnderBehandlingPersonConverterTest extends ConverterTestBase {

	@InjectMocks
	protected BeskyttleseUnderBehandlingConverter beskyttleseUnderBehandlingConverter;

	@Test
	public void convertFromPersonToSoknadOmBeskyttelseUnderBehandlingIfPresent() {
		SoknadOmBeskyttelseUnderBehandling result = beskyttleseUnderBehandlingConverter.convert(defaultTestPerson);
		assertNotNull(result);
		assertEquals(JaNeiUavklart.JA, result.getErUnderBehandling());
		assertNull(result.getSoknadsdato());
	}

	@Test
	public void convertFromPersonToSoknadOmBeskyttelseUnderBehandlingIfAbsent() {
		SoknadOmBeskyttelseUnderBehandling result = beskyttleseUnderBehandlingConverter.convert(null);
		assertNull(result);
	}
}
