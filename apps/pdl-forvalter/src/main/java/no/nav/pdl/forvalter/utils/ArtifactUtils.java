package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.data.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.Identtype;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class ArtifactUtils {

    public static final String NORGE = "NOR";

    public static boolean hasLandkode(String landkode) {

        return isNotBlank(landkode) &&
                (landkode.matches("[A-Z]{3}") ||
                        "???".equals(landkode));
    }

    public static boolean hasSpraak(String spraak) {

        return isNotBlank(spraak) && spraak.matches("[A-Z]{2}");
    }

    public static DbVersjonDTO.Master getMaster(DbVersjonDTO artifact, PersonDTO person) {

        return getMaster(artifact, person.getIdenttype());
    }

    public static DbVersjonDTO.Master getMaster(DbVersjonDTO artifact, Identtype identtype) {

        if (identtype == Identtype.NPID) {
            return DbVersjonDTO.Master.PDL;

        } else if (nonNull(artifact.getMaster())) {
            return artifact.getMaster();

        } else {
            return DbVersjonDTO.Master.FREG;
        }
    }

    public static String getKilde(DbVersjonDTO artifact) {

        return isNotBlank(artifact.getKilde()) ? artifact.getKilde() : "Dolly";
    }
}
