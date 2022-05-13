package no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DagpengevedtakDTO {

    private String rettighetKode;

    private String vedtaktypeKode;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate datoMottatt;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate beregningsdato;

    private String automatiseringsgrad;

    private String saksbehandlersTilleggsbegrunnelseForVedtaket;

    private Vedtaksperiode vedtaktsperiode;

    private TaptArbeidstid taptArbeidstid;

    private Dagpengeperiode dagpengeperiode;

    private GodkjenningerReellArbeidssoker godkjenningerReellArbeidssoker;

    private Beregningsregler beregningsregler;

    private BruktInntektsperiode bruktInntektsperiode;

    private List<Vilkaar> vilkaar;

    private List<KodeVerdi> reelArbeidssoker;

    private List<Barn> barnListe;

    private List<KodeVerdi> permittering;

    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public class Vedtaksperiode {
        @JsonFormat(pattern="yyyy-MM-dd")
        private LocalDate fom;
        @JsonFormat(pattern="yyyy-MM-dd")
        private LocalDate tom;
    }

    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public class TaptArbeidstid {
        private String anvendtRegelKode;
        private Double fastansattArbeidstid;
        private Double naavaerendeArbeidstid;
    }

    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public class Dagpengeperiode {
        private Integer antallUkerPermittering;
        @JsonFormat(pattern="yyyy-MM-dd")
        private LocalDate justertFomDato;
        private Integer antallUker;
        private String endringVentedagsteller;
        private String endringPeriodeteller;
        private String endringPermitteringsteller;
        private String begrunnelseTellerendring;
        private String nullstillPeriodeteller;
        private String nullstillPermitteringsteller;
    }

    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public class Vilkaar {
        private String kode;
        private String status;
    }

    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public class GodkjenningerReellArbeidssoker {
        private String godkjentLokalArbeidssoker;
        private String godkjentDeltidssoker;
        private String godkjentUtdanning;
    }

    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public class Beregningsregler {
        private String oppfyllerKravTilFangstOgFiske;
        private String harAvtjentVernepliktSiste3Av12Mnd;
    }

    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public class BruktInntektsperiode {
        private String forsteManed;
        private String sisteManed;
    }

    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public class KodeVerdi {
        private String kode;
        private String verdi;
    }

    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public class Barn {
        private List<KodeVerdi> barn;
    }

}
