package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.FoedselsdatoUtility;
import no.nav.pdl.forvalter.utils.IdenttypeUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DeltBostedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UkjentBostedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
@RequiredArgsConstructor
public class DeltBostedService implements BiValidation<DeltBostedDTO, PersonDTO> {

    private static final String VALIDATION_TO_FORELDRE_ERROR = "DeltBosted: må ha to foreldre for å kunne opprette delt bosted";
    private static final String VALIDATION_ADRESSER_ERROR = "DeltBosted: Foreldre må ha ulike adresser, evt angis kun én adressetype: vegadresse, " +
            "matrikkeladresse, ukjentbosted for oppretting.";

    private final PersonRepository personRepository;
    private final AdresseServiceConsumer adresseServiceConsumer;
    private final MapperFacade mapperFacade;

    public List<DeltBostedDTO> convert(PersonDTO person) {

        for (var type : person.getDeltBosted()) {

            if (isTrue(type.getIsNew())) {

                handle(type, person);

                type.setKilde(getKilde(type));
                type.setMaster(getMaster(type, person));
            }
        }

        if (LocalDateTime.now().isAfter(FoedselsdatoUtility.getMyndighetsdato(person))) {
            person.setDeltBosted(null);
        }
        return person.getDeltBosted();
    }

    public void handle(DeltBostedDTO deltBosted, PersonDTO hovedperson) {

        if (isNull(deltBosted.getStartdatoForKontrakt())) {
            deltBosted.setStartdatoForKontrakt(LocalDateTime.now());
        }

        if (LocalDateTime.now().isAfter(FoedselsdatoUtility.getMyndighetsdato(hovedperson))) {

            // DeltBosted for forelder, settes på barnet
            var foreldre = getForeldre(hovedperson);

            if (deltBosted.countAdresser() > 0) {
                prepAdresser(deltBosted);
            } else {
                foreldre.stream()
                        .map(PersonDTO::getBostedsadresse)
                        .map(adresser -> !adresser.isEmpty() ? adresser.getFirst() : null)
                        .filter(Objects::nonNull)
                        .forEach(boadresse -> {
                            if (!hovedperson.getBostedsadresse().isEmpty() &&
                                    !isEqualAdresse(hovedperson.getBostedsadresse().getFirst(), boadresse)) {

                                setAdresse(deltBosted, boadresse);
                            }
                        });
            }

            var barna = foreldre.stream()
                    .map(PersonDTO::getForelderBarnRelasjon)
                    .flatMap(Collection::stream)
                    .filter(ForelderBarnRelasjonDTO::isForeldre)
                    .map(ForelderBarnRelasjonDTO::getIdentForRelasjon)
                    .collect(Collectors.toSet());

           personRepository.findByIdentIn(barna, Pageable.unpaged()).stream()
                   .map(DbPerson::getPerson)
                   .filter(person -> person.getForelderBarnRelasjon().stream()
                           .anyMatch(forelder ->
                                   forelder.getIdentForRelasjon().equals(foreldre.getFirst().getIdent()) ||
                                   forelder.getIdentForRelasjon().equals(foreldre.get(1).getIdent())))
                   .forEach(person -> {
                       deltBosted.setId(person.getDeltBosted().size() + 1);
                       person.getDeltBosted()
                           .addFirst(mapperFacade.map(deltBosted, DeltBostedDTO.class));
                   });

        } else {
            // DeltBosted for barn, settes på barnet
            if (deltBosted.countAdresser() > 0) {
                prepAdresser(deltBosted);

            } else {
                var boadresser = personRepository.findByIdentIn(hovedperson.getForelderBarnRelasjon().stream()
                                .filter(ForelderBarnRelasjonDTO::isBarn)
                                .map(ForelderBarnRelasjonDTO::getIdentForRelasjon)
                                .toList(), Pageable.unpaged()).stream()
                        .map(DbPerson::getPerson)
                        .map(PersonDTO::getBostedsadresse)
                        .map(adresser -> !adresser.isEmpty() ? adresser.getFirst() : null)
                        .filter(Objects::nonNull)
                        .toList();

                boadresser.forEach(boadresse -> {
                    if (!hovedperson.getBostedsadresse().isEmpty() &&
                            !isEqualAdresse(hovedperson.getBostedsadresse().getFirst(), boadresse)) {

                        setAdresse(deltBosted, boadresse);
                    }
                });
            }
        }
    }

    private List<PersonDTO> getForeldre(PersonDTO person) {

            return personRepository.findByIdentIn(
                    Stream.of(Stream.of(person.getIdent()), person.getSivilstand().stream()
                            .filter(SivilstandDTO::isGiftOrSamboer)
                            .map(SivilstandDTO::getRelatertVedSivilstand))
                    .flatMap(Function.identity())
                    .toList(), Pageable.unpaged()).stream()
                    .map(DbPerson::getPerson)
                    .toList();
    }

    private void setAdresse(DeltBostedDTO deltBosted, BostedadresseDTO boadresse) {

        deltBosted.setMaster(DbVersjonDTO.Master.FREG);
        deltBosted.setKilde(getKilde(deltBosted));
        deltBosted.setAdresseIdentifikatorFraMatrikkelen(boadresse.getAdresseIdentifikatorFraMatrikkelen());

        if (nonNull(boadresse.getVegadresse())) {
            deltBosted.setVegadresse(mapperFacade.map(boadresse.getVegadresse(), VegadresseDTO.class));

        } else if (nonNull(boadresse.getMatrikkeladresse())) {
            deltBosted.setMatrikkeladresse(mapperFacade.map(boadresse.getMatrikkeladresse(), MatrikkeladresseDTO.class));

        } else if (nonNull(boadresse.getUkjentBosted())) {
            deltBosted.setUkjentBosted(mapperFacade.map(boadresse.getUkjentBosted(), UkjentBostedDTO.class));
        }
    }

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
                            deltBosted.setStartdatoForKontrakt(
                                    FoedselsdatoUtility.getFoedselsdato(dbPerson.getPerson()));
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
                    adresseServiceConsumer.getVegadresse(deltBosted.getVegadresse(), deltBosted.getAdresseIdentifikatorFraMatrikkelen()).block();
            deltBosted.setAdresseIdentifikatorFraMatrikkelen(vegadresse.getMatrikkelId());
            mapperFacade.map(vegadresse, deltBosted.getVegadresse());

        } else if (nonNull(deltBosted.getMatrikkeladresse())) {

            var matrikkeladresse =
                    adresseServiceConsumer.getMatrikkeladresse(deltBosted.getMatrikkeladresse(), deltBosted.getAdresseIdentifikatorFraMatrikkelen()).block();
            deltBosted.setAdresseIdentifikatorFraMatrikkelen(matrikkeladresse.getMatrikkelId());
            mapperFacade.map(matrikkeladresse, deltBosted.getMatrikkeladresse());
        }
    }

    private void setDeltBosted(PersonDTO barn, DeltBostedDTO deltBosted) {

        deltBosted.setId(barn.getDeltBosted().stream()
                .findFirst()
                .map(DeltBostedDTO::getId)
                .orElse(0) + 1);

        deltBosted.setKilde(getKilde(deltBosted));
        deltBosted.setMaster(getMaster(deltBosted, IdenttypeUtility.getIdenttype(barn.getIdent())));
        barn.getDeltBosted().addFirst(deltBosted);
    }

    @Override
    public void validate(DeltBostedDTO artifact, PersonDTO person) {

        List<String> foreldre;
        if (LocalDateTime.now().isAfter(FoedselsdatoUtility.getMyndighetsdato(person))) {

            foreldre = Stream.of(Stream.of(person.getIdent()), person.getSivilstand().stream()
                            .filter(SivilstandDTO::isGiftOrSamboer)
                            .map(SivilstandDTO::getRelatertVedSivilstand))
                    .flatMap(Function.identity())
                    .toList();
        } else {

            foreldre = person.getForelderBarnRelasjon().stream()
                    .filter(ForelderBarnRelasjonDTO::isBarn)
                    .map(ForelderBarnRelasjonDTO::getIdentForRelasjon)
                    .toList();
        }

        if (foreldre.size() != 2) {
            throw new InvalidRequestException(VALIDATION_TO_FORELDRE_ERROR);
        }

        var adresser = personRepository.findByIdentIn(foreldre, Pageable.unpaged()).stream()
                .map(DbPerson::getPerson)
                .map(PersonDTO::getBostedsadresse)
                .map(adresse -> !adresse.isEmpty() ? adresse.getFirst() : null)
                .filter(Objects::nonNull)
                .toList();

        if (artifact.countAdresser() > 1 ||
                (adresser.size() == 2 && isEqualAdresse(adresser.get(0), adresser.get(1)))) {
            throw new InvalidRequestException(VALIDATION_ADRESSER_ERROR);
        }
    }
}