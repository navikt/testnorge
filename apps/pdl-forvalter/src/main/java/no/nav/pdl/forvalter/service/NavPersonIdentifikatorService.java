package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.data.pdlforvalter.v1.NavPersonIdentifikatorDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.time.LocalDate.now;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
public class NavPersonIdentifikatorService implements Validation<NavPersonIdentifikatorDTO> {

    public List<NavPersonIdentifikatorDTO> convert(PersonDTO person) {

        for (var type : person.getNavPersonIdentifikator()) {
            if (isTrue(type.getIsNew())) {

                type.setIdentifikator(person.getIdent());
                type.setGyldigFraOgMed(now().minusWeeks(1));
                type.setKilde(getKilde(type));
                type.setMaster(getMaster(type, person));
            }
        }

        return person.getNavPersonIdentifikator();
    }

    @Override
    public void validate(NavPersonIdentifikatorDTO artifact) {

        // No validation
    }
}
