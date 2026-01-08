package no.nav.dolly.mapper;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PdlStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static no.nav.dolly.domain.resultset.SystemTyper.PDL_ORDRE;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.decodeMsg;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@UtilityClass
public final class BestillingPdlOrdreStatusMapper {

    private static final String JSON_PARSE_ERROR = "Teknisk feil, se logg! Parsing av response fra PDL-forvalter feilet";
    private static final String ELEMENT_ERROR_FMT = "FEIL: Element: %s, Id: %d, Beskrivelse: %s";

    public static List<RsStatusRapport> buildPdlOrdreStatusMap(List<BestillingProgress> bestProgress, ObjectMapper objectMapper) {

        //  melding      ident
        Map<String, List<String>> meldingIdents = new HashMap<>();

        bestProgress.forEach(progress -> {
            if (isNotBlank(progress.getPdlOrdreStatus()) && isNotBlank(progress.getIdent())) {

                if (progress.getPdlOrdreStatus().contains("PDL_") || progress.getPdlOrdreStatus().contains("\"ordrer\":[]")) {
                    extractStatus(meldingIdents, progress, objectMapper);

                } else {
                    addElement(meldingIdents, progress.getPdlOrdreStatus(), progress.getIdent());
                }
            }
        });

        return meldingIdents.isEmpty() ? emptyList() : formatStatus(meldingIdents);
    }

    private static void extractStatus(Map<String, List<String>> meldingIdents, BestillingProgress progress, ObjectMapper objectMapper) {

        try {
            var response = objectMapper.readValue(progress.getPdlOrdreStatus(), OrdreResponseDTO.class);
            var errors = collectErrors(response);

            if (errors.isEmpty() || response.getHovedperson().getOrdrer().stream()
                    .noneMatch(ordre -> ordre.getHendelser().stream()
                            .anyMatch(hendelse -> PdlStatus.FEIL == hendelse.getStatus()))) {

                addElement(meldingIdents, "OK", progress.getIdent());
            }

            errors.forEach(error ->
                    addElement(meldingIdents, format(ELEMENT_ERROR_FMT,
                            error.getArtifact(), error.getId(), error.getError()),
                            markRelation(error.getIdent(), progress.getIdent()))
            );

        } catch (JacksonException e) {
            addElement(meldingIdents, JSON_PARSE_ERROR, progress.getIdent());
            log.error("Json parsing feilet: {}", e.getMessage(), e);
        }
    }

    private static String markRelation(String ident, String hovedperson) {

        var person = new StringBuilder();

        if (!hovedperson.equals(ident)) {
            person.append('(');
        }

        person.append(ident);

        if (!hovedperson.equals(ident)) {
            person.append(')');
        }

        return person.toString();
    }

    private static void addElement(Map<String, List<String>> rapport, String melding, String ident) {

        if (rapport.containsKey(melding)) {
            rapport.get(melding).add(ident);

        } else {
            rapport.put(melding, new ArrayList<>(List.of(isNotBlank(ident) ? ident : "?")));
        }
    }

    private static List<PdlInternalStatus> collectErrors(OrdreResponseDTO response) {

        return Stream.of(List.of(response.getHovedperson()),
                        response.getRelasjoner())
                .flatMap(Collection::stream)
                .map(personHendelse -> personHendelse.getOrdrer().stream()
                        .map(ordre -> ordre.getHendelser().stream()
                                .filter(hendelse -> PdlStatus.FEIL == hendelse.getStatus())
                                .map(hendelse -> PdlInternalStatus.builder()
                                        .artifact(ordre.getInfoElement())
                                        .ident(personHendelse.getIdent())
                                        .id(hendelse.getId())
                                        .error(hendelse.getError())
                                        .build())
                                .toList())
                        .flatMap(Collection::stream)
                        .toList())
                .flatMap(Collection::stream)
                .toList();
    }

    private static List<RsStatusRapport> formatStatus(Map<String, List<String>> meldingIdents) {

        return List.of(RsStatusRapport.builder()
                .id(PDL_ORDRE)
                .navn(PDL_ORDRE.getBeskrivelse())
                .statuser(new ArrayList<>(meldingIdents.entrySet().stream()
                        .map(entry -> RsStatusRapport.Status.builder()
                                .melding(decodeMsg(entry.getKey()))
                                .identer(entry.getValue())
                                .build())
                        .toList()))
                .build());
    }

    @Data
    @Builder
    private static class PdlInternalStatus {

        private PdlArtifact artifact;
        private Integer id;
        private String error;
        private String ident;
    }
}
