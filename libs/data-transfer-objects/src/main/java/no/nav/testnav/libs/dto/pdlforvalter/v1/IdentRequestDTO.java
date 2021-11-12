package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class IdentRequestDTO extends DbVersjonDTO {

    // All fields are optional
    private Identtype identtype;
    private KjoennDTO.Kjoenn kjoenn;
    private LocalDateTime foedtEtter;
    private LocalDateTime foedtFoer;
    private Integer alder;
    private Boolean syntetisk;

    private NyttNavnDTO nyttNavn;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class NyttNavnDTO implements Serializable {

        private Boolean hasMellomnavn;
    }
}
