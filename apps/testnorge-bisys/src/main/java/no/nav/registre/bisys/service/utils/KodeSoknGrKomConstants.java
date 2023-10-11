package no.nav.registre.bisys.service.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
public interface KodeSoknGrKomConstants {

    @NoFomDato
    String AVSKRIVNING = "AV";
    @NoFomDato
    String BARNEBORTFORING = "BF";
    String BIDRAG = "B";
    String BIDRAG_18_AAR = "18";
    String BIDRAG_18_AAR_INNKREVING = "II";
    String BIDRAG_18_AAR_TILLEGGSBIDRAG = "IG";
    String BIDRAG_18_AAR_TILLEGGSBIDRAG_INNKREVING = "IT";
    String BIDRAG_INNKREVING = "BI";
    String DIREKTE_OPPGJOR = "DO";
    String EKTEFELLEBIDRAG_INNKREVING = "EI";
    String EKTEFELLEBIDRAG = "EB";
    @NoFomDato
    String ERSTATNING = "ER";
    @NoFomDato
    String ETTERGIVELSE = "EG";
    @NoFomDato
    String FARSKAP = "FA";
    String FORSKUDD = "FO";
    String GEBYR = "GB";
    String INNKREVING = "IK";
    @NoFomDato
    String KUNNSKAP_BIOLOGISK_FAR = "FB";
    @NoFomDato
    String MORSKAP = "MO";
    String MOTREGNING = "MR";
    String OPPFOSTRINGSBIDRAG_INNKREVING = "OI";
    @NoFomDato
    String REFUSJON_BIDRAG = "RB";
    String SAKSOMKOSTNINGER = "SO";
    String SARTILSKUDD = "ST";
    String SARTILSKUDD_INNKREVING = "IS";
    @NoFomDato
    String TILBAKEKREVING_ETTERGIVELSE = "TE";
    @NoFomDato
    String TILBAKEKREVING = "TK";
    String TILLEGGSBIDRAG = "TB";
    String TILLEGGSBIDRAG_INNKREVING = "TI";

    public static final Map<String, String> koder = ImmutableMap.<String, String> builder()
            .put(AVSKRIVNING, "Avskrivning direkte betalt")
            .put(BARNEBORTFORING, "Barnebortføring")
            .put(BIDRAG, "Bidrag")
            .put(BIDRAG_18_AAR, "18 år")
            .put(BIDRAG_18_AAR_INNKREVING, "18 år, innkreving")
            .put(BIDRAG_18_AAR_TILLEGGSBIDRAG, "18 år, tilleggsb.")
            .put(BIDRAG_18_AAR_TILLEGGSBIDRAG_INNKREVING, "18 år, tilleggsb,innkreving")
            .put(BIDRAG_INNKREVING, "Bidrag,innkreving")
            .put(DIREKTE_OPPGJOR, "Direkte oppgjør")
            .put(EKTEFELLEBIDRAG_INNKREVING, "Ektefellebidrag m/innkr")
            .put(EKTEFELLEBIDRAG, "Ektefellebidrag u/innkr")
            .put(ERSTATNING, "Erstatning")
            .put(ETTERGIVELSE, "Ettergivelse")
            .put(FARSKAP, "Farskap")
            .put(FORSKUDD, "Forskudd")
            .put(GEBYR, "Gebyr")
            .put(INNKREVING, "Innkreving")
            .put(KUNNSKAP_BIOLOGISK_FAR, "Kunnskap om biologisk far")
            .put(MORSKAP, "Morskap")
            .put(MOTREGNING, "Motregning")
            .put(OPPFOSTRINGSBIDRAG_INNKREVING, "Oppfostringsbidrag,innkreving")
            .put(REFUSJON_BIDRAG, "Refusjon bidrag")
            .put(SARTILSKUDD, "Særtilskudd")
            .put(SARTILSKUDD_INNKREVING, "Særtilskudd,innkreving")
            .put(SAKSOMKOSTNINGER, "Saksomkostninger")
            .put(TILBAKEKREVING_ETTERGIVELSE, "Tilbakekr,ettergivelse")
            .put(TILBAKEKREVING, "Tilbakekreving")
            .put(TILLEGGSBIDRAG_INNKREVING, "Tilleggsbidrag,innkreving")
            .build();

    @Deprecated
    static List<String> kodeSoknGrKomList() {

        String[] kodeSoknGrKom = new String[] { AVSKRIVNING, BARNEBORTFORING, BIDRAG, BIDRAG_18_AAR, BIDRAG_18_AAR_INNKREVING,
                BIDRAG_18_AAR_TILLEGGSBIDRAG, BIDRAG_18_AAR_TILLEGGSBIDRAG_INNKREVING,
                BIDRAG_INNKREVING, DIREKTE_OPPGJOR, EKTEFELLEBIDRAG_INNKREVING, EKTEFELLEBIDRAG,
                ERSTATNING, ETTERGIVELSE, FARSKAP, FORSKUDD, GEBYR, INNKREVING, KUNNSKAP_BIOLOGISK_FAR,
                MORSKAP, MOTREGNING, OPPFOSTRINGSBIDRAG_INNKREVING, REFUSJON_BIDRAG, SAKSOMKOSTNINGER,
                SARTILSKUDD, SARTILSKUDD_INNKREVING, TILBAKEKREVING, TILBAKEKREVING_ETTERGIVELSE,
                TILLEGGSBIDRAG, TILLEGGSBIDRAG_INNKREVING };

        return Arrays.asList(kodeSoknGrKom);
    }

    @Deprecated
    static Map<String, String> dekodeMap() {
        return koder;
    }

    public static String dekode(String kode) {
        return koder.get(kode);
    }

    static List<String> supportedKodeSoknGrKom() {
        String[] kodeSoknGrKomInnkreving = new String[] { BIDRAG_18_AAR_INNKREVING, BIDRAG_INNKREVING,
                FORSKUDD, SARTILSKUDD_INNKREVING };

        List<String> supportedKodeSoknGrKomMedInnkreving = Arrays.asList(kodeSoknGrKomInnkreving);
        List<String> supportedKodeSoknGrKom = new ArrayList<>();

        Stream.of(supportedKodeSoknGrKomMedInnkreving, supportedKodeSoknGrKomUtenInnkreving())
                .forEach(supportedKodeSoknGrKom::addAll);

        return supportedKodeSoknGrKom;
    }

    static List<String> supportedKodeSoknGrKomUtenInnkreving() {
        String[] kodeSoknGrKomUtenInnkreving = new String[] { BIDRAG, BIDRAG_18_AAR, SARTILSKUDD };

        return Arrays.asList(kodeSoknGrKomUtenInnkreving);
    }

    static List<String> bidrag18Aar() {
        String[] kodeSoknGrKom = new String[] { BIDRAG_18_AAR, BIDRAG_18_AAR_INNKREVING };

        return Arrays.asList(kodeSoknGrKom);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface NoFomDato {
    }
}