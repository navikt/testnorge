package no.nav.registre.testnorge.generersyntameldingservice.provider.response;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.testnorge.domain.dto.aareg.amelding.Arbeidsforhold;
import no.nav.registre.testnorge.domain.dto.aareg.amelding.Fartoey;
import no.nav.registre.testnorge.domain.dto.aareg.amelding.Inntekt;
import no.nav.registre.testnorge.domain.dto.aareg.amelding.Permisjon;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SyntAmeldingResponse {

    private String rapporteringsmaaned;
    private String arbeidsforholdType;
    private LocalDate startdato;
    private LocalDate sluttdato;
    private float antallTimerPerUkeSomEnFullStillingTilsvarer;
    private String yrke;
    private String arbeidstidsordning;
    private float stillingsprosent;
    private LocalDate sisteLoennsendringsdato;
    private LocalDate sisteDatoForStillingsprosentendring;
    private List<Permisjon> permisjoner;
    private Fartoey fartoey;
    private List<Inntekt> inntekter;

    public SyntAmeldingResponse(Arbeidsforhold arbeidsforhold){
        var enddate = arbeidsforhold.getSluttdato();

        this.rapporteringsmaaned = arbeidsforhold.getRapporteringsmaaned();
        this.antallTimerPerUkeSomEnFullStillingTilsvarer = arbeidsforhold.getAntallTimerPerUkeSomEnFullStillingTilsvarer();
        this.arbeidsforholdType = arbeidsforhold.getArbeidsforholdType();
        this.startdato = LocalDate.parse(arbeidsforhold.getStartdato());
        this.sluttdato = enddate == null || enddate.isEmpty() ? null : LocalDate.parse(enddate);
        this.antallTimerPerUkeSomEnFullStillingTilsvarer = arbeidsforhold.getAntallTimerPerUkeSomEnFullStillingTilsvarer();
        this.yrke = arbeidsforhold.getYrke();
        this.arbeidstidsordning = arbeidsforhold.getArbeidstidsordning();
        this.stillingsprosent = arbeidsforhold.getStillingsprosent();
        this.sisteLoennsendringsdato = LocalDate.parse(arbeidsforhold.getSisteLoennsendringsdato());
        this.sisteDatoForStillingsprosentendring = LocalDate.parse(arbeidsforhold.getSisteDatoForStillingsprosentendring());
        this.permisjoner = arbeidsforhold.getPermisjoner() == null ? Collections.emptyList() : arbeidsforhold.getPermisjoner();
        this.fartoey = arbeidsforhold.getFartoey();
        this.inntekter = arbeidsforhold.getInntekter() == null ? Collections.emptyList() : arbeidsforhold.getInntekter();
    }

}
