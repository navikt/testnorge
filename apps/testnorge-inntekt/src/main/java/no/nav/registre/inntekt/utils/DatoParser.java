package no.nav.registre.inntekt.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import no.nav.registre.inntekt.domain.inntektstub.RsInntekt;

public class DatoParser {

    private DatoParser() {

    }

    private static final List<String> MAANEDER = Arrays.asList("januar", "februar", "mars", "april", "mai", "juni", "juli", "august", "september", "oktober", "november", "desember");

    public static List<RsInntekt> finnSenesteInntekter(List<RsInntekt> inntekter) {
        var senesteAar = 0;
        var senesteMaaned = 0;
        List<RsInntekt> senesteInntekter = new ArrayList<>();
        for (var inntekt : inntekter) {
            var inntektsAar = Integer.parseInt(inntekt.getAar());
            var inntektsMaaned = MAANEDER.indexOf(inntekt.getMaaned());
            if (inntektsAar == senesteAar) {
                if (inntektsMaaned == senesteMaaned) { // funnet ny inntekt på samme tidspunkt
                    senesteInntekter.add(inntekt);
                }
                if (inntektsMaaned > senesteMaaned) { // funnet ny høyeste pga måned
                    senesteMaaned = inntektsMaaned;
                    senesteInntekter.clear();
                    senesteInntekter.add(inntekt);
                }
            } else if (inntektsAar > senesteAar) { // funnet ny høyeste pga år
                senesteAar = inntektsAar;
                senesteMaaned = inntektsMaaned;
                senesteInntekter.clear();
                senesteInntekter.add(inntekt);
            }
        }
        return senesteInntekter;
    }

    public static int hentMaanedsnummerFraMaanedsnavn(String maaned) {
        if (!new HashSet<>(MAANEDER).contains(maaned)) {
            throw new IllegalArgumentException("Ugyldig maaned " + maaned);
        }

        return MAANEDER.indexOf(maaned) + 1;
    }

    public static String hentMaanedsnavnFraMaanedsnummer(int maaned) {
        if (maaned < 1 || maaned > MAANEDER.size()) {
            throw new IllegalArgumentException("Ugyldig maaned " + maaned);
        }

        return MAANEDER.get(maaned - 1);
    }
}
