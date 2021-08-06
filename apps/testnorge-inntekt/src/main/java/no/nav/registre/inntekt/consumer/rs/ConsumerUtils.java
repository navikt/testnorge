package no.nav.registre.inntekt.consumer.rs;

import static no.nav.registre.inntekt.utils.DatoParser.hentMaanedsnummerFraMaanedsnavn;
import static no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.Inntektstype.LOENNSINNTEKT;
import static no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.Inntektstype.NAERINGSINNTEKT;
import static no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.Inntektstype.PENSJON_ELLER_TRYGD;
import static no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.Inntektstype.YTELSE_FRA_OFFENTLIGE;

import no.nav.registre.inntekt.domain.inntektstub.RsInntekt;
import no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.Inntekt;
import no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.Inntektsinformasjon;
import no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.Inntektstype;
import no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.Tilleggsinformasjon;
import no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.tilleggsinformasjon.AldersUfoereEtterlatteAvtalefestetOgKrigspensjon;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConsumerUtils {

    private static final String KONTANTYTELSE = "kontantytelse";

    private ConsumerUtils() {

    }

    public static final String CALL_ID_NAME = "Nav-Call-Id";
    public static final String CONSUMER_ID_NAME = "Nav-Consumer-Id";
    public static final String NAV_CALL_ID = "ORKESTRATOREN";
    public static final String NAV_CONSUMER_ID = "ORKESTRATOREN";

    public static Inntektstype mapInntektstypeFromSyntToInntekt(String inntektstype) {
        if ("PensjonEllerTrygd".equals(inntektstype)) {
            return PENSJON_ELLER_TRYGD;
        } else if ("Loennsinntekt".equals(inntektstype)) {
            return LOENNSINNTEKT;
        } else if ("YtelseFraOffentlige".equals(inntektstype)) {
            return YTELSE_FRA_OFFENTLIGE;
        } else if ("Naeringsinntekt".equals(inntektstype)) {
            return NAERINGSINNTEKT;
        }
        throw new IllegalArgumentException("Kunne ikke finne inntektstype " + inntektstype);
    }

    public static void setBeskrivelseOgFordel(Inntekt inntekt) {
        if (LOENNSINNTEKT == inntekt.getInntektstype()) {
            inntekt.setBeskrivelse("fastloenn");
            inntekt.setFordel(KONTANTYTELSE);
            inntekt.setUtloeserArbeidsgiveravgift(true);
            inntekt.setInngaarIGrunnlagForTrekk(true);
        } else if (YTELSE_FRA_OFFENTLIGE == inntekt.getInntektstype()) {
            inntekt.setBeskrivelse("arbeidsavklaringspenger");
            inntekt.setFordel(KONTANTYTELSE);
            inntekt.setUtloeserArbeidsgiveravgift(false);
            inntekt.setInngaarIGrunnlagForTrekk(true);
        } else if (PENSJON_ELLER_TRYGD == inntekt.getInntektstype()) {
            int pensjonsgrad = 80;
            double grunnpensjon = (pensjonsgrad / 100.0) * inntekt.getBeloep();
            double tillegspensjon = inntekt.getBeloep() - grunnpensjon;
            inntekt.setBeskrivelse("alderspensjon");
            inntekt.setFordel(KONTANTYTELSE);
            inntekt.setUtloeserArbeidsgiveravgift(false);
            inntekt.setInngaarIGrunnlagForTrekk(true);
            var tilleggsinformasjon = new Tilleggsinformasjon(AldersUfoereEtterlatteAvtalefestetOgKrigspensjon.builder()
                    .grunnpensjonsbeloep(grunnpensjon)
                    .tilleggspensjonsbeloep(tillegspensjon)
                    .pensjonsgrad(pensjonsgrad)
                    .build());
            inntekt.setTilleggsinformasjon(tilleggsinformasjon);
        } else if (NAERINGSINNTEKT == inntekt.getInntektstype()) {
            inntekt.setBeskrivelse("sykepenger");
            inntekt.setFordel(KONTANTYTELSE);
            inntekt.setUtloeserArbeidsgiveravgift(false);
            inntekt.setInngaarIGrunnlagForTrekk(false);
        } else {
            throw new IllegalArgumentException("Kunne ikke finne inntektstype " + inntekt);
        }
    }

    public static Map<String, Map<String, Inntektsinformasjon>> buildInntektsliste(List<RsInntekt> rsInntekter) {
        Map<String, Map<String, Inntektsinformasjon>> periodeVirksomhetInntekterMap = new HashMap<>();
        for (var rsInntekt : rsInntekter) {
            var aarMaaned = YearMonth.of(Integer.parseInt(rsInntekt.getAar()), Month.of(hentMaanedsnummerFraMaanedsnavn(rsInntekt.getMaaned()))).toString();
            var virksomhet = rsInntekt.getVirksomhet();
            if (periodeVirksomhetInntekterMap.containsKey(aarMaaned)) {
                var virksomhetInntekterMap = periodeVirksomhetInntekterMap.get(aarMaaned);
                if (virksomhetInntekterMap.containsKey(virksomhet)) {
                    var inntekter = virksomhetInntekterMap.computeIfAbsent(virksomhet, k -> Inntektsinformasjon.builder()
                            .opplysningspliktig(rsInntekt.getOpplysningspliktig())
                            .build());
                    inntekter.getInntektsliste().add(buildInntektFromSyntInntekt(rsInntekt));
                } else {
                    virksomhetInntekterMap.put(virksomhet, buildInntektsinformasjonFromSyntInntekt(rsInntekt));
                }
            } else {
                Map<String, Inntektsinformasjon> virksomhetInntekterMap = new HashMap<>();
                virksomhetInntekterMap.put(virksomhet, buildInntektsinformasjonFromSyntInntekt(rsInntekt));
                periodeVirksomhetInntekterMap.put(aarMaaned, virksomhetInntekterMap);
            }
        }
        return periodeVirksomhetInntekterMap;
    }

    private static Inntektsinformasjon buildInntektsinformasjonFromSyntInntekt(RsInntekt rsInntekt) {
        return Inntektsinformasjon.builder()
                .opplysningspliktig(rsInntekt.getOpplysningspliktig())
                .inntektsliste(new ArrayList<>(Collections.singletonList(buildInntektFromSyntInntekt(rsInntekt))))
                .build();
    }

    private static Inntekt buildInntektFromSyntInntekt(RsInntekt rsInntekt) {
        return Inntekt.builder()
                .inntektstype(mapInntektstypeFromSyntToInntekt(rsInntekt.getInntektstype()))
                .utloeserArbeidsgiveravgift(rsInntekt.getUtloeserArbeidsgiveravgift())
                .inngaarIGrunnlagForTrekk(rsInntekt.getInngaarIGrunnlagForTrekk())
                .beloep(rsInntekt.getBeloep())
                .beskrivelse(rsInntekt.getBeskrivelse())
                .fordel(rsInntekt.getFordel())
                .build();
    }
}
