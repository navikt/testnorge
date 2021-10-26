package no.nav.dolly.domain.resultset.tpsf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InnvandretUtvandret {

    public enum InnUtvandret {INNVANDRET, UTVANDRET, NIL}

    private Long id;
    private InnUtvandret innutvandret;
    private String landkode;
    private LocalDateTime flyttedato;
}