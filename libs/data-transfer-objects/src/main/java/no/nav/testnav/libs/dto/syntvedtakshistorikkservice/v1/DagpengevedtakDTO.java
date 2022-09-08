package no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger.Barn;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger.Beregningsregler;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger.Dagpengerettighet;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger.BruktInntektsperiode;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger.Dagpengeperiode;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger.Vedtaksperiode;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger.TaptArbeidstid;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger.GodkjenningerReellArbeidssoker;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger.Vilkaar;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger.KodeVerdi;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DagpengevedtakDTO {

    private Integer sakId;
    private Integer oppgaveId;

    private Dagpengerettighet rettighetKode;

    private String vedtaktypeKode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate datoMottatt;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate beregningsdato;

    private String automatiseringsgrad;

    private String saksbehandlersTilleggsbegrunnelseForVedtaket;

    private Vedtaksperiode vedtaksperiode;

    private TaptArbeidstid taptArbeidstid;

    private Dagpengeperiode dagpengeperiode;

    private GodkjenningerReellArbeidssoker godkjenningerReellArbeidssoker;

    private Beregningsregler beregningsregler;

    private BruktInntektsperiode bruktInntektsperiode;

    private List<Vilkaar> vilkaar;

    private List<KodeVerdi> reellArbeidssoker;

    private List<Barn> barnListe;

    private List<KodeVerdi> permittering;
}
