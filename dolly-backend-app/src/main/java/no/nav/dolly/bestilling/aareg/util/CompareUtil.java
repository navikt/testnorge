package no.nav.dolly.bestilling.aareg.util;

import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.builder.EqualsBuilder;

import lombok.experimental.UtilityClass;
import no.nav.dolly.bestilling.aareg.domain.Arbeidsforhold;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdResponse;
import no.nav.dolly.domain.resultset.aareg.RsAntallTimerIPerioden;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsavtale;
import no.nav.dolly.util.NullcheckUtil;

@UtilityClass
public class CompareUtil {

    public List<Arbeidsforhold> filterArbeidforhold(List<Arbeidsforhold> request, List<ArbeidsforholdResponse> eksisterende) {

    }

    private boolean isEqual(Arbeidsforhold request, ArbeidsforholdResponse exist) {

        return new EqualsBuilder()
                .append(request.getAnsettelsesPeriode().getFom(), exist.getAnsettelsesperiode().getPeriode().getFom())
                .append(request.getAnsettelsesPeriode().getTom(), exist.getAnsettelsesperiode().getPeriode().getTom())
                .append(request.getArbeidsforholdstype(), exist.getType())
                .appendSuper(request.getAntallTimerForTimeloennet().stream()
                        .allMatch(rsAntallTimerIPerioden ->
                        exist.getAntallTimerForTimeloennet().stream()
                                .anyMatch(eksisterende -> isEqual(rsAntallTimerIPerioden, eksisterende))))
                .append(request.getArbeidsavtale(), exist.getArbeidsavtaler())
//                .append(getArbeidsforholdId(), exist.getArbeidsforholdId())
//                .append(getArbeidsgiver(), exist.getArbeidsgiver())
//                .append(getArbeidstaker(), exist.getArbeidstaker())
//                .append(getNavArbeidsforholdId(), exist.getNavArbeidsforholdId())
//                .append(getOpplysningspliktig(), exist.getOpplysningspliktig())
//                .append(getPermisjonPermitteringer(), exist.getPermisjonPermitteringer())
//                .append(getRegistrert(), exist.getRegistrert())
//                .append(getSistBekreftet(), exist.getSistBekreftet())
//                .append(getType(), exist.getType())
//                .append(getUtenlandsopphold(), exist.getUtenlandsopphold())
                .isEquals();
    }

}

    private boolean isEqual(RsArbeidsavtale request, ArbeidsforholdResponse.Arbeidsavtale exist) {

        return new EqualsBuilder()
                .append(request.getAvtaltArbeidstimerPerUke(), exist.getBeregnetAntallTimerPrUke())  // må testes
                .append(request.getArbeidstidsordning(), exist.getArbeidstidsordning())
                .append(request.getAntallKonverterteTimer(), exist.getAntallTimerPrUke())  // må testes
                .append(Optional.of(request.getStillingsprosent().doubleValue()), exist.getStillingsprosent())
                .append(request.getYrke(), exist.getYrke())
                .append(request.getEndringsdatoStillingsprosent().toLocalDate(), exist.getSistStillingsendring()) // må testes
                .isEquals();
    }

    private boolean isEqual(RsAntallTimerIPerioden request, ArbeidsforholdResponse.AntallTimerForTimeloennet exist) {

        return new EqualsBuilder()
                .append(Optional.of(request.getAntallTimer().doubleValue()), exist.getAntallTimer())
                .append(request.getPeriode().getFom(), exist.getPeriode().getFom())
                .append(request.getPeriode().getTom(), exist.getPeriode().getTom())
                .isEquals();
    }
}
