package no.nav.dolly.mapper;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.pdlforvalter.PdlForvalterClient.FALSK_IDENTITET;
import static no.nav.dolly.bestilling.pdlforvalter.PdlForvalterClient.KONTAKTINFORMASJON_DOEDSBO;
import static no.nav.dolly.bestilling.pdlforvalter.PdlForvalterClient.PDL_FORVALTER;
import static no.nav.dolly.bestilling.pdlforvalter.PdlForvalterClient.UTENLANDS_IDENTIFIKASJONSNUMMER;
import static no.nav.dolly.mapper.BestillingMeldingStatusIdentMapper.resolveStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsPdlForvalterStatus;
import no.nav.dolly.domain.resultset.RsStatusIdent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingPdlForvalterStatusMapper {

    public static RsPdlForvalterStatus buildPdldataStatusMap(List<BestillingProgress> progressList) {

        //  melding     status      ident
        Map<String, Map<String, List<String>>> msgStatusIdents = new HashMap();

        progressList.forEach(progress -> {
            if (nonNull(progress.getPdlforvalterStatus())) {
                newArrayList(progress.getPdlforvalterStatus().split("\\$")).forEach(
                        resolveStatus(msgStatusIdents, progress)
                );
            }
        });

        return prepareResult(msgStatusIdents);
    }

    private static RsPdlForvalterStatus prepareResult(Map<String, Map<String, List<String>>> msgStatusIdents) {

        return msgStatusIdents.containsKey(KONTAKTINFORMASJON_DOEDSBO) || msgStatusIdents.containsKey(UTENLANDS_IDENTIFIKASJONSNUMMER)
                || msgStatusIdents.containsKey(FALSK_IDENTITET) || msgStatusIdents.containsKey(PDL_FORVALTER) ?
                RsPdlForvalterStatus.builder()
                        .kontaktinfoDoedsbo(buildMessageStatus(msgStatusIdents.get(KONTAKTINFORMASJON_DOEDSBO)))
                        .utenlandsid(buildMessageStatus(msgStatusIdents.get(UTENLANDS_IDENTIFIKASJONSNUMMER)))
                        .falskIdentitet(buildMessageStatus(msgStatusIdents.get(FALSK_IDENTITET)))
                        .pdlForvalter(buildMessageStatus(msgStatusIdents.get(PDL_FORVALTER)))
                        .build()
                : null;
    }

    private static List<RsStatusIdent> buildMessageStatus(Map<String, List<String>> statusIdent) {
        List<RsStatusIdent> result = new ArrayList();
        if (nonNull(statusIdent)) {
            statusIdent.entrySet().forEach(entry ->
                    result.add(RsStatusIdent.builder()
                            .statusMelding(entry.getKey())
                            .identer(entry.getValue())
                            .build()));
        }
        return result;
    }
}