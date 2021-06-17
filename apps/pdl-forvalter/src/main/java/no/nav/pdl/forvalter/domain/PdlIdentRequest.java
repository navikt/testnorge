package no.nav.pdl.forvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlIdentRequest extends PdlDbVersjon {

    // All fields are optional
    private Identtype identtype;
    private PdlKjoenn.Kjoenn kjoenn;
    private LocalDateTime foedtEtter;
    private LocalDateTime foedtFoer;
    private Integer alder;
    private Boolean syntetisk;

    private NyttNavn nyttNavn;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class NyttNavn implements Serializable {

        private Boolean harMellomnavn;
    }
}
