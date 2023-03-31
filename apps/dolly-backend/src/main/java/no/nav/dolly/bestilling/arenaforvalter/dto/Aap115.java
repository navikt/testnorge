package no.nav.dolly.bestilling.arenaforvalter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AAP-§115-rettigheter for brukeren")
public class Aap115 {

    public enum VedtaksType {O, S}

    private LocalDate fraDato;
    private LocalDate tilDato;
    private LocalDate datoMottatt;

    @Schema(description = "Gyldige verdier: O (ny), S (stans)")
    private VedtaksType vedtaktype;
    private MedisinskOpplysningV1 medisinskOpplysning;
    private Vilkaar vilkaar;
    private String utfall;
    private String begrunnelse;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedisinskOpplysningV1 {

        private String type;
        private String klassifisering;
        private String diagnose;
        private String kilde;
        private LocalDate kildeDato;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Vilkaar {

        private String kode;
        private String status;
    }
}