package no.nav.dolly.mapper;

import static org.assertj.core.util.Lists.emptyList;
import static org.assertj.core.util.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import org.junit.Test;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsPdlForvalterStatus;

public class BestillingPdlForvalterStatusMapperTest {

    private static final String IDENT_1 = "111";
    private static final String IDENT_2 = "222";

    private static final String PDL_STATUS_OK = "$UtenlandskIdentifikasjonsnummer&OK, "
            + "hendelsesId:712d7e36-f83b-4415-a1c3-bf74b45c45a5";
    private static final String PDL_STATUS_NOK = "$UtenlandskIdentifikasjonsnummer&Feil (404 Not Found - message: "
            + "Feil ved henting)";

    @Test
    public void buildPdldataStatusMap_emptyList() {

        RsPdlForvalterStatus resultat = BestillingPdlForvalterStatusMapper.buildPdldataStatusMap(newArrayList(BestillingProgress.builder().build()));

        assertThat(resultat, is(nullValue()));
    }

    @Test
    public void buildPdldataStatusMap_SingleStatus() {

        RsPdlForvalterStatus resultat = BestillingPdlForvalterStatusMapper.buildPdldataStatusMap(
                newArrayList(BestillingProgress.builder()
                                .pdlforvalterStatus(PDL_STATUS_OK)
                                .ident(IDENT_1)
                                .build(),
                        BestillingProgress.builder()
                                .pdlforvalterStatus(PDL_STATUS_OK)
                                .ident(IDENT_2)
                                .build()
                ));

        assertThat(resultat.getKontaktinfoDoedsbo(), is(emptyList()));
        assertThat(resultat.getUtenlandsid(), hasSize(1));
        assertThat(resultat.getUtenlandsid().get(0).getStatusMelding(), is(equalTo("OK")));
        assertThat(resultat.getUtenlandsid().get(0).getIdenter(), contains(IDENT_1, IDENT_2));
    }

    @Test
    public void buildPdldataStatusMap_MultipleStatus() {

        RsPdlForvalterStatus resultat = BestillingPdlForvalterStatusMapper.buildPdldataStatusMap(
                newArrayList(BestillingProgress.builder()
                                .pdlforvalterStatus(PDL_STATUS_OK)
                                .ident(IDENT_1)
                                .build(),
                        BestillingProgress.builder()
                                .pdlforvalterStatus(PDL_STATUS_NOK)
                                .ident(IDENT_2)
                                .build()
                ));

        assertThat(resultat.getKontaktinfoDoedsbo(), hasSize(0));
        assertThat(resultat.getUtenlandsid(), hasSize(2));
        assertThat(resultat.getUtenlandsid().get(1).getStatusMelding(), is(equalTo("OK")));
        assertThat(resultat.getUtenlandsid().get(1).getIdenter(), contains(IDENT_1));
        assertThat(resultat.getUtenlandsid().get(0).getStatusMelding(), is(equalTo("Feil (404 Not Found - message: Feil ved henting)")));
        assertThat(resultat.getUtenlandsid().get(0).getIdenter(), contains(IDENT_2));
    }
}