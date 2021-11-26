package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class KontaktadresseDTO extends AdresseDTO {

    private VegadresseDTO vegadresse;
    private UtenlandskAdresseDTO utenlandskAdresse;
    private PostboksadresseDTO postboksadresse;

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostboksadresseDTO implements Serializable {
        private String postboks;
        private String postbokseier;
        private String postnummer;
    }

    @JsonIgnore
    public int countAdresser() {

        return count(getVegadresse()) + count(getUtenlandskAdresse()) + count(getPostboksadresse());
    }
}
