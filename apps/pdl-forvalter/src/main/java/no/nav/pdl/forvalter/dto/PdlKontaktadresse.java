package no.nav.pdl.forvalter.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskAdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlKontaktadresse extends AdresseDTO {

    private VegadresseDTO vegadresse;
    private UtenlandskAdresseDTO utenlandskAdresse;
    private PostadresseIFrittFormat postadresseIFrittFormat;
    private Postboksadresse postboksadresse;
    private UtenlandskAdresseIFrittFormat utenlandskAdresseIFrittFormat;
    private VegadresseForPost vegadresseForPost;

    @Override
    @JsonIgnore
    public boolean isNorskAdresse() {
        return false;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Postboksadresse implements Serializable {
        private String postboks;
        private String postbokseier;
        private String postnummer;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UtenlandskAdresseIFrittFormat implements Serializable {

        private List<String> adresselinjer;
        private String byEllerStedsnavn;
        private String landkode;
        private String postkode;

        public List<String> getAdresselinjer() {
            if (isNull(adresselinjer)) {
                adresselinjer = new ArrayList<>();
            }
            return adresselinjer;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class VegadresseForPost implements Serializable {

        private String adressekode;
        private String adressenavn;
        private String adressetilleggsnavn;
        private String bruksenhetsnummer;
        private String husbokstav;
        private String husnummer;
        private String postnummer;
    }
}
