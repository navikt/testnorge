package no.nav.dolly.mapper;

import static org.assertj.core.util.Lists.emptyList;
import static org.assertj.core.util.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;
import org.junit.Test;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsMeldingStatusIdent;

public class BestillingPdlForvalterStatusMpperTest {

    private static final String IDENT_1 = "111";
    private static final String IDENT_2 = "222";

    private static final String PDL_STATUS_OK = "FolkeregisterIdent&status: OK, "
            + "hendelsesId:a1bb529a-e29a-47ca-8dec-a0b6e89a2262$UtenlandskIdentifikasjonsnummer&status: OK, "
            + "hendelsesId:712d7e36-f83b-4415-a1c3-bf74b45c45a5";
    private static final String PDL_STATUS_NOK = "FolkeregisterIdent&status: Feil (404 Not Found - message: "
            + "Feil ved henting)"
            + "$UtenlandskIdentifikasjonsnummer&status: Feil (404 Not Found - message: "
            + "Feil ved henting)";

    @Test
    public void buildPdldataStatusMap_emptyList() {

        List<RsMeldingStatusIdent> resultat = BestillingPdlForvalterStatusMpper.buildPdldataStatusMap(newArrayList(BestillingProgress.builder().build()));

        assertThat(resultat, is(emptyList()));
    }

    @Test
    public void buildPdldataStatusMap_SingleStatus() {

        List<RsMeldingStatusIdent> resultat = BestillingPdlForvalterStatusMpper.buildPdldataStatusMap(
                newArrayList(BestillingProgress.builder()
                                .pdlforvalterStatus(PDL_STATUS_OK)
                                .ident(IDENT_1)
                                .build(),
                        BestillingProgress.builder()
                                .pdlforvalterStatus(PDL_STATUS_OK)
                                .ident(IDENT_2)
                                .build()
                ));

        assertThat(resultat, hasSize(2));
        assertThat(resultat.get(0).getMelding(), is(equalTo("FolkeregisterIdent")));
        assertThat(resultat.get(0).getStatusIdent().get("status: OK"), contains(IDENT_1, IDENT_2));
        assertThat(resultat.get(1).getMelding(), is(equalTo("UtenlandskIdentifikasjonsnummer")));
        assertThat(resultat.get(1).getStatusIdent().get("status: OK"), contains(IDENT_1, IDENT_2));
    }

    @Test
    public void buildPdldataStatusMap_MultipleStatus() {

        List<RsMeldingStatusIdent> resultat = BestillingPdlForvalterStatusMpper.buildPdldataStatusMap(
                newArrayList(BestillingProgress.builder()
                                .pdlforvalterStatus(PDL_STATUS_OK)
                                .ident(IDENT_1)
                                .build(),
                        BestillingProgress.builder()
                                .pdlforvalterStatus(PDL_STATUS_NOK)
                                .ident(IDENT_2)
                                .build()
                ));

        assertThat(resultat, hasSize(2));
        assertThat(resultat.get(0).getMelding(), is(equalTo("FolkeregisterIdent")));
        assertThat(resultat.get(0).getStatusIdent().get("status: OK"), contains(IDENT_1));
        assertThat(resultat.get(0).getMelding(), is(equalTo("FolkeregisterIdent")));
        assertThat(resultat.get(0).getStatusIdent().get("status: Feil (404 Not Found - message: Feil ved henting)"), contains(IDENT_2));
        assertThat(resultat.get(1).getMelding(), is(equalTo("UtenlandskIdentifikasjonsnummer")));
        assertThat(resultat.get(1).getStatusIdent().get("status: OK"), contains(IDENT_1));
        assertThat(resultat.get(1).getMelding(), is(equalTo("UtenlandskIdentifikasjonsnummer")));
        assertThat(resultat.get(1).getStatusIdent().get("status: Feil (404 Not Found - message: Feil ved henting)"), contains(IDENT_2));
    }
}