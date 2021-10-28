package no.nav.dolly.domain.resultset;

import no.nav.dolly.util.IdentTypeUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
public class IdentTypeUtilTest {

    private static final String FNR_IDENT = "10101020312";
    private static final String DNR_IDENT = "40101020312";
    private static final String BOST_IDENT = "10301020312";
    private static final String FDAT_IDENT = "10101000000";

    @Test
    public void getIdentTypeFNR_OK() {

        assertThat(IdentTypeUtil.getIdentType(FNR_IDENT), is(equalTo(IdentType.FNR)));
    }

    @Test
    public void getIdentTypeDNR_OK() {

        assertThat(IdentTypeUtil.getIdentType(DNR_IDENT), is(equalTo(IdentType.DNR)));
    }

    @Test
    public void getIdentTypeBOST_OK() {

        assertThat(IdentTypeUtil.getIdentType(BOST_IDENT), is(equalTo(IdentType.BOST)));
    }

    @Test
    public void getIdentTypeFDAT_OK() {

        assertThat(IdentTypeUtil.getIdentType(FDAT_IDENT), is(equalTo(IdentType.FDAT)));
    }
}