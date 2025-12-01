package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdressehistorikkDTO {

    private String miljoe;
    private TpsMeldingResponse status;
    private PersonData persondata;

    public boolean isOk() {

        return nonNull(status) && "OK".equals(status.getReturStatus());
    }

    public enum PersonStatus {
        ABNR,
        ADNR,
        BOSA,
        DØD,
        DØDD,
        FØDR,
        FOSV,
        UREG,
        UTPE,
        UTAN,
        UFUL,
        UTVA
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonData {
        private String Ident;
        private String identType;
        private List<PersonstatusType> personStatus;
        private List<BoAdresseType> bostedsAdresse;
        private List<PostAdresseType> postAdresse;
        private List<NavTilleggType> tilleggAdresseNAV;

        public List<PersonstatusType> getPersonStatus() {

            if (isNull(personStatus)) {
                personStatus = new ArrayList<>();
            }
            return personStatus;
        }

        public List<BoAdresseType> getBostedsAdresse() {

            if (isNull(bostedsAdresse)) {
                bostedsAdresse = new ArrayList<>();
            }
            return bostedsAdresse;
        }

        public List<PostAdresseType> getPostAdresse() {

            if (isNull(postAdresse)) {
                postAdresse = new ArrayList<>();
            }
            return postAdresse;
        }

        public List<NavTilleggType> getTilleggAdresseNAV() {

            if (isNull(tilleggAdresseNAV)) {
                tilleggAdresseNAV = new ArrayList<>();
            }
            return tilleggAdresseNAV;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonstatusType {

        protected LocalDate datoFom;
        protected LocalDate datoTom;
        protected PersonStatus kodePersonstatus;
        protected LocalDateTime tidspunktReg;
        protected String system;
        protected String saksbehandler;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoAdresseType {
        private LocalDate datoFom;
        private LocalDate datoTom;
        private String adresse1;
        private String adresse2;
        private String tilleggsAdresseSKD;
        private String kommunenr;
        private String kommuneNavn;
        private String bolignr;
        private String postnr;
        private String poststed;
        private String landKode;
        private String land;
        private String adresseType;
        private String beskrAdrType;
        private LOffAdrType offAdresse;
        private LMatrAdrType matrAdresse;
        private LocalDateTime tidspunktReg;
        private String system;
        private String saksbehandler;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostAdresseType {

        private LocalDate datoFom;
        private LocalDate datoTom;
        private String landKode;
        private String land;
        private String adresseType;
        private String beskrAdrType;
        private String adresse1;
        private String adresse2;
        private String adresse3;
        private String postnr;
        private String poststed;
        private LocalDateTime tidspunktReg;
        private String system;
        private String saksbehandler;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NavTilleggType {

        private LocalDate datoFom;
        private LocalDate datoTom;
        private String adresseType;
        private String beskrAdrType;
        private String typeAdresseNavNorge;
        private String beskrTypeAdresseNavNorge;
        private String typeTilleggsLinje;
        private String beskrTypeTilleggsLinje;
        private String tilleggsLinje;
        private String kommunenr;
        private String kommuneNavn;
        private String gatekode;
        private String gatenavn;
        private String husnr;
        private String husbokstav;
        private String eiendomsnavn;
        private String bolignr;
        private String postboksnr;
        private String postboksAnlegg;
        private String postnr;
        private String poststed;
        private String landKode;
        private String land;
        private String adresse1;
        private String adresse2;
        private String adresse3;
        private LocalDateTime tidspunktReg;
        private String system;
        private String saksbehandler;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LOffAdrType {

        private String gatekode;
        private String gateNavn;
        private String husnr;
        private String bokstav;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LMatrAdrType {

        protected String mellomAdresse;
        protected String gardsnr;
        protected String bruksnr;
        protected String festenr;
        protected String undernr;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class TpsMeldingResponse {

        private String returStatus;
        private String returMelding;
        private String utfyllendeMelding;
    }
}
