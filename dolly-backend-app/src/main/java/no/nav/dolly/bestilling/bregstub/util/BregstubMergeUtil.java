package no.nav.dolly.bestilling.bregstub.util;

import static java.util.Objects.isNull;

import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;
import no.nav.dolly.bestilling.bregstub.domain.RolleoversiktTo;

@UtilityClass
public class BregstubMergeUtil {

    public static RolleoversiktTo merge(RolleoversiktTo nyRolleovesikt, RolleoversiktTo eksisterendeRoller) {

        if (isNull(eksisterendeRoller)) {
            return nyRolleovesikt;
        }

        nyRolleovesikt.getUnderstatuser().addAll(eksisterendeRoller.getUnderstatuser().stream()
                .filter(understatus -> nyRolleovesikt.getUnderstatuser().stream().noneMatch(understatus::equals))
                .collect(Collectors.toList()));

        nyRolleovesikt.getEnheter().addAll(eksisterendeRoller.getEnheter().stream()
                .filter(enhet -> nyRolleovesikt.getEnheter().stream().noneMatch(enhet::equals))
                .collect(Collectors.toList()));

        return nyRolleovesikt;
    }
}
