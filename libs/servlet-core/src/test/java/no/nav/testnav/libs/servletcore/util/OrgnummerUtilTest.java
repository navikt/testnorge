package no.nav.testnav.libs.servletcore.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

class OrgnummerUtilTest {

    @Test
    void generated_orgnr_should_always_be_of_length_9() {
        var orgnummer = OrgnummerUtil.generateOrgnr();

        assertThat(orgnummer.length(), is(equalTo(9)));
    }

    @Test
    void should_calculate_correct_control_digit() {
        String randomString = "95416839";

        var calculatedControlDigit = OrgnummerUtil.calculateControlDigit(randomString);
        assertThat(calculatedControlDigit, is(equalTo(5)));
    }
}
