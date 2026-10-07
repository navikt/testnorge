package no.nav.dolly.mapper;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static no.nav.dolly.domain.resultset.SystemTyper.INNTK;
import static org.assertj.core.api.Assertions.assertThat;

class BestillingInntektstubStatusMapperTest {

    @Test
    void shouldReturnEmptyInntektstubReportWhenNoProgressExists() {

        var reports = BestillingInntektstubStatusMapper.buildInntektstubStatusMap(List.of());

        assertThat(reports).hasSize(1);
        assertThat(reports.getFirst().getId()).isEqualTo(INNTK);
        assertThat(reports.getFirst().getNavn()).isEqualTo(INNTK.getBeskrivelse());
        assertThat(reports.getFirst().getStatuser()).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", ",, ,"})
    void shouldIgnoreBlankStatuses(String status) {

        var reports = BestillingInntektstubStatusMapper.buildInntektstubStatusMap(
                List.of(progress("IDENT_1", status)));

        assertThat(reports.getFirst().getStatuser()).isEmpty();
    }

    @Test
    void shouldGroupIdentifiersByTypeAndStatusWithoutDuplicates() {

        var reports = BestillingInntektstubStatusMapper.buildInntektstubStatusMap(List.of(
                progress("IDENT_1", "Import: OK,Import: OK,Oppretting: Feil= Lagring feilet"),
                progress("IDENT_2", "Import: OK,Oppretting: Feil= Lagring feilet"),
                progress("IDENT_3", "Import: Henting feilet")));

        var statuses = reports.getFirst().getStatuser();
        assertThat(statuses).hasSize(3);
        assertThat(statuses.getFirst().getMelding()).isEqualTo("OK");
        assertThat(findStatus(statuses, "OK").getIdenter())
                .containsExactlyInAnyOrder("IDENT_1", "IDENT_2");
        assertThat(findStatus(statuses, "Feil: Oppretting: Lagring feilet").getIdenter())
                .containsExactlyInAnyOrder("IDENT_1", "IDENT_2");
        assertThat(findStatus(statuses, "Feil: Import: Henting feilet").getIdenter())
                .containsExactly("IDENT_3");
    }

    @Test
    void shouldKeepSuccessfulImportAndOpprettingStatusesSeparate() {

        var reports = BestillingInntektstubStatusMapper.buildInntektstubStatusMap(List.of(
                progress("IDENT_1", "Import: OK"),
                progress("IDENT_2", ",Oppretting: OK")));

        var statuses = reports.getFirst().getStatuser();
        assertThat(statuses).hasSize(2);
        assertThat(statuses).allSatisfy(status -> assertThat(status.getMelding()).isEqualTo("OK"));
        assertThat(statuses).extracting(RsStatusRapport.Status::getIdenter)
                .containsExactlyInAnyOrder(List.of("IDENT_1"), List.of("IDENT_2"));
    }

    @Test
    void shouldDecodeErrorMessagesAndIgnoreEmptyEntries() {

        var reports = BestillingInntektstubStatusMapper.buildInntektstubStatusMap(List.of(
                progress("IDENT_1", ",Oppretting:  ugyldig= verdi; feil&detalj  ,, ")));

        var statuses = reports.getFirst().getStatuser();
        assertThat(statuses).hasSize(1);
        assertThat(statuses.getFirst().getMelding()).isEqualTo("Feil: Oppretting: ugyldig: verdi, feil,detalj");
        assertThat(statuses.getFirst().getIdenter()).containsExactly("IDENT_1");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Feil= Lagring feilet", "Oppretting: Feil= Lagring feilet"})
    void shouldMapLegacyAndTypedCreationErrors(String status) {

        var reports = BestillingInntektstubStatusMapper.buildInntektstubStatusMap(
                List.of(progress("IDENT_1", status)));

        assertThat(reports.getFirst().getStatuser()).hasSize(1);
        assertThat(reports.getFirst().getStatuser().getFirst().getMelding()).isEqualTo("Feil: Oppretting: Lagring feilet");
        assertThat(reports.getFirst().getStatuser().getFirst().getIdenter()).containsExactly("IDENT_1");
    }

    @Test
    void shouldMapLegacySuccessStatus() {

        var reports = BestillingInntektstubStatusMapper.buildInntektstubStatusMap(
                List.of(progress("IDENT_1", "OK")));

        assertThat(reports.getFirst().getStatuser()).hasSize(1);
        assertThat(reports.getFirst().getStatuser().getFirst().getMelding()).isEqualTo("OK");
        assertThat(reports.getFirst().getStatuser().getFirst().getIdenter()).containsExactly("IDENT_1");
    }

    @Test
    void shouldGroupEachCreationErrorWithAllAffectedIdentifiers() {

        var reports = BestillingInntektstubStatusMapper.buildInntektstubStatusMap(List.of(
                progress("IDENT_1", ",Oppretting: Feil= First= error; details,Oppretting: Feil= Second error"),
                progress("IDENT_2", "Oppretting: Feil= Second error")));

        var statuses = reports.getFirst().getStatuser();
        assertThat(statuses).hasSize(2);
        assertThat(findStatus(statuses, "Feil: Oppretting: First: error, details").getIdenter())
                .containsExactly("IDENT_1");
        assertThat(findStatus(statuses, "Feil: Oppretting: Second error").getIdenter())
                .containsExactlyInAnyOrder("IDENT_1", "IDENT_2");
    }

    private static BestillingProgress progress(String ident, String status) {

        return BestillingProgress.builder()
                .ident(ident)
                .inntektstubStatus(status)
                .build();
    }

    private static RsStatusRapport.Status findStatus(List<RsStatusRapport.Status> statuses, String message) {

        return statuses.stream()
                .filter(status -> message.equals(status.getMelding()))
                .findFirst()
                .orElseThrow();
    }
}
