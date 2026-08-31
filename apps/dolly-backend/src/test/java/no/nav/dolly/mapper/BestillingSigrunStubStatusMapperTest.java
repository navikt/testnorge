package no.nav.dolly.mapper;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import no.nav.dolly.domain.resultset.SystemTyper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BestillingSigrunStubStatusMapperTest {

    private static final List<BestillingProgress> RUN_STATUS = asList(
            BestillingProgress.builder().ident("IDENT_1")
                    .sigrunstubStatus("SIGRUN_PENSJONSGIVENDE:OK")
                    .build(),
            BestillingProgress.builder().ident("IDENT_2")
                    .sigrunstubStatus("SIGRUN_PENSJONSGIVENDE:FEIL")
                    .build(),
            BestillingProgress.builder().ident("IDENT_3")
                    .sigrunstubStatus("SIGRUN_SUMMERT:OK")
                    .build(),
            BestillingProgress.builder().ident("IDENT_4")
                    .sigrunstubStatus("SIGRUN_SUMMERT:FEIL")
                    .build(),
            BestillingProgress.builder().ident("IDENT_5")
                    .sigrunstubStatus("OK")
                    .build()
    );

    @Test
    void shouldMapPensjonsgivendeAndSummertSkattegrunnlagStatuses() {

        List<RsStatusRapport> identStatuses = BestillingSigrunStubStatusMapper.buildSigrunStubStatusMap(RUN_STATUS);

        assertThat(identStatuses)
                .extracting(RsStatusRapport::getId)
                .containsExactlyInAnyOrder(SystemTyper.SIGRUN_PENSJONSGIVENDE, SystemTyper.SIGRUN_SUMMERT);

        assertThat(findStatusReport(identStatuses, SystemTyper.SIGRUN_PENSJONSGIVENDE).getStatuser())
                .extracting(RsStatusRapport.Status::getMelding, RsStatusRapport.Status::getIdenter)
                .containsExactlyInAnyOrder(
                        org.assertj.core.groups.Tuple.tuple("OK", List.of("IDENT_1")),
                        org.assertj.core.groups.Tuple.tuple("FEIL", List.of("IDENT_2")));

        assertThat(findStatusReport(identStatuses, SystemTyper.SIGRUN_SUMMERT).getStatuser())
                .extracting(RsStatusRapport.Status::getMelding, RsStatusRapport.Status::getIdenter)
                .containsExactlyInAnyOrder(
                        org.assertj.core.groups.Tuple.tuple("OK", List.of("IDENT_3")),
                        org.assertj.core.groups.Tuple.tuple("FEIL", List.of("IDENT_4")));
    }

    private RsStatusRapport findStatusReport(List<RsStatusRapport> identStatuses, SystemTyper type) {

        return identStatuses.stream()
                .filter(statusReport -> statusReport.getId() == type)
                .findFirst()
                .orElseThrow();
    }
}