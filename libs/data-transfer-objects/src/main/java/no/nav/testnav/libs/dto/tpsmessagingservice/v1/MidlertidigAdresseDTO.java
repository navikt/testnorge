package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MidlertidigAdresseDTO {

    private LocalDateTime gyldigTom;

    private String tilleggsadresse;

    private String postnr;

    private String adressetype;

    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class MidlertidigGateAdresseDTO extends MidlertidigAdresseDTO {

        private String gatenavn;

        private String gatekode;

        private String husnr;

        private String matrikkelId;
    }

    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class MidlertidigStedAdresseDTO extends MidlertidigAdresseDTO {

        private String eiendomsnavn;
    }

    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class MidlertidigPboxAdresseDTO extends MidlertidigAdresseDTO {

        private String postboksnr;

        private String postboksAnlegg;
    }

    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class MidlertidigUtadAdresseDTO extends MidlertidigAdresseDTO {

        private String postLinje1;

        private String postLinje2;

        private String postLinje3;

        private String postLand;
    }
}
