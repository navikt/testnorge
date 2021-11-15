package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class DeltBostedDTO extends DbVersjonDTO {

    private String adresseIdentifikatorFraMatrikkelen;
    private String coAdressenavn;
    private LocalDateTime sluttdatoForKontrakt;
    private LocalDateTime startdatoForKontrakt;
    private VegadresseDTO vegadresse;
    private UkjentBostedDTO ukjentBosted;
    private MatrikkeladresseDTO matrikkeladresse;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UkjentBostedDTO implements Serializable {

        private String bostedskommune;
    }

    @JsonIgnore
    public int countAdresser() {

        return count(getVegadresse()) + count(getMatrikkeladresse()) + count(ukjentBosted);
    }
}
