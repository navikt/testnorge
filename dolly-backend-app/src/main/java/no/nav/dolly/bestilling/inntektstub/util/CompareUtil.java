package no.nav.dolly.bestilling.inntektstub.util;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.experimental.UtilityClass;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;

@UtilityClass
public class CompareUtil {

    public static boolean isSubsetOf(List<Inntektsinformasjon> inntekterRequest, List<Inntektsinformasjon> eksisterendeInntekter) {

        boolean match = true;
        for (Inntektsinformasjon rsInntektsinformasjon : inntekterRequest) {
            match &= isMatch(rsInntektsinformasjon, eksisterendeInntekter);
            if (!match) {
                break;
            }
        }

        return match;
    }

    private static boolean isMatch(Inntektsinformasjon rsInntektsinformasjon, List<Inntektsinformasjon> eksisterendeInntekter) {

        boolean match = false;
        for (Inntektsinformasjon eksiterendeInntektInfo : eksisterendeInntekter) {
            if (eksiterendeInntektInfo.equals(rsInntektsinformasjon) &&
                    hasInntekter(rsInntektsinformasjon, eksiterendeInntektInfo) &&
                    hasOtherBusiness(rsInntektsinformasjon, eksiterendeInntektInfo)) {
                match = true;
                break;
            }
        }
        return match;
    }

    private static boolean hasOtherBusiness(Inntektsinformasjon inntektInfoRequest, Inntektsinformasjon eksisterendeInntektInfo) {

        return hasArbeidforhold(inntektInfoRequest, eksisterendeInntektInfo) &&
                hasForskuddstrekk(inntektInfoRequest, eksisterendeInntektInfo) &&
                hasFradrag(inntektInfoRequest, eksisterendeInntektInfo);
    }

    private static boolean hasInntekter(Inntektsinformasjon inntektInfoRequest, Inntektsinformasjon inntektsinformasjon) {

        AtomicBoolean match = new AtomicBoolean(true);
        inntektInfoRequest.getInntektsliste().forEach(rsInntekt -> {
            if (inntektsinformasjon.getInntektsliste().stream()
                    .noneMatch(inntekt -> inntekt.equals(rsInntekt))) {
                match.set(false);
            }
        });
        return match.get();
    }

    private static boolean hasArbeidforhold(Inntektsinformasjon inntektInfoRequest, Inntektsinformasjon inntektsinformasjon) {

        AtomicBoolean match = new AtomicBoolean(true);
        inntektInfoRequest.getArbeidsforholdsliste().forEach(rsArbeidsforhold -> {
            if (inntektsinformasjon.getArbeidsforholdsliste().stream()
                    .noneMatch(arbeidsforhold -> arbeidsforhold.equals(rsArbeidsforhold))) {
                match.set(false);
            }
        });
        return match.get();
    }

    private static boolean hasForskuddstrekk(Inntektsinformasjon inntektInfoRequest, Inntektsinformasjon inntektsinformasjon) {

        AtomicBoolean match = new AtomicBoolean(true);
        inntektInfoRequest.getForskuddstrekksliste().forEach(rsForskuddstrekk -> {
            if (inntektsinformasjon.getForskuddstrekksliste().stream()
                    .noneMatch(forskuddstrekk -> forskuddstrekk.equals(rsForskuddstrekk))) {
                match.set(false);
            }
        });
        return match.get();
    }

    private static boolean hasFradrag(Inntektsinformasjon inntektInfoRequest, Inntektsinformasjon inntektsinformasjon) {

        AtomicBoolean match = new AtomicBoolean(true);
        inntektInfoRequest.getFradragsliste().forEach(rsFradrag -> {
            if (inntektsinformasjon.getFradragsliste().stream()
                    .noneMatch(fradrag -> fradrag.equals(rsFradrag))) {
                match.set(false);
            }
        });
        return match.get();
    }
}
