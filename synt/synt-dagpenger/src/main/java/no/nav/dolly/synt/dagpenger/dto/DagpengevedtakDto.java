package no.nav.dolly.synt.dagpenger.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "Dagpengevedtak")
public class DagpengevedtakDto {

    private String rettighetKode;
    private String vedtaktypeKode;
    private String datoMOttatt;
    private String beregningsdato;
    private String automatiseringsgrad;
    private String begrunnelse;
    private PeriodeDto vedtaksperiode;
    private TaptArbeidstidDto taptArbeidstid;
    private DagpengeperiodeDto dagpengeperiode;
    private GodkjenningerReellArbeidssokerDto godkjenningerReellArbeidssoker;
    private BeregningsreglerDto beregningsregler;
    private InntektsperiodeDto bruktInntektsperiode;
    private List<VilkaarDto> vilkaar;
    private List<KodeVerdiDto> reellArbeidssoker;
    private List<BarnListeDto> barnListe;
    private List<KodeVerdiDto> permittering;
}


