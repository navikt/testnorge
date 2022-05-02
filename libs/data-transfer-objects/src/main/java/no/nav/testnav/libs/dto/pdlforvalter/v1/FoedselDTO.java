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
public class FoedselDTO extends DbVersjonDTO {

    private String foedekommune;
    private String foedeland;
    private String foedested;
    private Integer foedselsaar;
    private LocalDateTime foedselsdato;
}
