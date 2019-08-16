package no.nav.registre.core.converter;

import static no.nav.registre.core.DefaultTestData.TEST_AVGJOERELSE_UAVKLART;
import static no.nav.registre.core.DefaultTestData.TEST_ER_POSITIV;
import static no.nav.registre.core.DefaultTestData.TEST_ETAT;
import static no.nav.registre.core.DefaultTestData.TEST_FLYKTNINGSTATUS;
import static no.nav.registre.core.DefaultTestData.TEST_ID;
import static no.nav.registre.core.DefaultTestData.TEST_KODEVERK_CODE;
import static no.nav.registre.core.DefaultTestData.TEST_OMGJORT_AVGJORELSE_ID;
import static no.nav.registre.core.DefaultTestData.TEST_SAKSNUMMER;
import static no.nav.registre.core.DefaultTestData.TEST_VARIGHET;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import no.udi.mt_1067_nav_data.v1.Avgjorelse;
import no.udi.mt_1067_nav_data.v1.Avgjorelser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AvgjorelsehistorikkPersonConverterTest extends ConverterTestBase {

	@InjectMocks
	protected AvgjorelsehistorikkConverter avgjorelsehistorikkConverter;

	@Test
	public void convertFromPersonToArbeidsadgangIfPresent() {
		Avgjorelser result = avgjorelsehistorikkConverter.convert(defaultTestPerson);
		assertNotNull(result);
		result.getAvgjorelseListe()
				.getAvgjorelse()
				.forEach(this::assertAvgjorelse);
	}

	private void assertAvgjorelse(Avgjorelse avgjorelse) {
		assertNotNull(avgjorelse);
		assertEquals(TEST_ID.toString(), avgjorelse.getAvgjorelseId());
		assertEquals(TEST_OMGJORT_AVGJORELSE_ID, avgjorelse.getOmgjortavAvgjorelseId());

		assertNotNull(avgjorelse.getAvgjorelsestype());
		assertEquals(TEST_KODEVERK_CODE, avgjorelse.getAvgjorelsestype().getGrunntypeKode());
		assertEquals(TEST_KODEVERK_CODE, avgjorelse.getAvgjorelsestype().getUtfallstypeKode());
		assertEquals(TEST_KODEVERK_CODE, avgjorelse.getAvgjorelsestype().getTillatelseKode());
		assertEquals(TEST_ER_POSITIV, avgjorelse.isErPositiv());

		assertNotNull(avgjorelse.getTillatelse());
		assertEquals(TEST_KODEVERK_CODE, avgjorelse.getTillatelse().getVarighetKode());
		assertEquals(TEST_VARIGHET, avgjorelse.getTillatelse().getVarighet());
		assertNull(avgjorelse.getTillatelse().getGyldighetsperiode());

		assertEquals(TEST_VARIGHET, avgjorelse.getUtfall().getVarighet());
		assertEquals(TEST_KODEVERK_CODE, avgjorelse.getUtfall().getVarighetKode());
		assertNull(avgjorelse.getUtfall().getGjeldendePeriode());

		assertNull(avgjorelse.getAvgjorelseDato());
		assertNull(avgjorelse.getEffektueringsDato());
		assertNull(avgjorelse.getIverksettelseDato());
		assertNull(avgjorelse.getUtreisefristDato());

		assertEquals(TEST_ETAT, avgjorelse.getEtat());
		assertEquals(TEST_SAKSNUMMER, avgjorelse.getSaksnummer());

		assertEquals(TEST_FLYKTNINGSTATUS, avgjorelse.isFlyktingstatus());
		assertEquals(TEST_AVGJOERELSE_UAVKLART, avgjorelse.isUavklartFlyktningstatus());

	}

	@Test
	public void convertFromPersonToArbeidsadgangIfAbsent() {
		Avgjorelser result = avgjorelsehistorikkConverter.convert(null);
		assertNull(result);
	}
}