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
public class UtflyttingDTO extends DbVersjonDTO {

    private static final String LANDKODE_UKJENT = "XUK";

    private String tilflyttingsland;
    private String tilflyttingsstedIUtlandet;
    private LocalDateTime utflyttingsdato;

    @JsonIgnore
    public boolean isVelkjentLand(){

        return !LANDKODE_UKJENT.equals(getTilflyttingsland());
    }
}