package no.nav.dolly.bestilling.brregstub.util;

import lombok.experimental.UtilityClass;
import no.nav.dolly.bestilling.brregstub.domain.RolleoversiktTo;

import static java.util.Objects.isNull;

@UtilityClass
public class BrregstubMergeUtil {

    public static RolleoversiktTo merge(RolleoversiktTo nyRolleovesikt, RolleoversiktTo eksisterendeRoller) {

        if (isNull(eksisterendeRoller)) {
            return nyRolleovesikt;
        }

        nyRolleovesikt.getUnderstatuser().addAll(eksisterendeRoller.getUnderstatuser().stream()
                .filter(understatus -> nyRolleovesikt.getUnderstatuser().stream().noneMatch(understatus::equals))
                .toList());

        nyRolleovesikt.getEnheter().addAll(eksisterendeRoller.getEnheter().stream()
                .filter(enhet -> nyRolleovesikt.getEnheter().stream().noneMatch(enhet::equals))
                .toList());

        return nyRolleovesikt;
    }
}
