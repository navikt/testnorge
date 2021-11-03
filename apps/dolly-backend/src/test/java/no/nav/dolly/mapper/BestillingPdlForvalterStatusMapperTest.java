package no.nav.dolly.mapper;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;

public class BestillingPdlForvalterStatusMapperTest {

    private static final String IDENT_1 = "111";
    private static final String IDENT_2 = "222";
    private static final String UTENLANDS_PDL = "PDL_UTENLANDSID";
    private static final String STATUS_OK = "OK";

    private static final String PDL_STATUS_OK = "$UtenlandskIdentifikasjonsnummer&OK, "
            + "hendelsesId:712d7e36-f83b-4415-a1c3-bf74b45c45a5";
    private static final String PDL_STATUS_NOK = "$UtenlandskIdentifikasjonsnummer&Feil: "
            + "Feil ved henting";

    @Test
    public void buildPdldataStatusMap_emptyList() {

        List<RsStatusRapport> resultat = BestillingPdlForvalterStatusMapper.buildPdlForvalterStatusMap(List.of(BestillingProgress.builder().build()), null);

        assertThat(resultat, is(empty()));
    }

    @Test
    public void buildPdldataStatusMap_UtenlandsId() {

        List<RsStatusRapport> resultat = BestillingPdlForvalterStatusMapper.buildPdlForvalterStatusMap(
                List.of(BestillingProgress.builder()
                                .pdlforvalterStatus(PDL_STATUS_OK)
                                .ident(IDENT_1)
                                .build(),
                        BestillingProgress.builder()
                                .pdlforvalterStatus(PDL_STATUS_OK)
                                .ident(IDENT_2)
                                .build()
                ), null);

        assertThat(resultat.get(0).getId().name(), is(equalTo(UTENLANDS_PDL)));
        assertThat(resultat.get(0).getStatuser().get(0).getMelding(), is(equalTo("OK")));
        assertThat(resultat.get(0).getStatuser().get(0).getIdenter(), containsInAnyOrder(IDENT_1, IDENT_2));
    }

    @Test
    public void buildPdldataStatusMap_MultipleStatus() {

        List<RsStatusRapport> resultat = BestillingPdlForvalterStatusMapper.buildPdlForvalterStatusMap(
                List.of(BestillingProgress.builder()
                                .pdlforvalterStatus(PDL_STATUS_OK)
                                .ident(IDENT_1)
                                .build(),
                        BestillingProgress.builder()
                                .pdlforvalterStatus(PDL_STATUS_NOK)
                                .ident(IDENT_2)
                                .build()
                ), null);

        assertThat(resultat.get(0).getId().name(), is(equalTo(UTENLANDS_PDL)));
        assertThat(resultat.get(0).getStatuser(), hasSize(2));
        assertThat(resultat.get(0).getStatuser().get(0).getMelding(), is(equalTo("Feil: Feil ved henting")));
        assertThat(resultat.get(0).getStatuser().get(0).getIdenter(), containsInAnyOrder(IDENT_2));
        assertThat(resultat.get(0).getStatuser().get(1).getMelding(), is(equalTo(STATUS_OK)));
        assertThat(resultat.get(0).getStatuser().get(1).getIdenter(), containsInAnyOrder(IDENT_1));
    }
}