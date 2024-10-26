package no.nav.testnav.levendearbeidsforholdansettelse;

import io.netty.handler.ssl.SslContextBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.FileInputStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@ActiveProfiles("test")
class LevendeArbeidsforholdAnsettelseApplicationTests {

    @MockBean
    @SuppressWarnings("unused")
    public ReactiveJwtDecoder jwtDecoder;

    @Autowired
    private R2dbcEntityTemplate template;

    @Test
    void load_app_context() {
        assertThat(template)
                .isNotNull();
    }

    @Disabled("Useful for checking the format of a SSL key file manually.")
    @Test
    @SuppressWarnings("java:S2699")
    void attemptToLoadSslKeyFile()
            throws Exception {
        var sslKey = "key.pk8";
        System.err.println("SSL Key File: " + sslKey);
        var file = new File(sslKey);
        System.out.println("File " + file.getAbsolutePath() + (file.exists() ? " exists" : " not found"));
        if (!file.exists()) {
            fail();
        } else {
            SslContextBuilder
                    .forClient()
                    .keyManager(null, new FileInputStream(file), null)
                    .build();
        }
    }

}
