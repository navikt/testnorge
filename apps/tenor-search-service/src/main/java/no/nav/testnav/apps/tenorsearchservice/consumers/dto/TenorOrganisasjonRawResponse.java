package no.nav.testnav.apps.tenorsearchservice.consumers.dto;

import tools.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
public class TenorOrganisasjonRawResponse {

    private Integer treff;
    private Integer rader;
    private Integer offset;
    private Integer nesteSide;
    private Integer seed;
    private List<DokumentOrganisasjon> dokumentListe;

    public List<DokumentOrganisasjon> getDokumentListe() {

        if (isNull(dokumentListe)) {
            dokumentListe = new ArrayList<>();
        }
        return dokumentListe;
    }

    @Data
    @NoArgsConstructor
    public static class DokumentOrganisasjon {
        private String navn;
        private TenorMetadata tenorMetadata;
        private JsonNode tenorRelasjoner;
    }

    @Data
    @NoArgsConstructor
    public static class TenorMetadata {
        private List<String> kilder;
        private String kildedata;
        private String oppdatert;
        private Integer datasettVersjon;
        private String opprettet;
        private String id;
        private String indeksert;
    }
}
