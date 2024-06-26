package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.data.pdlforvalter.v1.NAVsPersonIdentifikatorDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
public class NAVsPersonIdentifikatorService implements Validation<NAVsPersonIdentifikatorDTO> {

    public List<NAVsPersonIdentifikatorDTO> convert(PersonDTO person) {

        for (var type : person.getNAVsPersonIdentifikator()) {
            if (isTrue(type.getIsNew())) {

                type.setIdentifikator(person.getIdent());
                type.setKilde(getKilde(type));
                type.setMaster(getMaster(type, person));
            }
        }

        return person.getNAVsPersonIdentifikator();
    }

    @Override
    public void validate(NAVsPersonIdentifikatorDTO artifact) {

        // No validation
    }
}
