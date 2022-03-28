package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UtflyttingDTO extends DbVersjonDTO {

    private String tilflyttingsland;
    private String tilflyttingsstedIUtlandet;
    private LocalDateTime utflyttingsdato;

    @JsonIgnore
    public boolean isVelkjentLand(){

        return !"XUK".equals(getTilflyttingsland());
    }
}