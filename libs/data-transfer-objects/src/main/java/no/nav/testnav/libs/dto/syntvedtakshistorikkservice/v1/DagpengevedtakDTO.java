package no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DagpengevedtakDTO {

    private Integer sakId;
    private Integer oppgaveId;

    @JsonProperty
    private Dagpengerettighet rettighetKode;

    @JsonProperty
    private String vedtaktypeKode;

    @JsonProperty
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate datoMottatt;

    @JsonProperty
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate beregningsdato;

    @JsonProperty
    private String automatiseringsgrad;

    @JsonProperty
    private String saksbehandlersTilleggsbegrunnelseForVedtaket;

    @JsonProperty
    private Vedtaksperiode vedtaksperiode;

    @JsonProperty
    private TaptArbeidstid taptArbeidstid;

    @JsonProperty
    private Dagpengeperiode dagpengeperiode;

    @JsonProperty
    private GodkjenningerReellArbeidssoker godkjenningerReellArbeidssoker;

    @JsonProperty
    private Beregningsregler beregningsregler;

    @JsonProperty
    private BruktInntektsperiode bruktInntektsperiode;

    @JsonProperty
    private List<Vilkaar> vilkaar;

    @JsonProperty
    private List<KodeVerdi> reelArbeidssoker;

    @JsonProperty
    private List<Barn> barnListe;

    @JsonProperty
    private List<KodeVerdi> permittering;
}
