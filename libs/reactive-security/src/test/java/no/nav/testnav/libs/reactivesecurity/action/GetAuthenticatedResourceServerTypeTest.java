package no.nav.testnav.libs.reactivesecurity.action;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.fail;

class GetAuthenticatedResourceServerTypeTest {

    @Test
    void test() {

        try {
            assertThat(
                    new GetAuthenticatedResourceServerType(List.of())
                            .call()
                            .block())
                    .isNull();
        } catch (RuntimeException e) {
            fail("Should not throw a RuntimeException");
        }

    }

}
