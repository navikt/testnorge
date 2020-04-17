package no.nav.dolly.bestilling.instdata.util;

import java.util.List;

import lombok.experimental.UtilityClass;
import no.nav.dolly.domain.resultset.inst.Instdata;

@UtilityClass
public class CompareUtil {

    public static boolean isSubsetOf(List<Instdata> instdataRequest, List<Instdata> eksisterendeInstdata) {

        boolean match = true;
        for (Instdata instRequest : instdataRequest) {
            if (eksisterendeInstdata.stream().noneMatch(instdata -> instdata.equals(instRequest))) {
                match = false;
                break;
            }
        }
        return match;
    }
}
