package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.utils.DatoFraIdentUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DeltBostedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UkjentBostedDTO;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class DeltBostedService {

    private final PersonRepository personRepository;
    private final AdresseServiceConsumer adresseServiceConsumer;
    private final MapperFacade mapperFacade;

    private static boolean isEqualAdresse(BostedadresseDTO adresse1, BostedadresseDTO adresse2) {

        return nonNull(adresse1.getVegadresse()) && adresse1.getVegadresse().equals(adresse2.getVegadresse()) ||
                nonNull(adresse1.getMatrikkeladresse()) && adresse1.getMatrikkeladresse().equals(adresse2.getMatrikkeladresse()) ||
                nonNull(adresse1.getUtenlandskAdresse()) && adresse1.getUtenlandskAdresse().equals(adresse2.getUtenlandskAdresse()) ||
                nonNull(adresse1.getUkjentBosted()) && adresse1.getUkjentBosted().equals(adresse2.getUkjentBosted());
    }

    public void handle(DeltBostedDTO deltBostedBestilling, PersonDTO hovedperson, String barnIdent) {

        var deltBosted = mapperFacade.map(deltBostedBestilling, DeltBostedDTO.class);
        
        prepAdresser(deltBosted);

        if (deltBosted.countAdresser() == 0 &&
                hovedperson.getSivilstand().stream().anyMatch(sivilstand -> sivilstand.isGift() || sivilstand.isSeparert())) {

            var sivilstandGift = hovedperson.getSivilstand().stream()
                    .filter(sivilstand -> sivilstand.isGift() || sivilstand.isSeparert())
                    .findFirst();

            if (sivilstandGift.isPresent()) {
                var partner = personRepository.findByIdent(
                        sivilstandGift.get().getRelatertVedSivilstand());

                if (partner.isPresent()) {
                    var partneradresse = partner.get().getPerson()
                            .getBostedsadresse().stream().findFirst();

                    var hovedpersonadresse = hovedperson.getBostedsadresse().stream().findFirst();

                    if (partneradresse.isPresent() && hovedpersonadresse.isPresent() &&
                            !isEqualAdresse(partneradresse.get(), hovedpersonadresse.get())) {

                        deltBosted.setVegadresse(partneradresse.get().getVegadresse());
                        deltBosted.setMatrikkeladresse(partneradresse.get().getMatrikkeladresse());
                        deltBosted.setUkjentBosted(nonNull(partneradresse.get().getUkjentBosted()) ?
                                mapperFacade.map(partneradresse.get().getUkjentBosted(), UkjentBostedDTO.class) : null);
                    }
                }
            }
        }

        if (deltBosted.countAdresser() > 0) {
            personRepository.findByIdent(barnIdent)
                    .ifPresent(dbPerson -> {
                        if (isNull(deltBosted.getStartdatoForKontrakt())) {
                            deltBosted.setStartdatoForKontrakt(dbPerson.getPerson().getFoedsel().stream()
                                    .map(FoedselDTO::getFoedselsdato)
                                    .findFirst()
                                    .orElse(DatoFraIdentUtility.getDato(barnIdent).atStartOfDay()));
                        }
                        setDeltBosted(dbPerson.getPerson(), deltBosted);
                    });
        }
    }

    public void update(DeltBostedDTO deltBosted) {

        prepAdresser(deltBosted);
    }

    private void prepAdresser(DeltBostedDTO deltBosted) {

        if (nonNull(deltBosted.getVegadresse())) {

            var vegadresse =
                    adresseServiceConsumer.getVegadresse(deltBosted.getVegadresse(), deltBosted.getAdresseIdentifikatorFraMatrikkelen());
            deltBosted.setAdresseIdentifikatorFraMatrikkelen(vegadresse.getMatrikkelId());
            mapperFacade.map(vegadresse, deltBosted.getVegadresse());

        } else if (nonNull(deltBosted.getMatrikkeladresse())) {

            var matrikkeladresse =
                    adresseServiceConsumer.getMatrikkeladresse(deltBosted.getMatrikkeladresse(), deltBosted.getAdresseIdentifikatorFraMatrikkelen());
            deltBosted.setAdresseIdentifikatorFraMatrikkelen(matrikkeladresse.getMatrikkelId());
            mapperFacade.map(matrikkeladresse, deltBosted.getMatrikkeladresse());
        }
    }

    private void setDeltBosted(PersonDTO barn, DeltBostedDTO deltBosted) {

        deltBosted.setId(barn.getDeltBosted().stream()
                .findFirst()
                .map(DeltBostedDTO::getId)
                .orElse(0) + 1);
        deltBosted.setKilde(isNotBlank(deltBosted.getKilde()) ? deltBosted.getKilde() : "Dolly");
        deltBosted.setMaster(nonNull(deltBosted.getMaster()) ? deltBosted.getMaster() : DbVersjonDTO.Master.FREG);
        barn.getDeltBosted().add(0, deltBosted);
    }
}