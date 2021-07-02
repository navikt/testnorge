package no.nav.registre.testnorge.libs.dto.pdlforvalter.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StatsborgerskapDTO extends DbVersjonDTO {

    private String landkode;
    private LocalDateTime gyldigFom;
    private LocalDateTime gyldigTom;
}