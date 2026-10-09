package no.nav.dolly.mapper;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static no.nav.dolly.domain.resultset.SystemTyper.HENVENDELSE;
import static org.assertj.core.api.Assertions.assertThat;

class BestillingHenvendelseStatusMapperTest {

    @Test
    void shouldReturnNoReportsWhenNoProgressExists() {

        var reports = BestillingHenvendelseStatusMapper.buildHenvendelseStatusMap(List.of());

        assertThat(reports).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", ",, ,"})
    void shouldIgnoreBlankStatuses(String status) {

        var reports = BestillingHenvendelseStatusMapper.buildHenvendelseStatusMap(
                List.of(progress("IDENT_1", status)));

        assertThat(reports).isEmpty();
    }

    @Test
    void shouldReturnReportForValidStatusAlongsideBlankStatuses() {

        var reports = BestillingHenvendelseStatusMapper.buildHenvendelseStatusMap(List.of(
                progress("IDENT_1", null),
                progress("IDENT_2", ""),
                progress("IDENT_3", ",, ,"),
                progress("IDENT_4", "melding:OK")));

        assertThat(reports).hasSize(1);
        assertThat(reports.getFirst().getId()).isEqualTo(HENVENDELSE);
        assertThat(reports.getFirst().getNavn()).isEqualTo(HENVENDELSE.getBeskrivelse());
        assertThat(reports.getFirst().getStatuser()).hasSize(1);
        assertThat(reports.getFirst().getStatuser().getFirst().getMelding()).isEqualTo("OK");
        assertThat(reports.getFirst().getStatuser().getFirst().getIdenter()).containsExactly("IDENT_4");
    }

    @Test
    void shouldGroupIdentifiersByErrorTypeAndStatusWithoutDuplicates() {

        var reports = BestillingHenvendelseStatusMapper.buildHenvendelseStatusMap(List.of(
                progress("IDENT_1", "samtalereferat:FEIL,samtalereferat:FEIL"),
                progress("IDENT_2", "samtalereferat:FEIL"),
                progress("IDENT_3", "melding:FEIL")));

        var statuses = reports.getFirst().getStatuser();
        assertThat(statuses).hasSize(2);
        assertThat(findStatus(statuses, "Feil: samtalereferat: FEIL").getIdenter())
                .containsExactlyInAnyOrder("IDENT_1", "IDENT_2");
        assertThat(findStatus(statuses, "Feil: melding: FEIL").getIdenter())
                .containsExactly("IDENT_3");
    }

    @Test
    void shouldIncludeOnlyIdentifiersSuccessfulForEveryType() {

        var reports = BestillingHenvendelseStatusMapper.buildHenvendelseStatusMap(List.of(
                progress("IDENT_1", "melding:OK"),
                progress("IDENT_1", "samtalereferat:OK"),
                progress("IDENT_2", "melding:OK,samtalereferat:FEIL"),
                progress("IDENT_2", "samtalereferat:OK")));

        var statuses = reports.getFirst().getStatuser();
        assertThat(findStatus(statuses, "OK").getIdenter()).containsExactly("IDENT_1");
        assertThat(findStatus(statuses, "Feil: samtalereferat: FEIL").getIdenter())
                .containsExactly("IDENT_2");
    }

    @Test
    void shouldExcludeIdentifiersMissingSuccessForAnyObservedType() {

        var reports = BestillingHenvendelseStatusMapper.buildHenvendelseStatusMap(List.of(
                progress("IDENT_1", "melding:OK,samtalereferat:FEIL"),
                progress("IDENT_2", "melding:OK"),
                progress("IDENT_3", "melding:OK,samtalereferat:OK")));

        var statuses = reports.getFirst().getStatuser();
        assertThat(findStatus(statuses, "OK").getIdenter()).containsExactly("IDENT_3");
        assertThat(findStatus(statuses, "Feil: samtalereferat: FEIL").getIdenter())
                .containsExactly("IDENT_1");
    }

    @Test
    void shouldDecodeErrorMessagesAndIgnoreEmptyEntries() {

        var reports = BestillingHenvendelseStatusMapper.buildHenvendelseStatusMap(List.of(
                progress("IDENT_1", ",melding:  ugyldig= verdi; feil&detalj  ,, ")));

        var statuses = reports.getFirst().getStatuser();
        assertThat(statuses).hasSize(1);
        assertThat(statuses.getFirst().getMelding()).isEqualTo("Feil: melding: ugyldig: verdi, feil,detalj");
        assertThat(statuses.getFirst().getIdenter()).containsExactly("IDENT_1");
    }

    @ParameterizedTest
    @ValueSource(strings = {"melding: ", "melding:  "})
    void shouldUseEmptyMessageWhenStatusIsMissing(String status) {

        var reports = BestillingHenvendelseStatusMapper.buildHenvendelseStatusMap(
                List.of(progress("IDENT_1", status)));

        assertThat(reports.getFirst().getStatuser()).hasSize(1);
        assertThat(reports.getFirst().getStatuser().getFirst().getMelding()).isEqualTo("Feil: melding: ");
        assertThat(reports.getFirst().getStatuser().getFirst().getIdenter()).containsExactly("IDENT_1");
    }

    private static BestillingProgress progress(String ident, String status) {

        return BestillingProgress.builder()
                .ident(ident)
                .henvendelseStatus(status)
                .build();
    }

    private static RsStatusRapport.Status findStatus(List<RsStatusRapport.Status> statuses, String message) {

        return statuses.stream()
                .filter(status -> message.equals(status.getMelding()))
                .findFirst()
                .orElseThrow();
    }
}
