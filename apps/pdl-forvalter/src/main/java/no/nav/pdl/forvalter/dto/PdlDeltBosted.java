package no.nav.pdl.forvalter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlDeltBosted {

    private String adresseIdentifikatorFraMatrikkelen;
    private String coAdressenavn;
    private Folkeregistermetadata folkeregistermetadata;
    private String kilde;
    private String naerAdresseIdentifikatorFraMatrikkelen;
    private LocalDate sluttdatoForKontrakt;
    private LocalDate startdatoForKontrakt;
    private PdlVegadresse vegadresse;
    private UkjentBosted ukjentBosted;
    private PdlMatrikkeladresse matrikkeladresse;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UkjentBosted {

        private String bostedskommune;
    }

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
