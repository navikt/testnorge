package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BostedadresseDTO extends AdresseDTO {

    private VegadresseDTO vegadresse;
    private UkjentBostedDTO ukjentBosted;
    private MatrikkeladresseDTO matrikkeladresse;
    private UtenlandskAdresseDTO utenlandskAdresse;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UkjentBostedDTO implements Serializable {

        private String bostedskommune;
    }
}
