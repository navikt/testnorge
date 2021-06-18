package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.DoedsfallDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.OppholdDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.UtflyttingDTO;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO.Folkeregisterpersonstatus.BOSATT;
import static no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO.Folkeregisterpersonstatus.DOED;
import static no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO.Folkeregisterpersonstatus.FOEDSELSREGISTRERT;
import static no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO.Folkeregisterpersonstatus.IKKE_BOSATT;
import static no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO.Folkeregisterpersonstatus.MIDLERTIDIG;
import static no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO.Folkeregisterpersonstatus.UTFLYTTET;

@Service
@RequiredArgsConstructor
public class FolkeregisterPersonstatusService {

    public List<FolkeregisterpersonstatusDTO> convert(PersonDTO person) {

        for (var type : person.getFolkeregisterpersonstatus()) {

            if (type.isNew()) {
                handle(type,
                        person.getBostedsadresse().stream().findFirst().orElse(null),
                        person.getUtflytting().stream().findFirst().orElse(null),
                        person.getOpphold().stream().findFirst().orElse(null),
                        person.getDoedsfall().stream().findFirst().orElse(null));

                if (Strings.isBlank(type.getKilde())) {
                    type.setKilde("Dolly");
                }
            }
        }
        return person.getFolkeregisterpersonstatus();
    }

    private void handle(FolkeregisterpersonstatusDTO status,
                        BostedadresseDTO bostedadresse,
                        UtflyttingDTO utflytting,
                        OppholdDTO opphold,
                        DoedsfallDTO doedsfall) {

        if (isNull(status.getStatus())) {

            if (nonNull(doedsfall)) {
                status.setStatus(DOED);

            } else if (nonNull(utflytting)) {
                status.setStatus(UTFLYTTET);

            } else if (nonNull(opphold)) {
                status.setStatus(MIDLERTIDIG);

            } else if (nonNull(bostedadresse) &&
                    (nonNull(bostedadresse.getVegadresse()) || nonNull(bostedadresse.getMatrikkeladresse()))) {
                status.setStatus(BOSATT);

            } else if (nonNull(bostedadresse) && nonNull(bostedadresse.getUtenlandskAdresse())) {
                status.setStatus(IKKE_BOSATT);

            } else {
                status.setStatus(FOEDSELSREGISTRERT);
            }
        }
    }
}
