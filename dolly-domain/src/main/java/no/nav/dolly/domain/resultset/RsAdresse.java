package no.nav.dolly.domain.resultset;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "adressetype")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RsGateadresse.class, name = "GATE"),
        @JsonSubTypes.Type(value = RsMatrikkeladresse.class, name = "MATR")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class RsAdresse {

    private String postnr;

    private String kommunenr;

    private LocalDateTime flyttedato;

}
