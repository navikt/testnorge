package no.nav.dolly.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PdlStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.PDL_DATA;

@Slf4j
@UtilityClass
public final class BestillingPdlDataStatusMapper {

    private static final String JSON_PARSE_ERROR = "Teknisk feil, se logg! Parsing av response fra PDL-forvalter feilet";
    private static final String ELEMENT_ERROR_FMT = "FEIL: Element: %s, Id: %d, Beskrivelse: %s";

    public static List<RsStatusRapport> buildPdlDataStatusMap(List<BestillingProgress> bestProgress, ObjectMapper objectMapper) {

        //  melding      ident
        Map<String, List<String>> meldingIdents = new HashMap<>();

        bestProgress.forEach(progress -> {
            if (nonNull(progress.getPdlDataStatus())) {

                if (progress.getPdlDataStatus().contains("PDL_OPPRETT_PERSON")) {
                    extractStatus(meldingIdents, progress, objectMapper);

                } else {
                    addElement(meldingIdents, progress.getPdlDataStatus(), progress.getIdent());
                }
            }
        });

        return meldingIdents.isEmpty() ? emptyList() : formatStatus(meldingIdents, PDL_DATA);
    }

    private static void extractStatus(Map<String, List<String>> meldingIdents, BestillingProgress progress, ObjectMapper objectMapper) {

        try {
            var response = objectMapper.readValue(progress.getPdlDataStatus(), OrdreResponseDTO.class);
            var errors = collectErrors(response);

            if (errors.isEmpty()) {
                addElement(meldingIdents, "OK", progress.getIdent());

            } else {
                errors.forEach(error ->
                        addElement(meldingIdents, format(ELEMENT_ERROR_FMT,
                                error.getArtifact(), error.getId(), error.getError()), progress.getIdent())
                );
            }

        } catch (JsonProcessingException e) {
            addElement(meldingIdents, JSON_PARSE_ERROR, progress.getIdent());
            log.error("Json parsing feilet: {}", e);
        }
    }

    private static void addElement(Map<String, List<String>> rapport, String melding, String ident) {

        if (rapport.containsKey(melding)) {
            rapport.get(melding).add(ident);

        } else {
            rapport.put(melding, new ArrayList<>(List.of(ident)));
        }
    }

    private static List<PdlInternalStatus> collectErrors(OrdreResponseDTO response) {

        return response.getHovedperson().getOrdrer().stream()
                .filter(ordre -> !PdlArtifact.PDL_SLETTING.equals(ordre.getInfoElement()))
                .filter(ordre -> ordre.getHendelser().stream().anyMatch(hendelse -> PdlStatus.FEIL == hendelse.getStatus()))
                .map(ordre -> ordre.getHendelser().stream()
                        .filter(hendelse -> PdlStatus.FEIL == hendelse.getStatus())
                        .map(hendelse -> PdlInternalStatus.builder()
                                .artifact(ordre.getInfoElement())
                                .id(hendelse.getId())
                                .error(hendelse.getError())
                                .build())
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static List<RsStatusRapport> formatStatus(Map<String, List<String>> meldingIdents, SystemTyper type) {

        return List.of(RsStatusRapport.builder()
                .id(type)
                .navn(type.getBeskrivelse())
                .statuser(meldingIdents.entrySet().stream()
                        .map(entry -> RsStatusRapport.Status.builder()
                                .melding(entry.getKey().replace(';', ',').replace('=', ':'))
                                .identer(entry.getValue())
                                .build())
                        .collect(Collectors.toList()))
                .build());
    }

    @Data
    @Builder
    private static class PdlInternalStatus {

        private PdlArtifact artifact;
        private Integer id;
        private String error;
    }
}