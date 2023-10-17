package no.nav.dolly.domain.resultset.arenaforvalter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AAP-rettigheter for brukeren")
public class RsArenaAap extends ArenaPeriode{

    public enum VedtakType {O, E, G, S}

    private String aktivitetsfase;
    private LocalDateTime datoMottatt;
    @Schema(description = "O = ny rettighet, S = sletting")
    private VedtakType vedtaktype;
    private List<Saksopplysning> genSaksopplysninger;
    private MedlemFolketrygden medlemFolketrygden;
    private List<AndreOkonomYtelser> andreOkonomYtelserListe;
    private List<Saksopplysning> institusjonsopphold;
    private LocalDateTime justertFra;
    private List<Vilkaar> vilkaar;
    private String utfall;
    private PeriodeAAP periode;
    private String vedtaksvariant;
    private String begrunnelse;
    private String utskrift;
    private String avbruddKode;
    private String saksbehandler;
    private String beslutter;

    public List<Saksopplysning> getGenSaksopplysninger() {
        if (isNull(genSaksopplysninger)) {
            genSaksopplysninger = new ArrayList<>();
        }
        return genSaksopplysninger;
    }

    public List<AndreOkonomYtelser> getAndreOkonomYtelserListe() {
        if (isNull(andreOkonomYtelserListe)) {
            andreOkonomYtelserListe = new ArrayList<>();
        }
        return andreOkonomYtelserListe;
    }

    public List<Saksopplysning> getInstitusjonsopphold() {
        if (isNull(institusjonsopphold)) {
            institusjonsopphold = new ArrayList<>();
        }
        return institusjonsopphold;
    }

    public List<Vilkaar> getVilkaar() {
        if (isNull(vilkaar)) {
            vilkaar = new ArrayList<>();
        }
        return vilkaar;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Saksopplysning {
        private String kode;
        private String overordnet;
        private String verdi;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedlemFolketrygden {
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
