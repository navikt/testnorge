package no.nav.testnav.levendearbeidsforholdansettelse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

//@SpringBootTest
class LevendeArbeidsforholdAnsettelseApplicationTests {

    @MockBean
    public ReactiveJwtDecoder jwtDecoder;

    @Test
    @SuppressWarnings("java:S2699")
    void load_app_context() {
    }

}
