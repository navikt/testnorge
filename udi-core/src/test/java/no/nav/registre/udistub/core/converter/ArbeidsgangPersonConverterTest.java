package no.nav.registre.udistub.core.converter;

import static no.nav.registre.udistub.core.DefaultTestData.TEST_ARBEIDOMGANGKATEGORI;
import static no.nav.registre.udistub.core.DefaultTestData.TEST_ARBEIDSADGANG;
import static no.nav.registre.udistub.core.DefaultTestData.TEST_ARBEIDSADGANG_TYPE;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import no.nav.registre.udistub.core.converter.ws.ArbeidsadgangWsConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ArbeidsgangPersonConverterTest extends ConverterTestBase {

    @InjectMocks
    protected ArbeidsadgangWsConverter arbeidsadgangConverter;

    @Test
    public void convertFromPersonToArbeidsadgangIfPresent() {
        no.udi.mt_1067_nav_data.v1.Arbeidsadgang result = arbeidsadgangConverter.convert(defaultTestPerson);
        assertNotNull(result);
        Assertions.assertEquals(TEST_ARBEIDSADGANG, result.getHarArbeidsadgang());
        Assertions.assertEquals(TEST_ARBEIDOMGANGKATEGORI, result.getArbeidsOmfang());
        Assertions.assertEquals(TEST_ARBEIDSADGANG_TYPE, result.getTypeArbeidsadgang());
        assertNull(result.getArbeidsadgangsPeriode());
    }

    @Test
    public void convertFromPersonToArbeidsadgangIfAbsent() {
        no.udi.mt_1067_nav_data.v1.Arbeidsadgang result = arbeidsadgangConverter.convert(null);
        assertNull(result);
    }
}