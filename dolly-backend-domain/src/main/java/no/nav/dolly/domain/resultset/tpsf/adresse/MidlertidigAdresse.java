package no.nav.dolly.domain.resultset.tpsf.adresse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "adressetype")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MidlertidigAdresse.MidlertidigGateAdresse.class, name = "GATE"),
        @JsonSubTypes.Type(value = MidlertidigAdresse.MidlertidigStedAdresse.class, name = "STED"),
        @JsonSubTypes.Type(value = MidlertidigAdresse.MidlertidigPboxAdresse.class, name = "PBOX"),
        @JsonSubTypes.Type(value = MidlertidigAdresse.MidlertidigUtadAdresse.class, name = "UTAD")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class MidlertidigAdresse {

    public enum MidlertidigAdressetype {
        PBOX,
        GATE,
        STED,
        UTAD
    }

    private Long id;
    private LocalDateTime gyldigTom;
    private MidlertidigAdressetype adressetype;
    private String tilleggsadresse;
    private String postnr;

    public boolean isGateAdr() {
        return MidlertidigAdressetype.GATE == getAdressetype();
    }

    public boolean isPostBox() {
        return MidlertidigAdressetype.PBOX == getAdressetype();
    }

    public boolean isUtenlandsk() {
        return MidlertidigAdressetype.UTAD == getAdressetype();
    }

    public abstract MidlertidigAdressetype getAdressetype();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MidlertidigGateAdresse extends MidlertidigAdresse {

        private String gatenavn;
        private String gatekode;
        private String husnr;
        private String matrikkelId;

        @Override public MidlertidigAdressetype getAdressetype() {
            return MidlertidigAdressetype.GATE;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MidlertidigStedAdresse extends MidlertidigAdresse {

        private String eiendomsnavn;

        @Override public MidlertidigAdressetype getAdressetype() {
            return MidlertidigAdressetype.STED;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MidlertidigPboxAdresse extends MidlertidigAdresse {

        private String postboksnr;
        private String postboksAnlegg;

        @Override public MidlertidigAdressetype getAdressetype() {
            return MidlertidigAdressetype.PBOX;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MidlertidigUtadAdresse extends MidlertidigAdresse {

        private String postLinje1;
        private String postLinje2;
        private String postLinje3;
        private String postLand;

        @Override public MidlertidigAdressetype getAdressetype() {
            return MidlertidigAdressetype.UTAD;
        }
    }
}
