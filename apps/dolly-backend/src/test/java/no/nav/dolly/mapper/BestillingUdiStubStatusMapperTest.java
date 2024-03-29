package no.nav.dolly.mapper;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class BestillingUdiStubStatusMapperTest {

    private static final String IDENT1 = "11111111111";
    private static final String IDENT2 = "22222222222";
    private static final String ERROR_STATUS = "FEIL: something fishy: is going on";

    @Test
    void buildUdiStubStatusMap() {

        List<RsStatusRapport> result = BestillingUdiStubStatusMapper.buildUdiStubStatusMap(List.of(BestillingProgress.builder()
                        .ident(IDENT1)
                        .udistubStatus(ERROR_STATUS)
                        .build(),
                BestillingProgress.builder()
                        .ident(IDENT2)
                        .udistubStatus(ERROR_STATUS)
                        .build()));

        assertThat(result.get(0).getStatuser().get(0).getMelding(), is(equalTo(ERROR_STATUS)));
        assertThat(result.get(0).getStatuser().get(0).getIdenter(), contains(IDENT1, IDENT2));
    }
}