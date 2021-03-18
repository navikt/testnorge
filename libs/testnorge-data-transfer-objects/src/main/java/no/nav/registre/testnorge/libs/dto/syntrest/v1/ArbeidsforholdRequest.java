package no.nav.registre.testnorge.libs.dto.syntrest.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ArbeidsforholdRequest {
    private final String rapporteringsmaaned;
    private final String arbeidsforholdType;
    private final LocalDate startdato;
    private final String sluttdato;
    private final Float antallTimerPerUkeSomEnFullStillingTilsvarer;
    private final String yrke;
    private final String arbeidstidsordning;
    private final Float stillingsprosent;
    private final LocalDate sisteLoennsendringsdato;
    private final LocalDate sisteDatoForStillingsprosentendring;
    private final Float permisjonMedForeldrepenger;
    private final Float permittering;
    private final Float permisjon;
    private final Float permisjonVedMilitaertjeneste;
    private final Float velferdspermisjon;
    private final Float utdanningspermisjon;
    private final String historikk;
    private final Integer numEndringer;
    private final List<PermisjonDTO> permisjoner;
    private final FartoeyDTO fartoey;
    private final Integer antallInntekter;
    private final List<InntektDTO> inntekter;

    public ArbeidsforholdRequest(ArbeidsforholdRequest request) {
        this.rapporteringsmaaned = request.rapporteringsmaaned;
        this.arbeidsforholdType = request.arbeidsforholdType;
        this.startdato = request.startdato;
        this.sluttdato = request.sluttdato;
        this.antallTimerPerUkeSomEnFullStillingTilsvarer = request.antallTimerPerUkeSomEnFullStillingTilsvarer;
        this.yrke = request.yrke;
        this.arbeidstidsordning = request.arbeidstidsordning;
        this.stillingsprosent = request.stillingsprosent;
        this.sisteLoennsendringsdato = request.sisteLoennsendringsdato;
        this.sisteDatoForStillingsprosentendring = request.sisteDatoForStillingsprosentendring;
        this.permisjonMedForeldrepenger = request.permisjonMedForeldrepenger;
        this.permittering = request.permittering;
        this.permisjon = request.permisjon;
        this.permisjonVedMilitaertjeneste = request.permisjonVedMilitaertjeneste;
        this.velferdspermisjon = request.velferdspermisjon;
        this.utdanningspermisjon = request.utdanningspermisjon;
        this.historikk = request.historikk;
        this.numEndringer = request.numEndringer;
        this.permisjoner = request.permisjoner;
        this.fartoey = request.fartoey;
        this.antallInntekter = request.antallInntekter;
        this.inntekter = request.inntekter;
    }
}