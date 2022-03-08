package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SivilstandDTO extends DbVersjonDTO {

    public enum Sivilstand {
        UOPPGITT,
        UGIFT,
        GIFT,
        ENKE_ELLER_ENKEMANN,
        SKILT,
        SEPARERT,
        REGISTRERT_PARTNER,
        SEPARERT_PARTNER,
        SKILT_PARTNER,
        GJENLEVENDE_PARTNER
    }

    private LocalDateTime bekreftelsesdato;
    private String relatertVedSivilstand;
    private LocalDateTime sivilstandsdato;
    private Sivilstand type;

    private Boolean borIkkeSammen;
    private PersonRequestDTO nyRelatertPerson;

    private Boolean eksisterendePerson;

    public boolean isEksisterendePerson() {

        return isTrue(eksisterendePerson);
    }

    @JsonIgnore
    public boolean isGift() {

        return Sivilstand.GIFT == type || Sivilstand.REGISTRERT_PARTNER == type;
    }

    @JsonIgnore
    public boolean isSeparert() {

        return Sivilstand.SEPARERT == type || Sivilstand.SEPARERT_PARTNER == type;
    }

    @JsonIgnore
    public boolean isUgift() {

        return Sivilstand.UGIFT == type;
    }
}