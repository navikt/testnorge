package no.nav.registre.hodejegeren.mongodb.tpsStatusQuo.Kjerneinfo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BostedsAdresse {

    private String boAdresse2;
    private String boPoststed;
    private String boAdresse1;
    private String boPostnr;
    private FullBostedsAdresse fullBostedsAdresse;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FullBostedsAdresse {

        private String adresseType;
        private Map<String, String> offAdresse;
        private String kommunenr;
        private String landKode;
        private String adrTidspunktReg;
        private String datoFom;
        private String kommuneNavn;
        private String adresse1;
        private String tilleggsAdresseSKD;
        private String adresse2;
        private String tknr;
        private String poststed;
        private String bolignr;
        private String beskrAdrType;
        private String adrSaksbehandler;
        private String datoTom;
        @JsonIgnore
        private Map<String, String> matrAdresse;
        private String postnr;
        private String adrSystem;
        private String tkNavn;
        private String land;
    }
}
