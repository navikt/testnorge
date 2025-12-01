package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class StatsborgerskapDTO extends DbVersjonDTO {

    private String landkode;
    private LocalDateTime gyldigFraOgMed;
    private LocalDateTime gyldigTilOgMed;
    private LocalDateTime bekreftelsesdato;

    @JsonIgnore
    public boolean isNorskStatsborger() {

        return "NOR".equalsIgnoreCase(landkode);
    }

    @JsonIgnore
    public boolean isUtenlandskStatsborger() {

        return !isNorskStatsborger();
    }
}