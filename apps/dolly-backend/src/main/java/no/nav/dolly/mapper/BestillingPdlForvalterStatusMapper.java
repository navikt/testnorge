package no.nav.dolly.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.bestilling.pdlforvalter.PdlForvalterClient;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import no.nav.dolly.domain.resultset.SystemTyper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.pdlforvalter.PdlForvalterClient.FALSK_IDENTITET;
import static no.nav.dolly.bestilling.pdlforvalter.PdlForvalterClient.KONTAKTINFORMASJON_DOEDSBO;
import static no.nav.dolly.bestilling.pdlforvalter.PdlForvalterClient.UTENLANDS_IDENTIFIKASJONSNUMMER;
import static no.nav.dolly.domain.resultset.SystemTyper.PDL_DODSBO;
import static no.nav.dolly.domain.resultset.SystemTyper.PDL_FALSKID;
import static no.nav.dolly.domain.resultset.SystemTyper.PDL_FORVALTER;
import static no.nav.dolly.domain.resultset.SystemTyper.PDL_UTENLANDSID;
import static no.nav.dolly.mapper.BestillingMeldingStatusIdentMapper.resolveStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingPdlForvalterStatusMapper {

    private static final String OKEY = "OK";

    public static List<RsStatusRapport> buildPdlForvalterStatusMap(List<BestillingProgress> progressList, ObjectMapper objectMapper) {

        //  melding     status      ident
        Map<String, Map<String, List<String>>> msgStatusIdents = new HashMap();

        progressList.forEach(progress -> {
            if (nonNull(progress.getPdlforvalterStatus())) {
                List.of(progress.getPdlforvalterStatus().split("\\$")).forEach(
                        resolveStatus(msgStatusIdents, progress)
                );
            }
        });

        List<RsStatusRapport> statusRapporter = new ArrayList<>();
        statusRapporter.addAll(extractStatus(msgStatusIdents, KONTAKTINFORMASJON_DOEDSBO, PDL_DODSBO));
        statusRapporter.addAll(extractStatus(msgStatusIdents, UTENLANDS_IDENTIFIKASJONSNUMMER, PDL_UTENLANDSID));
        statusRapporter.addAll(extractStatus(msgStatusIdents, FALSK_IDENTITET, PDL_FALSKID));
        statusRapporter.addAll(extractStatus(msgStatusIdents, PdlForvalterClient.PDL_FORVALTER, PDL_FORVALTER));

        var pdlDataStatus = BestillingPdlDataStatusMapper.buildPdlDataStatusMap(progressList, objectMapper);

        if (statusRapporter.isEmpty() && !pdlDataStatus.isEmpty()) {
            statusRapporter.add(RsStatusRapport.builder()
                    .id(PDL_FORVALTER)
                    .navn(PDL_FORVALTER.getBeskrivelse())
                    .statuser(new ArrayList<>(List.of(RsStatusRapport.Status.builder()
                            .melding("OK")
                            .identer(progressList.stream()
                                    .map(BestillingProgress::getIdent)
                                    .collect(Collectors.toList()))
                            .build())))
                    .build());
        }

        if (!pdlDataStatus.isEmpty()) {
            pdlDataStatus.stream().findFirst().get()
                    .getStatuser().stream()
                    .forEach(status -> addPdlDataStatus(statusRapporter, status));
        }

        return statusRapporter;
    }

    private static void removeAmbiguousOKs(RsStatusRapport statusRapporter) {

        var identerMedFeil = statusRapporter.getStatuser().stream()
                .filter(rapport -> !OKEY.equals(rapport.getMelding()))
                .map(RsStatusRapport.Status::getIdenter)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        Iterator<RsStatusRapport.Status> it = statusRapporter.getStatuser().iterator();
        while (it.hasNext()) {
            RsStatusRapport.Status status = it.next();

            if (OKEY.equals(status.getMelding())) {
                status.setIdenter(status.getIdenter().stream()
                        .filter(ident -> !identerMedFeil.contains(ident))
                        .collect(Collectors.toList()));

                if (status.getIdenter().isEmpty()) {
                    it.remove();
                }
            }
        }
    }

    private static void addPdlDataStatus(List<RsStatusRapport> statusRapporter, RsStatusRapport.Status nyStatus) {

        for (var rapport : statusRapporter) {
            if (rapport.getId() == PDL_FORVALTER) {
                var statusFound = false;
                for (var status : rapport.getStatuser()) {
                    if (status.getMelding().equals(nyStatus.getMelding())) {
                        status.setIdenter(Stream.concat(status.getIdenter().stream(), nyStatus.getIdenter().stream())
                                .distinct()
                                .collect(Collectors.toList()));
                        statusFound = true;
                    }
                }
                if (!statusFound) {
                    rapport.getStatuser().add(RsStatusRapport.Status.builder()
                            .melding(nyStatus.getMelding())
                            .identer(nyStatus.getIdenter())
                            .detaljert(nyStatus.getDetaljert())
                            .build());
                }
            }

            removeAmbiguousOKs(rapport);
        }
    }

    private static List<RsStatusRapport> extractStatus(Map<String, Map<String, List<String>>> msgStatusIdents, String clientid, SystemTyper type) {
        return msgStatusIdents.entrySet().stream().filter(entry -> clientid.equals(entry.getKey()))
                .map(entry -> RsStatusRapport.builder().id(type).navn(type.getBeskrivelse())
                        .statuser(entry.getValue().entrySet().stream()
                                .map(entry1 -> RsStatusRapport.Status.builder()
                                        .melding(entry1.getKey().replace(';', ',').replace('=', ':'))
                                        .identer(entry1.getValue())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }
}