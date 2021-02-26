package no.nav.registre.testnorge.domain.dto.arena.testnorge.tiltak;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Deltakerstatuser;

public class Constants {

    private Constants() {
    }

    private static final List<String> avbruttTiltakStatuser = new ArrayList<>(Arrays.asList("AVLYST", "AVBRUTT"));
    private static final Map<String, List<String>> deltakerstatuskoderMedAarsakkoder;
    private static final String PLANLAGT_TILTAK_STATUS = "PLANLAGT";

    static {
        deltakerstatuskoderMedAarsakkoder = new HashMap<>();
        deltakerstatuskoderMedAarsakkoder.put(Deltakerstatuser.NEITAKK.toString(), Arrays.asList("ANN", "BEGA", "FRISM", "FTOAT", "HENLU", "SYK", "UTV"));
        deltakerstatuskoderMedAarsakkoder.put(Deltakerstatuser.IKKEM.toString(), Arrays.asList("ANN", "BEGA", "SYK"));
        deltakerstatuskoderMedAarsakkoder.put(Deltakerstatuser.DELAVB.toString(), Arrays.asList("ANN", "BEGA", "FTOAT", "SYK"));
    }

    public static List<String> getAvbruttTiltakStatuser() {
        return avbruttTiltakStatuser;
    }

    public static Map<String, List<String>> getDeltakerstatuskoderMedAarsakkoder() {
        return deltakerstatuskoderMedAarsakkoder;
    }

    public static String getPlanlagtTiltakStatus(){
        return PLANLAGT_TILTAK_STATUS;
    }
}
