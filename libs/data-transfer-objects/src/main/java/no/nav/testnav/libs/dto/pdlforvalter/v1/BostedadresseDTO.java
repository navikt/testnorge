package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BostedadresseDTO extends AdresseDTO {

    private LocalDateTime angittFlyttedato;
    private VegadresseDTO vegadresse;
    private UkjentBostedDTO ukjentBosted;
    private MatrikkeladresseDTO matrikkeladresse;
    private UtenlandskAdresseDTO utenlandskAdresse;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class UkjentBostedDTO implements Serializable {

        private String bostedskommune;
    }

    @JsonIgnore
    public int countAdresser() {

        return count(getVegadresse()) + count(getMatrikkeladresse()) +
                count(getUkjentBosted()) + count(getUtenlandskAdresse());
    }
}
