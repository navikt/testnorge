package no.nav.udistub.converter;

import no.nav.udistub.converter.ws.ArbeidsadgangWsConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static no.nav.udistub.converter.DefaultTestData.TEST_ARBEIDOMGANGKATEGORI;
import static no.nav.udistub.converter.DefaultTestData.TEST_ARBEIDSADGANG;
import static no.nav.udistub.converter.DefaultTestData.TEST_ARBEIDSADGANG_TYPE;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class ArbeidsadgangPersonConverterTest extends ConverterTestBase {

    @InjectMocks
    protected ArbeidsadgangWsConverter arbeidsadgangConverter;

    @Test
    void convertFromPersonToArbeidsadgangIfPresent() {
        no.udi.mt_1067_nav_data.v1.Arbeidsadgang result = arbeidsadgangConverter.convert(defaultTestPerson.getArbeidsadgang());
        assertNotNull(result);
        Assertions.assertEquals(TEST_ARBEIDSADGANG, result.getHarArbeidsadgang());
        Assertions.assertEquals(TEST_ARBEIDOMGANGKATEGORI, result.getArbeidsOmfang());
        Assertions.assertEquals(TEST_ARBEIDSADGANG_TYPE, result.getTypeArbeidsadgang());
    }

    @Test
    void convertFromPersonToArbeidsadgangIfAbsent() {
        no.udi.mt_1067_nav_data.v1.Arbeidsadgang result = arbeidsadgangConverter.convert(null);
        assertNull(result);
    }
}