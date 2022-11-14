package no.nav.dolly.domain.resultset.arenaforvalter;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArenaDagpenger {

    public static final List<Vilkaar> DAGPENGER_VILKAAR =
            List.of(
                    new ArenaDagpenger.Vilkaar("GEOMOB", "J"),
                    new ArenaDagpenger.Vilkaar("HELDELT", "J"),
                    new ArenaDagpenger.Vilkaar("IFAFP", "J"),
                    new ArenaDagpenger.Vilkaar("IFFODSP", "J"),
                    new ArenaDagpenger.Vilkaar("IFGAFISK", "J"),
                    new ArenaDagpenger.Vilkaar("IFSYKEP", "J"),
                    new ArenaDagpenger.Vilkaar("OATVIST", "J"),
                    new ArenaDagpenger.Vilkaar("PATVIST", "J"),
                    new ArenaDagpenger.Vilkaar("MEDLFOLKT", "J"),
                    new ArenaDagpenger.Vilkaar("MELDMØT", "J"),
                    new ArenaDagpenger.Vilkaar("ARBFØR", "J"),
                    new ArenaDagpenger.Vilkaar("ARBVILL", "J"),
                    new ArenaDagpenger.Vilkaar("INORGE", "J"),
                    new ArenaDagpenger.Vilkaar("TILTDELT", "J"),
                    new ArenaDagpenger.Vilkaar("UNDER67", "J"),
                    new ArenaDagpenger.Vilkaar("UNDERUTD", "J"),
                    new ArenaDagpenger.Vilkaar("UTESTENG", "J"),
                    new ArenaDagpenger.Vilkaar("IFUFTRY", "J"),
                    new ArenaDagpenger.Vilkaar("TAPTINNT", "J"),
                    new ArenaDagpenger.Vilkaar("MOTTATTDOK", "J")
            );

    private String personident;
    private String miljoe;
    private List<NyeDagp> nyeDagp;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NyeDagp {

        private String rettighetKode;
        private LocalDate datoMottatt;
        private Dagpengeperiode dagpengeperiode;
        private GodkjenningerReellArbeidssoker godkjenningerReellArbeidssoker;
        private TaptArbeidstid taptArbeidstid;
        private Vedtaksperiode vedtaksperiode;
        private String vedtaktype;
        private String utfall;

        public List<Vilkaar> getVilkaar() {
            if (isNull(vilkaar)) {
                vilkaar = new ArrayList<>();
            }
            return vilkaar;
        }

        private List<Vilkaar> vilkaar;
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
    public static class Dagpengeperiode {

        private String nullstillPeriodeteller;
        private String nullstillPermitteringsteller;
        private String nullstillPermitteringstellerFisk;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Vedtaksperiode {
        private LocalDate fom;
        private LocalDate tom;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaptArbeidstid {

        private String anvendtRegelKode;
        private Integer fastsattArbeidstid;
        private Integer naavaerendeArbeidstid;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GodkjenningerReellArbeidssoker {
        private String godkjentDeltidssoker;
        private String godkjentLokalArbeidssoker;
        private String godkjentUtdanning;
    }

    public List<NyeDagp> getNyeDagp() {
        if (isNull(nyeDagp)) {
            nyeDagp = new ArrayList<>();
        }
        return nyeDagp;
    }
}