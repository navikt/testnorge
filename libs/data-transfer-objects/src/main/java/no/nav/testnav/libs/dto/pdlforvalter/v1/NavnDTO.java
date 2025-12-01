package no.nav.testnav.libs.dto.pdlforvalter.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class NavnDTO extends DbVersjonDTO {

    private String etternavn;
    private String fornavn;
    private String mellomnavn;
    private Boolean hasMellomnavn;
    private LocalDateTime gyldigFraOgMed;
}
