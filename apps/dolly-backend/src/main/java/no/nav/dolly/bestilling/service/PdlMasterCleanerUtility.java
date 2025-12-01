package no.nav.dolly.bestilling.service;

import lombok.experimental.UtilityClass;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master.FREG;

@UtilityClass
public class PdlMasterCleanerUtility {

    public static PdlPersondata clean(PdlPersondata persondata) {

        if (isNull(persondata)) {
            return null;
        }

        persondata.setOpprettNyPerson(null);

        if (nonNull(persondata.getPerson())) {
            Arrays.stream(persondata.getPerson().getClass().getMethods())
                    .filter(method -> method.getName().contains("get"))
                    .map(method -> {
                        try {
                            return method.invoke(persondata.getPerson());
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new DollyFunctionalException("Feilet å filtrere bestilling på master = PDL");
                        }
                    })
                    .filter(Objects::nonNull)
                    .filter(List.class::isInstance)
                    .map(data -> (List<? extends DbVersjonDTO>) data)
                    .forEach(PdlMasterCleanerUtility::filterOpplysning);

            persondata.getPerson().setNyident(null);
        }

        return persondata;
    }

    public static void filterOpplysning(List<? extends DbVersjonDTO> opplysninger) {

        opplysninger.removeIf(opplysning -> opplysning.getMaster() == FREG);
    }
}
