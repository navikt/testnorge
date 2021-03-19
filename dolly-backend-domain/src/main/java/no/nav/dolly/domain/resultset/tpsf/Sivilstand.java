package no.nav.dolly.domain.resultset.tpsf;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Sivilstand {

    private Long id;
    private Person person;
    private Sivilstatus sivilstand;
    private LocalDateTime sivilstandRegdato;
    private Person personRelasjonMed;

    public enum Sivilstatus {
        UGIF,
        GIFT,
        ENKE,
        SKIL,
        SEPR,
        REPA,
        SEPA,
        SKPA,
        GJPA,
        SAMB
    }

    @JsonIgnore
    public boolean isGift() {

        if (isNull(getSivilstand())) {
            return false;

        } else {
            switch (getSivilstand()) {
                case GIFT:
                case REPA:
                case SEPR:
                case SEPA:
                    return true;
                default:
                    return false;
            }
        }
    }
}