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
import java.util.List;

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
    private PostadresseIFrittFormat postadresseIFrittFormat;
    private UtenlandskAdresseIFrittFormat utenlandskAdresseIFrittFormat;

    @Data
    @Builder
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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Deprecated
    public static class PostadresseIFrittFormat implements Serializable {

        private List<String> adresselinjer;
        private String postnummer;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Deprecated
    public static class UtenlandskAdresseIFrittFormat implements Serializable {

        private List<String> adresselinjer;
        private String postkode;
        private String byEllerStedsnavn;
        private String landkode;
    }
}
