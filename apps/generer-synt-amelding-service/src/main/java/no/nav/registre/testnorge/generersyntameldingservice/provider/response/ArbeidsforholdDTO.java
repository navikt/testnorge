package no.nav.registre.testnorge.generersyntameldingservice.provider.response;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.nav.registre.testnorge.domain.dto.aareg.amelding.Arbeidsforhold;
import no.nav.registre.testnorge.domain.dto.aareg.amelding.Fartoey;
import no.nav.registre.testnorge.domain.dto.aareg.amelding.Inntekt;
import no.nav.registre.testnorge.domain.dto.aareg.amelding.Permisjon;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ArbeidsforholdDTO {

    private String rapporteringsmaaned;
    private String arbeidsforholdType;
    private LocalDate startdato;
    private float antallTimerPerUkeSomEnFullStillingTilsvarer;
    private String yrke;
    private String arbeidstidsordning;
    private float stillingsprosent;
    private LocalDate sisteLoennsendringsdato;
    private LocalDate sisteDatoForStillingsprosentendring;
    private List<Permisjon> permisjoner;
    private Fartoey fartoey;
    private List<Inntekt> inntekter;

    public ArbeidsforholdDTO(Arbeidsforhold arbeidsforhold){
        this.rapporteringsmaaned = arbeidsforhold.getRapporteringsmaaned();
        this.arbeidsforholdType = arbeidsforhold.getArbeidsforholdType();
        this.startdato = LocalDate.parse(arbeidsforhold.getStartdato());
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
