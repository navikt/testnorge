package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.nonNull;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class DbVersjonDTO implements Serializable {

    private Metadata metadata;

    @Schema(description = "Denne kan også benyttes ved behov")
    private FolkeregistermetadataDTO folkeregistermetadata;

    @JsonIgnore
    protected static <T> int count(T artifact) {
        return nonNull(artifact) ? 1 : 0;
    }

    public enum Master {FREG, PDL}

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Metadata {

        private List<Endringer> endringer;
        private boolean historisk;
        private Master master;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Endringer {

        private String kilde;
        private LocalDateTime registrert;
        private String registrertAv;
        private String systemkilde;
        private String type;
    }
}