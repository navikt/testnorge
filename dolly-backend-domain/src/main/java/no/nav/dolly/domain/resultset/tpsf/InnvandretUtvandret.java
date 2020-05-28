package no.nav.dolly.domain.resultset.tpsf;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InnvandretUtvandret {

    public enum InnUtvandret {INNVANDRET, UTVANDRET, NIL}

    private Long id;
    private InnUtvandret innutvandret;
    private String landkode;
    private LocalDateTime flyttedato;
}