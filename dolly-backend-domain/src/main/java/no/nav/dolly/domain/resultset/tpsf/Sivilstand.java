package no.nav.dolly.domain.resultset.tpsf;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sivilstand {

    public enum SIVILSTATUS {UGIF, GIFT, ENKE, SKIL, SEPR, REPA, SEPA, SKPA, GJPA, SAMB}

    private Long id;
    private Person person;
    private SIVILSTATUS sivilstand;
    private LocalDateTime sivilstandRegdato;
    private Person personRelasjonMed;
}