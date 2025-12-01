package no.nav.testnav.libs.dto.pdlforvalter.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class NavPersonIdentifikatorDTO extends DbVersjonDTO {

    private String identifikator;
    private LocalDate gyldigFraOgMed;
    private LocalDate gyldigTilOgMed;
}