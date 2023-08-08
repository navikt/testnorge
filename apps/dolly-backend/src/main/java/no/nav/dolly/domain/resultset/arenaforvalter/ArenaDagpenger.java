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

    public enum VedtaksType {O, S, E, G}

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

    public static final List<Vilkaar> DAGPENGER_VILKAAR_STANS=
            List.of(
                    new ArenaDagpenger.Vilkaar("GEOMOB", "N"),
                    new ArenaDagpenger.Vilkaar("HELDELT", "N"),
                    new ArenaDagpenger.Vilkaar("IFAFP", "N"),
                    new ArenaDagpenger.Vilkaar("IFFODSP", "N"),
                    new ArenaDagpenger.Vilkaar("IFGAFISK", "N"),
                    new ArenaDagpenger.Vilkaar("IFSYKEP", "N"),
                    new ArenaDagpenger.Vilkaar("OATVIST", "N"),
                    new ArenaDagpenger.Vilkaar("PATVIST", "N"),
                    new ArenaDagpenger.Vilkaar("MEDLFOLKT", "N"),
                    new ArenaDagpenger.Vilkaar("MELDMØT", "N"),
                    new ArenaDagpenger.Vilkaar("ARBFØR", "N"),
                    new ArenaDagpenger.Vilkaar("ARBVILL", "N"),
                    new ArenaDagpenger.Vilkaar("INORGE", "N"),
                    new ArenaDagpenger.Vilkaar("TILTDELT", "N"),
                    new ArenaDagpenger.Vilkaar("UNDER67", "N"),
                    new ArenaDagpenger.Vilkaar("UNDERUTD", "N"),
                    new ArenaDagpenger.Vilkaar("UTESTENG", "N"),
                    new ArenaDagpenger.Vilkaar("IFUFTRY", "N"),
                    new ArenaDagpenger.Vilkaar("TAPTINNT", "N"),
                    new ArenaDagpenger.Vilkaar("MOTTATTDOK", "N")
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
        private VedtaksType vedtaktype;
        private String utfall;
        private LocalDate stansFomDato;
        private String harVedtaksbrev;
        private String harRedigerbartBrev;
        private String saksbehandlersTilleggsbegrunnelseForVedtaket;
        private String saksbehandler;

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