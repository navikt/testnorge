package no.nav.pdl.forvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeltBostedDTO extends DbVersjonDTO {

    private String adresseIdentifikatorFraMatrikkelen;
    private String coAdressenavn;
    private LocalDateTime sluttdatoForKontrakt;
    private LocalDateTime startdatoForKontrakt;
    private VegadresseDTO vegadresse;
    private UkjentBostedDTO ukjentBosted;
    private MatrikkeladresseDTO matrikkeladresse;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UkjentBostedDTO implements Serializable {

        private String bostedskommune;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PdlDelteBosteder implements Serializable {

        private List<DeltBostedDTO> delteBosteder;

        public List<DeltBostedDTO> getDelteBosteder() {
            if (isNull(delteBosteder)) {
                delteBosteder = new ArrayList<>();
            }
            return delteBosteder;
        }
    }
}
