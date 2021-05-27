package no.nav.dolly.domain.resultset.tpsf.adresse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "adressetype")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BoGateadresse.class, name = "GATE"),
        @JsonSubTypes.Type(value = BoMatrikkeladresse.class, name = "MATR")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BoAdresse {

    private String adressetype;

    private String postnr;

    private String kommunenr;

    private LocalDateTime flyttedato;

    private String tilleggsadresse;

    private String bolignr;

    private Boolean deltAdresse;

    private LocalDateTime gyldigTilOgMed;

    private String matrikkelId;

    public abstract String getAdressetype();

    public boolean isGateadresse() {
        return "GATE".equals(getAdressetype());
    }

    public boolean isMatrikkeladresse() {
        return "MATR".equals(getAdressetype());
    }
}