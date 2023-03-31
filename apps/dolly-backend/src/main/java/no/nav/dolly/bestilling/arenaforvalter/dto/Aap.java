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
@Schema(description = "AAP-rettigheter for brukeren")
public class Aap {

    public enum VedtakType {O, E, G, S}

    private String aktivitetsfase;
    private LocalDate datoMottatt;
    @Schema(description = "O = ny rettighet, S = sletting")
    private VedtakType vedtaktype;
    private SaksopplysningV1 genSaksopplysninger;
    private MedlemFolketrygdenV1 medlemFolketrygden;
    private AndreOkonomYtelser andreOkonomYtelserListe;
    private SaksopplysningV1 institusjonsopphold;
    private LocalDate fraDato;
    private LocalDate justertFra;
    private Vilkaar vilkaar;
    private String utfall;
    private LocalDate tilDato;
    private PeriodeAAP periode;
    private String vedtaksvariant;
    private String begrunnelse;
    private String utskrift;
    private String avbruddKode;
    private String saksbehandler;
    private String beslutter;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaksopplysningV1 {
        private String kode;
        private String overrdnet;
        private String verdi;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedlemFolketrygdenV1 {
        private String kode;
        private String verdi;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnnenOkonomYtelse {
        private String kode;
        private String verdi;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AndreOkonomYtelser {
        private AnnenOkonomYtelse annenOkonomYtelse;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Vilkaar {
        private String kode;
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodeAAP {
        private String periodeKode;
        private Integer endringPeriodeTeller;
        private String endringPeriodeBegrunnelse;
        private String nullstill;
        private Integer endringUnntakTeller;
        private String endringUnntakBegrunnelse;
    }
}
