package no.nav.registre.ereg.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class orgnummerUtilTest {

    OrgnummerUtil orgnummerUtil;

    @Test
    public void should_calculate_correct_controlDigit () {

        String weights = "32765432";
        String randomString = "95416839";

        var calculatedControlDigit = orgnummerUtil.calculateControlDigit(weights, randomString);
        assertThat(calculatedControlDigit).isEqualTo(5);
    }
}
