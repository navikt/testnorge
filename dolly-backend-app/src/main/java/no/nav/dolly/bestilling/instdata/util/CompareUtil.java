package no.nav.dolly.bestilling.instdata.util;

import java.util.List;

import lombok.experimental.UtilityClass;
import no.nav.dolly.domain.resultset.inst.Instdata;

@UtilityClass
public class CompareUtil {

    public static boolean isSubsetOf(List<Instdata> instdataRequest, List<Instdata> eksisterendeInstdata) {

        return eksisterendeInstdata.stream().allMatch(eksisterende -> instdataRequest.stream().allMatch(eksisterende::equals));
    }
}
