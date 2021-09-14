package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.service.DummyAdresseService.getStrengtFortroligKontaktadresse;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO.AdresseBeskyttelse.STRENGT_FORTROLIG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO.AdresseBeskyttelse.STRENGT_FORTROLIG_UTLAND;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
public class AdressebeskyttelseService implements Validation<AdressebeskyttelseDTO> {

    private static final String VALIDATION_UTLAND_MASTER_ERROR = "Gradering STRENGT_FORTROLIG_UTLAND kan kun settes hvis master er PDL";

    public List<AdressebeskyttelseDTO> convert(PersonDTO person) {

        for (var type : person.getAdressebeskyttelse()) {
            if (isTrue(type.getIsNew())) {

                handle(type, person);
                type.setKilde(isNotBlank(type.getKilde()) ? type.getKilde() : "Dolly");
                type.setMaster(nonNull(type.getMaster()) ? type.getMaster() : Master.FREG);
            }
        }
        return person.getAdressebeskyttelse();
    }


    @Override
    public void validate(AdressebeskyttelseDTO adressebeskyttelse) {

        if (STRENGT_FORTROLIG_UTLAND == adressebeskyttelse.getGradering() &&
                Master.FREG == adressebeskyttelse.getMaster()) {
            throw new InvalidRequestException(VALIDATION_UTLAND_MASTER_ERROR);
        }
    }

    private void handle(AdressebeskyttelseDTO adressebeskyttelse, PersonDTO person) {

        if (STRENGT_FORTROLIG_UTLAND == adressebeskyttelse.getGradering()) {
            adressebeskyttelse.setMaster(Master.PDL);
        }

        if (STRENGT_FORTROLIG == adressebeskyttelse.getGradering()) {
            person.setBostedsadresse(null);
            person.setOppholdsadresse(null);
            person.setKontaktadresse(null);
            person.getKontaktadresse().add(getStrengtFortroligKontaktadresse());
        }
    }
}
