package no.nav.registre.udistub.core.converter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import no.nav.registre.udistub.core.DefaultTestData;
import no.nav.registre.udistub.core.converter.ws.AvgjorelsehistorikkWsConverter;
import no.udi.mt_1067_nav_data.v1.Avgjorelse;
import no.udi.mt_1067_nav_data.v1.Avgjorelser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AvgjorelsehistorikkPersonConverterTest extends ConverterTestBase {

    @InjectMocks
    protected AvgjorelsehistorikkWsConverter avgjorelsehistorikkConverter;

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
        Assertions.assertEquals(DefaultTestData.TEST_OMGJORT_AVGJORELSE_ID, avgjorelse.getOmgjortavAvgjorelseId());

        assertNotNull(avgjorelse.getAvgjorelsestype());
        Assertions.assertEquals(DefaultTestData.TEST_KODEVERK_CODE, avgjorelse.getAvgjorelsestype().getGrunntypeKode());
        Assertions.assertEquals(DefaultTestData.TEST_KODEVERK_CODE, avgjorelse.getAvgjorelsestype().getUtfallstypeKode());
        Assertions.assertEquals(DefaultTestData.TEST_KODEVERK_CODE, avgjorelse.getAvgjorelsestype().getTillatelseKode());
        Assertions.assertEquals(DefaultTestData.TEST_ER_POSITIV, avgjorelse.isErPositiv());

        assertNotNull(avgjorelse.getTillatelse());
        Assertions.assertEquals(DefaultTestData.TEST_KODEVERK_CODE, avgjorelse.getTillatelse().getVarighetKode());
        Assertions.assertEquals(DefaultTestData.TEST_VARIGHET, avgjorelse.getTillatelse().getVarighet());
        assertNull(avgjorelse.getTillatelse().getGyldighetsperiode());

        Assertions.assertEquals(DefaultTestData.TEST_VARIGHET, avgjorelse.getUtfall().getVarighet());
        Assertions.assertEquals(DefaultTestData.TEST_KODEVERK_CODE, avgjorelse.getUtfall().getVarighetKode());
        assertNull(avgjorelse.getUtfall().getGjeldendePeriode());

        assertNull(avgjorelse.getAvgjorelseDato());
        assertNull(avgjorelse.getEffektueringsDato());
        assertNull(avgjorelse.getIverksettelseDato());
        assertNull(avgjorelse.getUtreisefristDato());

        Assertions.assertEquals(DefaultTestData.TEST_ETAT, avgjorelse.getEtat());
        Assertions.assertEquals(DefaultTestData.TEST_SAKSNUMMER, avgjorelse.getSaksnummer());

        Assertions.assertEquals(DefaultTestData.TEST_FLYKTNINGSTATUS, avgjorelse.isFlyktingstatus());
        Assertions.assertEquals(DefaultTestData.TEST_AVGJOERELSE_UAVKLART, avgjorelse.isUavklartFlyktningstatus());

    }

    @Test
    public void convertFromPersonToArbeidsadgangIfAbsent() {
        Avgjorelser result = avgjorelsehistorikkConverter.convert(null);
        assertNull(result);
    }
}