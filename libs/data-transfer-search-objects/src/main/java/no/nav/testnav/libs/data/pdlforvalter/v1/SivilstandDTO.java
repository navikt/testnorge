package no.nav.testnav.libs.data.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
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
        GJENLEVENDE_PARTNER,
        SAMBOER
    }

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime bekreftelsesdato;
    private String relatertVedSivilstand;
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
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
    public boolean isGiftOrSamboer() {

        return isGift() || isSamboer();
    }

    @JsonIgnore
    public boolean isSamboer() {

        return Sivilstand.SAMBOER == type;
    }

    @JsonIgnore
    public boolean isNotSamboer() {

        return Sivilstand.SAMBOER != type;
    }

    @JsonIgnore
    public boolean isSeparert() {

        return Sivilstand.SEPARERT == type || Sivilstand.SEPARERT_PARTNER == type;
    }

    @JsonIgnore
    public boolean isUgift() {

        return Sivilstand.UGIFT == type;
    }

    @JsonIgnore
    public boolean hasRelatertVedSivilstand() {

        return isNotBlank(relatertVedSivilstand);
    }

    @JsonIgnore
    public Sivilstand getGjenlevendeSivilstand() {

        return switch (type) {
            case GIFT -> Sivilstand.ENKE_ELLER_ENKEMANN;
            case REGISTRERT_PARTNER -> Sivilstand.GJENLEVENDE_PARTNER;
            default -> type;
        };
    }

    @JsonIgnore
    @Override
    public String getIdentForRelasjon() {
        return relatertVedSivilstand;
    }
}