package no.nav.dolly.mapper;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import org.junit.jupiter.api.Test;

import java.util.List;

import static no.nav.dolly.domain.resultset.SystemTyper.INST2;
import static no.nav.dolly.domain.resultset.SystemTyper.INST_KDI;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

class BestillingInstdataStatusMapperTest {

    private static final String IDENT_1 = "12345678901";
    private static final String IDENT_2 = "98765432109";

    @Test
    void shouldReturnEmptyListForEmptyProgressList() {
        var result = BestillingInstdataStatusMapper.buildInstdataStatusMap(List.of());

        assertThat(result, is(empty()));
    }

    @Test
    void shouldReturnEmptyListWhenInstdataStatusIsNull() {
        var progress = BestillingProgress.builder()
                .ident(IDENT_1)
                .instdataStatus(null)
                .build();

        var result = BestillingInstdataStatusMapper.buildInstdataStatusMap(List.of(progress));

        assertThat(result, is(empty()));
    }

    @Test
    void shouldReturnEmptyListWhenInstdataStatusIsBlank() {
        var progress = BestillingProgress.builder()
                .ident(IDENT_1)
                .instdataStatus("   ")
                .build();

        var result = BestillingInstdataStatusMapper.buildInstdataStatusMap(List.of(progress));

        assertThat(result, is(empty()));
    }

    @Test
    void shouldReturnEmptyListWhenIdentIsBlank() {
        var progress = BestillingProgress.builder()
                .ident("")
                .instdataStatus("q1:OK")
                .build();

        var result = BestillingInstdataStatusMapper.buildInstdataStatusMap(List.of(progress));

        assertThat(result, is(empty()));
    }

    @Test
    void shouldDefaultToInst2WhenNoHashSeparator() {
        var progress = BestillingProgress.builder()
                .ident(IDENT_1)
                .instdataStatus("q1:OK")
                .build();

        var result = BestillingInstdataStatusMapper.buildInstdataStatusMap(List.of(progress));

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), is(INST2));
        assertThat(result.get(0).getNavn(), is(equalTo(INST2.getBeskrivelse())));
        assertThat(result.get(0).getStatuser().get(0).getMelding(), is(equalTo("OK")));
        assertThat(result.get(0).getStatuser().get(0).getDetaljert().get(0).getMiljo(), is(equalTo("q1")));
        assertThat(result.get(0).getStatuser().get(0).getDetaljert().get(0).getIdenter(), contains(IDENT_1));
    }

    @Test
    void shouldMapKdiStatusWithHashSeparator() {
        var progress = BestillingProgress.builder()
                .ident(IDENT_1)
                .instdataStatus("KDI_STATUS#q2:OK")
                .build();

        var result = BestillingInstdataStatusMapper.buildInstdataStatusMap(List.of(progress));

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), is(INST_KDI));
        assertThat(result.get(0).getNavn(), is(equalTo(INST_KDI.getBeskrivelse())));
        assertThat(result.get(0).getStatuser().get(0).getMelding(), is(equalTo("OK")));
        assertThat(result.get(0).getStatuser().get(0).getDetaljert().get(0).getMiljo(), is(equalTo("q2")));
    }

    @Test
    void shouldMapBothSystemsFromPipeSeparatedStatus() {
        var progress = BestillingProgress.builder()
                .ident(IDENT_1)
                .instdataStatus("INST2_STATUS#q1:OK|KDI_STATUS#q2:OK")
                .build();

        var result = BestillingInstdataStatusMapper.buildInstdataStatusMap(List.of(progress));

        assertThat(result, hasSize(2));
        assertThat(result.get(0).getId(), is(INST2));
        assertThat(result.get(0).getStatuser().get(0).getDetaljert().get(0).getMiljo(), is(equalTo("q1")));
        assertThat(result.get(1).getId(), is(INST_KDI));
        assertThat(result.get(1).getStatuser().get(0).getDetaljert().get(0).getMiljo(), is(equalTo("q2")));
    }

    @Test
    void shouldResolveForholdOkStatus() {
        var progress = BestillingProgress.builder()
                .ident(IDENT_1)
                .instdataStatus("q1:opphold$OK")
                .build();

        var result = BestillingInstdataStatusMapper.buildInstdataStatusMap(List.of(progress));

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getStatuser().get(0).getMelding(), is(equalTo("OK")));
    }

    @Test
    void shouldResolveForholdErrorStatus() {
        var progress = BestillingProgress.builder()
                .ident(IDENT_1)
                .instdataStatus("q1:opphold$Feil ved opprettelse")
                .build();

        var result = BestillingInstdataStatusMapper.buildInstdataStatusMap(List.of(progress));

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getStatuser().get(0).getMelding(), is(equalTo("opphold: Feil ved opprettelse")));
    }

    @Test
    void shouldDecodeEncodedCharacters() {
        var progress = BestillingProgress.builder()
                .ident(IDENT_1)
                .instdataStatus("q1:Feil= kode;verdi&ekstra")
                .build();

        var result = BestillingInstdataStatusMapper.buildInstdataStatusMap(List.of(progress));

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getStatuser().get(0).getMelding(), is(equalTo("Feil: kode,verdi,ekstra")));
    }

    @Test
    void shouldGroupMultipleIdentsUnderSameStatus() {
        var progress1 = BestillingProgress.builder()
                .ident(IDENT_1)
                .instdataStatus("q1:OK")
                .build();
        var progress2 = BestillingProgress.builder()
                .ident(IDENT_2)
                .instdataStatus("q1:OK")
                .build();

        var result = BestillingInstdataStatusMapper.buildInstdataStatusMap(List.of(progress1, progress2));

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getStatuser(), hasSize(1));
        assertThat(result.get(0).getStatuser().get(0).getDetaljert().get(0).getIdenter(),
                containsInAnyOrder(IDENT_1, IDENT_2));
    }

    @Test
    void shouldGroupMultipleEnvironmentsUnderSameStatus() {
        var progress = BestillingProgress.builder()
                .ident(IDENT_1)
                .instdataStatus("q1:OK,q2:OK")
                .build();

        var result = BestillingInstdataStatusMapper.buildInstdataStatusMap(List.of(progress));

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getStatuser(), hasSize(1));
        assertThat(result.get(0).getStatuser().get(0).getMelding(), is(equalTo("OK")));
        assertThat(result.get(0).getStatuser().get(0).getDetaljert(), hasSize(2));

        var miljoer = result.get(0).getStatuser().get(0).getDetaljert().stream()
                .map(RsStatusRapport.Detaljert::getMiljo)
                .toList();
        assertThat(miljoer, containsInAnyOrder("q1", "q2"));
    }

    @Test
    void shouldSkipStatusEntryWithBlankErrorMessage() {
        var progress = BestillingProgress.builder()
                .ident(IDENT_1)
                .instdataStatus("q1:,q2:OK")
                .build();

        var result = BestillingInstdataStatusMapper.buildInstdataStatusMap(List.of(progress));

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getStatuser(), hasSize(1));
        assertThat(result.get(0).getStatuser().get(0).getDetaljert().get(0).getMiljo(), is(equalTo("q2")));
    }
}
