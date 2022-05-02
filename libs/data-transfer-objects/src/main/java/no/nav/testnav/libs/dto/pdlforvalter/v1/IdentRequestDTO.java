package no.nav.testnav.libs.dto.pdlforvalter.v1;

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
public class IdentRequestDTO extends DbVersjonDTO {

    // All fields are optional
    private Identtype identtype;
    private KjoennDTO.Kjoenn kjoenn;
    private LocalDateTime foedtEtter;
    private LocalDateTime foedtFoer;
    private Integer alder;
    private Boolean syntetisk;
    private String eksisterendeIdent;

    private NyttNavnDTO nyttNavn;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NyttNavnDTO implements Serializable {

        private Boolean hasMellomnavn;
    }
}
