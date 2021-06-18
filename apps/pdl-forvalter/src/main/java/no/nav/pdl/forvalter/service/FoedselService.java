package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.utils.DatoFraIdentUtility;
import no.nav.pdl.forvalter.utils.IdenttypeFraIdentUtility;
import no.nav.pdl.forvalter.utils.TilfeldigKommuneService;
import no.nav.pdl.forvalter.utils.TilfeldigLandService;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.FoedselDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.PersonDTO;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.NORGE;
import static no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.Identtype.FNR;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class FoedselService {

    private final TilfeldigKommuneService tilfeldigKommuneService;
    private final TilfeldigLandService tilfeldigLandService;

    public List<FoedselDTO> convert(PersonDTO person) {

        for (var type : person.getFoedsel()) {

            if (type.isNew()) {

                handle(type, person.getIdent(),
                        person.getBostedsadresse().stream().reduce((a, b) -> b).orElse(null),
                        person.getInnflytting().stream().reduce((a, b) -> b).orElse(null));
                if (Strings.isBlank(type.getKilde())) {
                    type.setKilde("Dolly");
                }
            }
        }
        return person.getFoedsel();
    }

    private void handle(FoedselDTO foedsel, String ident, BostedadresseDTO bostedadresse, InnflyttingDTO innflytting) {

        if (isNull(foedsel.getFoedselsdato())) {
            foedsel.setFoedselsdato(DatoFraIdentUtility.getDato(ident).atStartOfDay());
        }
        foedsel.setFoedselsaar(foedsel.getFoedselsdato().getYear());

        setFoedeland(foedsel, ident, bostedadresse, innflytting);

        setFodekommune(foedsel, bostedadresse);
    }

    private void setFoedeland(FoedselDTO foedsel, String ident, BostedadresseDTO bostedadresse, InnflyttingDTO innflytting) {
        if (isNull(foedsel.getFoedeland())) {
            if (FNR.equals(IdenttypeFraIdentUtility.getIdenttype(ident))) {
                foedsel.setFoedeland(NORGE);
            } else if (nonNull(innflytting)) {
                foedsel.setFoedeland(innflytting.getFraflyttingsland());
            } else if (nonNull(bostedadresse) && nonNull(bostedadresse.getUtenlandskAdresse())) {
                foedsel.setFoedeland(bostedadresse.getUtenlandskAdresse().getLandkode());
            } else {
                foedsel.setFoedeland(tilfeldigLandService.getLand());
            }
        }
    }

    private void setFodekommune(FoedselDTO foedsel, BostedadresseDTO bostedadresse) {
        if (NORGE.equals(foedsel.getFoedeland()) && isBlank(foedsel.getFodekommune())) {
            if (nonNull(bostedadresse)) {
                if (nonNull(bostedadresse.getVegadresse())) {
                    foedsel.setFodekommune(bostedadresse.getVegadresse().getKommunenummer());
                } else if (nonNull(bostedadresse.getMatrikkeladresse())) {
                    foedsel.setFodekommune(bostedadresse.getMatrikkeladresse().getKommunenummer());
                } else if (nonNull(bostedadresse.getUkjentBosted())) {
                    foedsel.setFodekommune(bostedadresse.getUkjentBosted().getBostedskommune());
                }
            } else {
                foedsel.setFodekommune(tilfeldigKommuneService.getKommune());
            }
        }
    }
}
