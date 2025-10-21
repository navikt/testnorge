package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.data.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.KjoennDTO.Kjoenn;

import java.security.SecureRandom;
import java.util.Random;

import static java.util.Objects.isNull;
import static no.nav.testnav.libs.data.pdlforvalter.v1.KjoennDTO.Kjoenn.KVINNE;
import static no.nav.testnav.libs.data.pdlforvalter.v1.KjoennDTO.Kjoenn.MANN;
import static no.nav.testnav.libs.data.pdlforvalter.v1.KjoennDTO.Kjoenn.UKJENT;

@UtilityClass
public class KjoennUtility {

    private static final Random RANDOM = new SecureRandom();

    private static Kjoenn getMotsattKjoenn(Kjoenn kjoenn) {

        if (isNull(kjoenn)) {
            return null;
        }

        return switch (kjoenn) {
            case KVINNE -> MANN;
            case MANN -> KVINNE;
            default -> UKJENT;
        };
    }

    public static Kjoenn getPartnerKjoenn(Kjoenn kjoenn) {

        return RANDOM.nextFloat() <= .95 ?
                getMotsattKjoenn(kjoenn) :
                kjoenn;
    }

    public KjoennDTO.Kjoenn getKjoenn() {

        return RANDOM.nextBoolean() ? KVINNE : MANN;
    }
}