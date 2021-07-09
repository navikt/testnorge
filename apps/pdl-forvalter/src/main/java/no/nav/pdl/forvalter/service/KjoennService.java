package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.utils.KjoennFraIdentUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
public class KjoennService {

    public List<KjoennDTO> convert(PersonDTO person) {

        for (var type : person.getKjoenn()) {

            if (isTrue(type.getIsNew())) {

                handle(type, person.getIdent());
                if (isBlank(type.getKilde())) {
                    type.setKilde("Dolly");
                }
            }
        }
        return person.getKjoenn();
    }

    private void handle(KjoennDTO kjoenn, String ident) {

        if (isNull(kjoenn.getKjoenn())) {
            kjoenn.setKjoenn(KjoennFraIdentUtility.getKjoenn(ident));
        }
    }
}
