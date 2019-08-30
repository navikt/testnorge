package no.nav.registre.inntekt.testUtils;

import no.nav.registre.inntekt.domain.RsInntekt;
import no.nav.registre.inntekt.domain.RsInntektsinformasjonsType;

public class InntektGenerator {

    public static RsInntekt genererInntekt(double beloep) {
        RsInntekt inntektEntry = new RsInntekt();
        inntektEntry.setAar("2019");
        inntektEntry.setBeloep(beloep);
        inntektEntry.setInntektsinformasjonsType(RsInntektsinformasjonsType.INNTEKT);
        inntektEntry.setInngaarIGrunnlagForTrekk(true);
        inntektEntry.setInntektstype("YtelseFraOffentlige");
        inntektEntry.setMaaned("januar");
        inntektEntry.setUtloeserArbeidsgiveravgift(true);
        inntektEntry.setVirksomhet("12379571");
        return inntektEntry;
    }

}
