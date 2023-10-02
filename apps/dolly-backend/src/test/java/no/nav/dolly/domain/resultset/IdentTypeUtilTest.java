package no.nav.dolly.domain.resultset;

import no.nav.dolly.util.IdentTypeUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
class IdentTypeUtilTest {

    private static final String FNR_IDENT = "10101020312";
    private static final String DNR_IDENT = "40101020312";
    private static final String NPID_IDENT = "10301020312";

    @Test
    void getIdentTypeFNR_OK() {

        assertThat(IdentTypeUtil.getIdentType(FNR_IDENT), is(equalTo(IdentType.FNR)));
    }

    @Test
    void getIdentTypeDNR_OK() {

        assertThat(IdentTypeUtil.getIdentType(DNR_IDENT), is(equalTo(IdentType.DNR)));
    }

    @Test
    void getIdentTypeBOST_OK() {

        assertThat(IdentTypeUtil.getIdentType(NPID_IDENT), is(equalTo(IdentType.NPID)));
    }
}