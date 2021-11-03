package no.nav.dolly.bestilling.pdlforvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlDeltBosted extends PdlOpplysning {

    private String adresseIdentifikatorFraMatrikkelen;
    private String coAdressenavn;
    private String naerAdresseIdentifikatorFraMatrikkelen;
    private LocalDate sluttdatoForKontrakt;
    private LocalDate startdatoForKontrakt;
    private PdlVegadresse vegadresse;
    private UkjentBosted ukjentBosted;
    private PdlMatrikkeladresse matrikkeladresse;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UkjentBosted {

        private String bostedskommune;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PdlDelteBosteder {

        private List<PdlDeltBosted> delteBosteder;

        public List<PdlDeltBosted> getDelteBosteder() {
            if (isNull(delteBosteder)) {
                delteBosteder = new ArrayList<>();
            }
            return delteBosteder;
        }
    }
}
