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
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

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
    /**
     * @Deprecated Denne benyttes kun for import av SKD-meldinger fra TPS-Forvalteren
     */
    public static class PostadresseIFrittFormat implements Serializable {

        private List<String> adresselinjer;
        private String postnummer;

        public List<String> getAdresselinjer() {
            if (isNull(adresselinjer)) {
                adresselinjer = new ArrayList<>();
            }
            return adresselinjer;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Deprecated
    /**
     * @Deprecated Denne benyttes kun for import av SKD-meldinger fra TPS-Forvalteren
     */
    public static class UtenlandskAdresseIFrittFormat implements Serializable {

        private List<String> adresselinjer;
        private String postkode;
        private String byEllerStedsnavn;
        private String landkode;

        public List<String> getAdresselinjer() {
            if (isNull(adresselinjer)) {
                adresselinjer = new ArrayList<>();
            }
            return adresselinjer;
        }
    }

    @Override
    @JsonIgnore
    public boolean isAdresseNorge() {

        return nonNull(getVegadresse()) || nonNull(getPostboksadresse()) ||
                nonNull(getPostadresseIFrittFormat());
    }
}
