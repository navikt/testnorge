package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.pdl.forvalter.utils.DatoFraIdentUtility;
import no.nav.pdl.forvalter.utils.IdenttypeUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.NORGE;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.renumberId;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.FNR;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class FoedselService implements BiValidation<FoedselDTO, PersonDTO> {

    private final KodeverkConsumer kodeverkConsumer;

    public List<FoedselDTO> convert(PersonDTO person) {

        for (var type : person.getFoedsel()) {

            if (isTrue(type.getIsNew())) {

                handle(type, person.getIdent(),
                        person.getBostedsadresse().stream().reduce((a, b) -> b).orElse(null),
                        person.getInnflytting().stream().reduce((a, b) -> b).orElse(null));

                type.setKilde(getKilde(type));
                type.setMaster(getMaster(type, person));
            }
        }

        person.setFoedsel(new ArrayList<>(person.getFoedsel()));
        person.getFoedsel().sort(Comparator.comparing(FoedselDTO::getFoedselsaar).reversed());

        renumberId(person.getFoedsel());

        return person.getFoedsel();
    }

    private void handle(FoedselDTO foedsel, String ident, BostedadresseDTO bostedadresse, InnflyttingDTO innflytting) {

        if (isNull(foedsel.getFoedselsaar())) {
            if (isNull(foedsel.getFoedselsdato())) {
                foedsel.setFoedselsdato(DatoFraIdentUtility.getDato(ident).atStartOfDay());
            }

            foedsel.setFoedselsaar(foedsel.getFoedselsdato().getYear());
        }

        setFoedeland(foedsel, ident, bostedadresse, innflytting);
        setFoedekommune(foedsel, bostedadresse);
    }

    private void setFoedeland(FoedselDTO foedsel, String ident, BostedadresseDTO bostedadresse, InnflyttingDTO innflytting) {
        if (isNull(foedsel.getFoedeland())) {
            if (FNR.equals(IdenttypeUtility.getIdenttype(ident))) {
                foedsel.setFoedeland(NORGE);
            } else if (nonNull(innflytting)) {
                foedsel.setFoedeland(innflytting.getFraflyttingsland());
            } else if (nonNull(bostedadresse) && nonNull(bostedadresse.getUtenlandskAdresse())) {
                foedsel.setFoedeland(bostedadresse.getUtenlandskAdresse().getLandkode());
            } else {
                foedsel.setFoedeland(kodeverkConsumer.getTilfeldigLand());
            }
        }
    }

    private void setFoedekommune(FoedselDTO foedsel, BostedadresseDTO bostedadresse) {
        if (NORGE.equals(foedsel.getFoedeland()) && isBlank(foedsel.getFoedekommune())) {
            if (nonNull(bostedadresse)) {
                if (nonNull(bostedadresse.getVegadresse())) {
                    foedsel.setFoedekommune(bostedadresse.getVegadresse().getKommunenummer());
                } else if (nonNull(bostedadresse.getMatrikkeladresse())) {
                    foedsel.setFoedekommune(bostedadresse.getMatrikkeladresse().getKommunenummer());
                } else if (nonNull(bostedadresse.getUkjentBosted()) &&
                        isNotBlank(bostedadresse.getUkjentBosted().getBostedskommune())) {
                    foedsel.setFoedekommune(bostedadresse.getUkjentBosted().getBostedskommune());
                } else {
                    foedsel.setFoedekommune(kodeverkConsumer.getTilfeldigKommune());
                }
            } else {
                foedsel.setFoedekommune(kodeverkConsumer.getTilfeldigKommune());
            }
        }
    }

    @Override
    public void validate(FoedselDTO artifact, PersonDTO personDTO) {

        // Ingen validering
    }
}
