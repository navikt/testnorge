package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.utils.DatoFraIdentUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselsdatoDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
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
@RequiredArgsConstructor
public class FoedselsdatoService implements BiValidation<FoedselsdatoDTO, PersonDTO> {

    public List<FoedselsdatoDTO> convert(PersonDTO person) {

        for (var type : person.getFoedselsdato()) {

            if (isTrue(type.getIsNew())) {

                handle(type, person.getIdent());

                type.setKilde(getKilde(type));
                type.setMaster(getMaster(type, person));
            }
        }

        person.setFoedselsdato(new ArrayList<>(person.getFoedselsdato()));
        person.getFoedselsdato().sort(Comparator.comparing(FoedselsdatoDTO::getFoedselsaar).reversed());

        renumberId(person.getFoedselsdato());

        return person.getFoedselsdato();
    }

    private void handle(FoedselsdatoDTO foedsel, String ident) {

        if (isNull(foedsel.getFoedselsaar())) {
            if (isNull(foedsel.getFoedselsdato())) {
                foedsel.setFoedselsdato(DatoFraIdentUtility.getDato(ident).atStartOfDay());
            }

            foedsel.setFoedselsaar(foedsel.getFoedselsdato().getYear());
        }
    }

    @Override
    public void validate(FoedselsdatoDTO artifact, PersonDTO personDTO) {

        // Ingen validering
    }
}