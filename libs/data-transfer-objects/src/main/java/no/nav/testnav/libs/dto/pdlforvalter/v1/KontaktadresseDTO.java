package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

import static java.util.Objects.nonNull;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class KontaktadresseDTO extends AdresseDTO {

    private VegadresseDTO vegadresse;
    private UtenlandskAdresseDTO utenlandskAdresse;
    private PostboksadresseDTO postboksadresse;
    private PostadresseIFrittFormatDTO postadresseIFrittFormat;
    private UtenlandskAdresseIFrittFormatDTO utenlandskAdresseIFrittFormat;

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

        return count(getVegadresse()) + count(getUtenlandskAdresse()) + count(getPostboksadresse())
                + count(getPostadresseIFrittFormat()) + count(getUtenlandskAdresseIFrittFormat());
    }

    @Override
    @JsonIgnore
    public boolean isAdresseNorge() {

        return nonNull(getVegadresse()) || nonNull(getPostboksadresse()) ||
                nonNull(getPostadresseIFrittFormat());
    }

    @Override
    @JsonIgnore
    public boolean isAdresseUtland() {

        return nonNull(getUtenlandskAdresse());
    }
}
