package no.nav.registre.inntekt.testUtils;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;

import no.nav.registre.inntekt.domain.inntektstub.RsInntekt;
import no.nav.registre.inntekt.domain.inntektstub.RsInntektsinformasjonsType;
import no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.Inntekt;
import no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.Inntektsinformasjon;
import no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.Inntektstype;

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

    public static Inntektsinformasjon genererFeiletInntektsinformasjon(
            String ident,
            double beloep
    ) {
        Inntektsinformasjon inntektEntry = new Inntektsinformasjon();

        Inntekt inntekt = new Inntekt();
        inntekt.setBeloep(beloep);
        inntekt.setInngaarIGrunnlagForTrekk(true);
        inntekt.setUtloeserArbeidsgiveravgift(true);

        inntektEntry.setNorskIdent(ident);
        inntektEntry.setAarMaaned(YearMonth.of(2019, 1));
        inntekt.setInntektstype(Inntektstype.YTELSE_FRA_OFFENTLIGE);
        inntektEntry.setInntektsliste(new ArrayList<>(Collections.singletonList(inntekt)));
        inntektEntry.setVirksomhet("12379571");
        inntektEntry.setFeilmelding("En feilmelding");
        return inntektEntry;
    }
}
