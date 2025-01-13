package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.data.pdlforvalter.v1.DoedsfallDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Objects.isNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.renumberId;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
public class DoedsfallService implements Validation<DoedsfallDTO> {

    private static final String INVALID_DATO_MISSING = "Dødsfall: dødsdato må oppgis";

    public List<DoedsfallDTO> convert(PersonDTO person) {

        for (var type : person.getDoedsfall()) {
            if (isTrue(type.getIsNew())) {

                type.setKilde(getKilde(type));
                type.setMaster(getMaster(type, person));
            }
        }

        person.setDoedsfall(new ArrayList<>(person.getDoedsfall()));

        person.getDoedsfall().sort(Comparator.comparing(DoedsfallDTO::getDoedsdato).reversed());
        renumberId(person.getDoedsfall());

        return person.getDoedsfall();
    }

    @Override
    public void validate(DoedsfallDTO type) {

        if (isNull(type.getDoedsdato())) {

            throw new InvalidRequestException(INVALID_DATO_MISSING);
        }
    }
}
