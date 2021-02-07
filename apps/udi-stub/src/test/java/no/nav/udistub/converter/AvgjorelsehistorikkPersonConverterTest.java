package no.nav.udistub.converter;

import no.nav.udistub.converter.ws.AvgjorelsehistorikkWsConverter;
import no.udi.mt_1067_nav_data.v1.Avgjorelse;
import no.udi.mt_1067_nav_data.v1.Avgjorelser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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

        assertNotNull(avgjorelse.getAvgjorelsestype());
        Assertions.assertEquals(DefaultTestData.TEST_KODEVERK_CODE.getKode(), avgjorelse.getAvgjorelsestype().getGrunntypeKode().getKode());
        Assertions.assertEquals(DefaultTestData.TEST_KODEVERK_CODE.getKode(), avgjorelse.getAvgjorelsestype().getUtfallstypeKode().getKode());
        Assertions.assertEquals(DefaultTestData.TEST_KODEVERK_CODE.getKode(), avgjorelse.getAvgjorelsestype().getTillatelseKode().getKode());
        Assertions.assertEquals(DefaultTestData.TEST_ER_POSITIV, avgjorelse.isErPositiv());

        assertNotNull(avgjorelse.getTillatelse());
        Assertions.assertEquals(DefaultTestData.TEST_KODEVERK_CODE.getKode(), avgjorelse.getTillatelse().getVarighetKode().getKode());
        Assertions.assertEquals(DefaultTestData.TEST_VARIGHET, avgjorelse.getTillatelse().getVarighet());
        assertNull(avgjorelse.getTillatelse().getGyldighetsperiode());

        Assertions.assertEquals(DefaultTestData.TEST_VARIGHET, avgjorelse.getUtfall().getVarighet());
        Assertions.assertEquals(DefaultTestData.TEST_KODEVERK_CODE.getKode(), avgjorelse.getUtfall().getVarighetKode().getKode());
        assertNull(avgjorelse.getUtfall().getGjeldendePeriode());

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