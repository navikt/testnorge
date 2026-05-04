package no.nav.dolly.synt.dagpenger.model;

import no.nav.dolly.synt.dagpenger.dto.BarnListeDto;
import no.nav.dolly.synt.dagpenger.dto.BeregningsreglerDto;
import no.nav.dolly.synt.dagpenger.dto.DagpengeperiodeDto;
import no.nav.dolly.synt.dagpenger.dto.DagpengevedtakDto;
import no.nav.dolly.synt.dagpenger.dto.GodkjenningerReellArbeidssokerDto;
import no.nav.dolly.synt.dagpenger.dto.InntektsperiodeDto;
import no.nav.dolly.synt.dagpenger.dto.KodeVerdiDto;
import no.nav.dolly.synt.dagpenger.dto.PeriodeDto;
import no.nav.dolly.synt.dagpenger.dto.TaptArbeidstidDto;
import no.nav.dolly.synt.dagpenger.dto.VilkaarDto;
import no.nav.dolly.synt.dagpenger.onnx.MinimalDagpengevedtak;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

final class VedtakMapper {

    private VedtakMapper() {
    }

    static DagpengevedtakDto fromPrediction(MinimalDagpengevedtak prediction) {
        LocalDate fomDate = LocalDate.parse(prediction.datoMottatt());
        LocalDate tomDate = fomDate.plusWeeks(26);
        boolean isPerm = "PERM".equals(prediction.rettighetKode());

        Map<String, Double> probabilities = prediction.sannsynligheter();
        int antallUker = deriveAntallUker(probabilities);
        double naavaerendeArbeidstid = deriveNaavaerendeArbeidstid(probabilities, isPerm);

        return DagpengevedtakDto.builder()
                .rettighetKode(prediction.rettighetKode())
                .vedtaktypeKode(prediction.vedtaktypeKode())
                .datoMOttatt(prediction.datoMottatt())
                .beregningsdato(prediction.beregningsdato())
                .automatiseringsgrad("HELT_AUTOMATISK")
                .begrunnelse(prediction.predikertLabel())
                .vedtaksperiode(PeriodeDto.builder()
                        .fom(prediction.datoMottatt())
                        .tom(tomDate.toString())
                        .build())
                .taptArbeidstid(TaptArbeidstidDto.builder()
                        .anvendtRegelKode("STANDARD")
                        .fastsattArbeidstid(37.5)
                        .naavaerendeArbeidstid(naavaerendeArbeidstid)
                        .build())
                .dagpengeperiode(DagpengeperiodeDto.builder()
                        .antallUkerPermittering(isPerm ? antallUker : 0)
                        .justertFomDato(prediction.datoMottatt())
                        .antallUker(antallUker)
                        .endringVentedagsteller("N")
                        .endringPeriodeteller("N")
                        .endringPermitteringsteller(isPerm ? "J" : "N")
                        .begrunnelseTellerendring(prediction.predikertLabel())
                        .nullstillPeriodeteller("N")
                        .nullstillPermitteringsteller("N")
                        .build())
                .godkjenningerReellArbeidssoker(GodkjenningerReellArbeidssokerDto.builder()
                        .godkjentLokalArbeidssoker("J")
                        .godkjentDeltidssoker(naavaerendeArbeidstid > 0 ? "J" : "N")
                        .godkjentUtdanning("N")
                        .build())
                .beregningsregler(BeregningsreglerDto.builder()
                        .oppfyllerKravTilFangstOgFiske("N")
                        .harAvtjentVernepliktSiste3Av12Mnd("N")
                        .build())
                .bruktInntektsperiode(InntektsperiodeDto.builder()
                        .forsteManed(fomDate.minusMonths(12).withDayOfMonth(1).toString())
                        .sisteManed(fomDate.minusMonths(1).withDayOfMonth(1).toString())
                        .build())
                .vilkaar(List.of(VilkaarDto.builder().kode("MINSTEINNTEKT").status("OPPFYLT").build()))
                .reellArbeidssoker(List.of(KodeVerdiDto.builder().kode("ARBSOKER").verdi("J").build()))
                .barnListe(List.of(BarnListeDto.builder()
                        .barn(List.of(KodeVerdiDto.builder().kode("ANTALL").verdi("0").build()))
                        .build()))
                .permittering(List.of(KodeVerdiDto.builder()
                        .kode("PERMITTERT")
                        .verdi(isPerm ? "J" : "N")
                        .build()))
                .build();
    }

    private static int deriveAntallUker(Map<String, Double> probabilities) {
        double highestProbability = probabilities.values().stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.5);
        return (int) Math.max(1, Math.round(10 + (highestProbability * 42)));
    }

    private static double deriveNaavaerendeArbeidstid(Map<String, Double> probabilities, boolean isPerm) {
        double highestProbability = probabilities.values().stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.5);
        if (isPerm) {
            return Math.round((37.5 - (highestProbability * 37.5)) * 10.0) / 10.0;
        }
        return 0.0;
    }
}

