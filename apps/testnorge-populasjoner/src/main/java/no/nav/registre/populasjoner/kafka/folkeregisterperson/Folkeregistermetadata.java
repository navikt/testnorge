package no.nav.registre.populasjoner.kafka.folkeregisterperson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Folkeregistermetadata {

    private LocalDateTime ajourholdstidspunkt;
    private LocalDateTime gyldighetstidspunkt;
    private LocalDateTime opphoerstidspunkt;
    private String kilde;
    private String aarsak;
    private Integer sekvens;
    @JsonIgnore
    private Boolean gjeldende;
}
