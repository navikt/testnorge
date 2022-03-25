package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.consumer.GeografiskeKodeverkConsumer;
import no.nav.pdl.forvalter.utils.DatoFraIdentUtility;
import no.nav.pdl.forvalter.utils.IdenttypeFraIdentUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.NORGE;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.FNR;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class FoedselService implements BiValidation<FoedselDTO, PersonDTO> {

    private final GeografiskeKodeverkConsumer geografiskeKodeverkConsumer;

    public List<FoedselDTO> convert(PersonDTO person) {

        for (var type : person.getFoedsel()) {

            if (isTrue(type.getIsNew())) {

                handle(type, person.getIdent(),
                        person.getBostedsadresse().stream().reduce((a, b) -> b).orElse(null),
                        person.getInnflytting().stream().reduce((a, b) -> b).orElse(null));
                type.setKilde(isNotBlank(type.getKilde()) ? type.getKilde() : "Dolly");
                type.setMaster(nonNull(type.getMaster()) ? type.getMaster() : Master.FREG);
            }
        }
        return person.getFoedsel();
    }

    private void handle(FoedselDTO foedsel, String ident, BostedadresseDTO bostedadresse, InnflyttingDTO innflytting) {

        if (nonNull(foedsel.getFoedselsaar())) {
            var fodselsdato = DatoFraIdentUtility.getDato(ident);
            foedsel.setFoedselsdato(LocalDateTime.of(foedsel.getFoedselsaar(), fodselsdato.getMonth(), fodselsdato.getDayOfMonth(), 0, 0));

        } else if (isNull(foedsel.getFoedselsdato())) {
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
                foedsel.setFoedeland(geografiskeKodeverkConsumer.getTilfeldigLand());
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
                foedsel.setFodekommune(geografiskeKodeverkConsumer.getTilfeldigKommune());
            }
        }
    }

    @Override
    public void validate(FoedselDTO artifact, PersonDTO personDTO) {

        // Ingen validering
    }
}
