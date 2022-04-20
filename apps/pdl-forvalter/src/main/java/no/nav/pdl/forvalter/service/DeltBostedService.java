package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DeltBostedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UkjentBostedDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class DeltBostedService implements BiValidation<DeltBostedDTO, PersonDTO> {

    private static final String INVALID_AMBIGUOUS_ADRESSE = "Delt bosted: kun én adresse skal være satt (vegadresse, " +
            "ukjentBosted, matrikkeladresse)";

    private final PersonRepository personRepository;
    private final AdresseServiceConsumer adresseServiceConsumer;
    private final MapperFacade mapperFacade;

    private static boolean isEqualAdresse(BostedadresseDTO adresse1, BostedadresseDTO adresse2) {

        return nonNull(adresse1.getVegadresse()) && adresse1.getVegadresse().equals(adresse2.getVegadresse()) ||
                nonNull(adresse1.getMatrikkeladresse()) && adresse1.getMatrikkeladresse().equals(adresse2.getMatrikkeladresse()) ||
                nonNull(adresse1.getUtenlandskAdresse()) && adresse1.getUtenlandskAdresse().equals(adresse2.getUtenlandskAdresse()) ||
                nonNull(adresse1.getUkjentBosted()) && adresse1.getUkjentBosted().equals(adresse2.getUkjentBosted());
    }

    public List<DeltBostedDTO> convert(PersonDTO person) {

        for (var type : person.getDeltBosted()) {

            if (isTrue(type.getIsNew())) {

                type.setKilde(isNotBlank(type.getKilde()) ? type.getKilde() : "Dolly");
                type.setMaster(nonNull(type.getMaster()) ? type.getMaster() : Master.FREG);
                handle(type, person);
            }
        }
        // Delt bosted settes kun på barn ikke på foreldre
        return emptyList();
    }

    @Override
    public void validate(DeltBostedDTO deltBosted, PersonDTO hovedperson) {

        if (deltBosted.countAdresser() > 1) {

            throw new InvalidRequestException(INVALID_AMBIGUOUS_ADRESSE);
        }
    }

    private void handle(DeltBostedDTO deltbosted, PersonDTO hovedperson) {

        if (nonNull(deltbosted.getVegadresse())) {

            var vegadresse =
                    adresseServiceConsumer.getVegadresse(deltbosted.getVegadresse(), deltbosted.getAdresseIdentifikatorFraMatrikkelen());
            deltbosted.setAdresseIdentifikatorFraMatrikkelen(vegadresse.getMatrikkelId());
            mapperFacade.map(vegadresse, deltbosted.getVegadresse());

        } else if (nonNull(deltbosted.getMatrikkeladresse())) {

            var matrikkeladresse =
                    adresseServiceConsumer.getMatrikkeladresse(deltbosted.getMatrikkeladresse(), deltbosted.getAdresseIdentifikatorFraMatrikkelen());
            deltbosted.setAdresseIdentifikatorFraMatrikkelen(matrikkeladresse.getMatrikkelId());
            mapperFacade.map(matrikkeladresse, deltbosted.getMatrikkeladresse());

        } else if (isNull(deltbosted.getUkjentBosted()) &&
                hovedperson.getSivilstand().stream().anyMatch(SivilstandDTO::isGift)) {

            var partner = personRepository.findByIdent(
                    hovedperson.getSivilstand().stream()
                            .filter(SivilstandDTO::isGift)
                            .findFirst().get().getRelatertVedSivilstand());

            if (partner.isPresent()) {
                var partneradresse = partner.get().getPerson()
                        .getBostedsadresse().stream().findFirst();

                var hovedpersonadresse = hovedperson.getBostedsadresse().stream().findFirst();

                if (partneradresse.isPresent() && hovedpersonadresse.isPresent() &&
                        !isEqualAdresse(partneradresse.get(), hovedpersonadresse.get())) {

                    deltbosted.setVegadresse(partneradresse.get().getVegadresse());
                    deltbosted.setMatrikkeladresse(partneradresse.get().getMatrikkeladresse());
                    deltbosted.setUkjentBosted(nonNull(partneradresse.get().getUkjentBosted()) ?
                            mapperFacade.map(partneradresse.get().getUkjentBosted(), UkjentBostedDTO.class) : null);
                }
            }
        }

        hovedperson.getForelderBarnRelasjon().stream()
                .filter(relasjon -> deltbosted.countAdresser() > 0)
                .filter(ForelderBarnRelasjonDTO::hasBarn)
                .map(ForelderBarnRelasjonDTO::getRelatertPerson)
                .map(ident -> personRepository.findByIdent(ident).get())
                .map(DbPerson::getPerson)
                .forEach(barn -> setDeltBosted(barn, deltbosted));
    }

    private void setDeltBosted(PersonDTO barn, DeltBostedDTO deltBosted) {

        var nyttDeltbosted = mapperFacade.map(deltBosted, DeltBostedDTO.class);
        nyttDeltbosted.setId(barn.getDeltBosted().stream()
                .findFirst()
                .map(DeltBostedDTO::getId)
                .orElse(0) + 1);
        barn.getDeltBosted().add(0, nyttDeltbosted);
    }
}