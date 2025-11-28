package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.utils.DatoFraIdentUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DeltBostedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedsfallDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FalskIdentitetDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselsdatoDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregistermetadataDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SikkerhetstiltakDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtflyttingDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class MetadataTidspunkterService {

    private final PersonRepository personRepository;
    private final MapperFacade mapperFacade;

    public void updateMetadata(String ident) {

        fixPerson(ident);
        personRepository.findByIdent(ident)
                .ifPresent(dbPerson -> dbPerson.getRelasjoner().stream()
                        .map(DbRelasjon::getRelatertPerson)
                        .map(DbPerson::getIdent)
                        .forEach(this::fixPerson));
    }

    private void fixPerson(String ident) {

        personRepository.findByIdent(ident)
                .ifPresent(dbPerson -> {
                    var person = dbPerson.getPerson();

                    person.getAdressebeskyttelse()
                            .forEach(MetadataTidspunkterService::fixVersioning);
                    person.getBostedsadresse()
                            .forEach(MetadataTidspunkterService::fixAdresser);
                    fixAddrOpphoert(person.getBostedsadresse());
                    person.getOppholdsadresse()
                            .forEach(MetadataTidspunkterService::fixAdresser);
                    fixAddrOpphoert(person.getOppholdsadresse());
                    person.getKontaktadresse()
                            .forEach(MetadataTidspunkterService::fixAdresser);
                    fixAddrOpphoert(person.getKontaktadresse());
                    person.getDeltBosted()
                            .forEach(MetadataTidspunkterService::fixDeltBosted);
                    person.getDoedfoedtBarn()
                            .forEach(MetadataTidspunkterService::fixVersioning);
                    person.getDoedsfall()
                            .forEach(MetadataTidspunkterService::fixDoedsfall);
                    person.getFoedsel()
                            .forEach(foedsel -> fixFoedsel(foedsel, person));
                    person.getFoedselsdato()
                            .forEach(foedselsdato -> fixFoedsel(foedselsdato, person));
                    person.getFoedested()
                            .forEach(MetadataTidspunkterService::fixVersioning);
                    person.getFalskIdentitet()
                            .forEach(MetadataTidspunkterService::fixFalskIdentitet);
                    person.getFolkeregisterPersonstatus()
                            .forEach(MetadataTidspunkterService::fixFolkerregisterPersonstatus);
                    person.getForelderBarnRelasjon()
                            .forEach(MetadataTidspunkterService::fixVersioning);
                    person.getForeldreansvar()
                            .forEach(MetadataTidspunkterService::fixForeldreansvar);
                    person.getFullmakt()
                            .forEach(MetadataTidspunkterService::fixFullmakt);
                    person.getInnflytting()
                            .forEach(MetadataTidspunkterService::fixInnflytting);
                    fixOpphoert(person.getInnflytting());
                    person.getKjoenn()
                            .forEach(kjoenn -> fixKjoenn(kjoenn, person));
                    person.getKontaktinformasjonForDoedsbo()
                            .forEach(MetadataTidspunkterService::fixVersioning);
                    fixNavn(person);
                    person.getOpphold()
                            .forEach(MetadataTidspunkterService::fixOpphold);
                    person.getSikkerhetstiltak()
                            .forEach(MetadataTidspunkterService::fixSikkerhetstiltak);
                    fixSivilstand(person);
                    fixOpphoert(person.getSivilstand());
                    person.getStatsborgerskap()
                            .forEach(MetadataTidspunkterService::fixStatsborgerskap);
                    person.getTelefonnummer()
                            .forEach(MetadataTidspunkterService::fixVersioning);
                    person.getTilrettelagtKommunikasjon()
                            .forEach(MetadataTidspunkterService::fixVersioning);
                    person.getUtenlandskIdentifikasjonsnummer()
                            .forEach(MetadataTidspunkterService::fixVersioning);
                    person.getUtflytting()
                            .forEach(MetadataTidspunkterService::fixUtflytting);
                    fixOpphoert(person.getUtflytting());
                    person.getVergemaal()
                            .forEach(MetadataTidspunkterService::fixVersioning);
                });
    }

    private static void fixAddrOpphoert(List<? extends AdresseDTO> adresseopplysning) {

        adresseopplysning.forEach(adresse ->
                adresse.getFolkeregistermetadata().setOpphoerstidspunkt(adresse.getGyldigTilOgMed()));
    }

    private static void fixOpphoert(List<? extends DbVersjonDTO> opplysningstype) {

        for (var i = opplysningstype.size() - 1; i > 0; i--) {
            opplysningstype.get(i).getFolkeregistermetadata().setOpphoerstidspunkt(
                    opplysningstype.get(i).getFolkeregistermetadata().getGyldighetstidspunkt().isAfter(opplysningstype.get(i-1).getFolkeregistermetadata().getGyldighetstidspunkt()) ?
                    subtractADay(opplysningstype.get(i - 1).getFolkeregistermetadata().getGyldighetstidspunkt()) :
                            null);
        }
    }

    private static LocalDateTime subtractADay(LocalDateTime tidspunkt) {

        return nonNull(tidspunkt) ? tidspunkt.minusDays(1) : null;
    }

    private static void fixKjoenn(KjoennDTO kjoennDTO, PersonDTO personDTO) {

        fixFolkeregisterMetadata(kjoennDTO);

        if (kjoennDTO.getId() == 1) {

            kjoennDTO.getFolkeregistermetadata().setAjourholdstidspunkt(personDTO.getFoedsel().stream()
                    .map(foedsel -> getFoedselsdato(personDTO, foedsel))
                    .findFirst().orElse(LocalDateTime.now().minusHours(1)));

        } else {
            kjoennDTO.getFolkeregistermetadata().setAjourholdstidspunkt(LocalDateTime.now().minusMinutes(1)
                    .plusSeconds(kjoennDTO.getId()));
        }

        kjoennDTO.getFolkeregistermetadata()
                .setGyldighetstidspunkt(kjoennDTO.getFolkeregistermetadata().getAjourholdstidspunkt());
    }

    private static void fixForeldreansvar(ForeldreansvarDTO foreldreansvarDTO) {

        fixFolkeregisterMetadata(foreldreansvarDTO);

        foreldreansvarDTO.getFolkeregistermetadata().setAjourholdstidspunkt(foreldreansvarDTO.getGyldigFraOgMed());
        foreldreansvarDTO.getFolkeregistermetadata().setGyldighetstidspunkt(foreldreansvarDTO.getGyldigFraOgMed());
        foreldreansvarDTO.getFolkeregistermetadata().setOpphoerstidspunkt(foreldreansvarDTO.getGyldigTilOgMed());
    }

    private static void fixFolkerregisterPersonstatus(FolkeregisterPersonstatusDTO personstatusDTO) {

        fixFolkeregisterMetadata(personstatusDTO);

        personstatusDTO.getFolkeregistermetadata().setAjourholdstidspunkt(personstatusDTO.getGyldigFraOgMed());
        personstatusDTO.getFolkeregistermetadata().setGyldighetstidspunkt(personstatusDTO.getGyldigFraOgMed());
        personstatusDTO.getFolkeregistermetadata().setOpphoerstidspunkt(personstatusDTO.getGyldigTilOgMed());
    }

    private static void fixFalskIdentitet(FalskIdentitetDTO falskIdentitetDTO) {

        fixFolkeregisterMetadata(falskIdentitetDTO);

        falskIdentitetDTO.getFolkeregistermetadata().setAjourholdstidspunkt(falskIdentitetDTO.getGyldigFraOgMed());
        falskIdentitetDTO.getFolkeregistermetadata().setGyldighetstidspunkt(falskIdentitetDTO.getGyldigFraOgMed());
        falskIdentitetDTO.getFolkeregistermetadata().setOpphoerstidspunkt(falskIdentitetDTO.getGyldigTilOgMed());
    }

    private static void fixDeltBosted(DeltBostedDTO deltBostedDTO) {

        fixFolkeregisterMetadata(deltBostedDTO);

        deltBostedDTO.getFolkeregistermetadata().setAjourholdstidspunkt(deltBostedDTO.getStartdatoForKontrakt());
        deltBostedDTO.getFolkeregistermetadata().setGyldighetstidspunkt(deltBostedDTO.getStartdatoForKontrakt());
        deltBostedDTO.getFolkeregistermetadata().setOpphoerstidspunkt(deltBostedDTO.getSluttdatoForKontrakt());
    }

    private static void fixFullmakt(FullmaktDTO fullmaktDTO) {

        fixFolkeregisterMetadata(fullmaktDTO);

        fullmaktDTO.getFolkeregistermetadata().setAjourholdstidspunkt(fullmaktDTO.getGyldigFraOgMed());
        fullmaktDTO.getFolkeregistermetadata().setGyldighetstidspunkt(fullmaktDTO.getGyldigFraOgMed());
        fullmaktDTO.getFolkeregistermetadata().setOpphoerstidspunkt(fullmaktDTO.getGyldigTilOgMed());
    }

    private static void fixOpphold(OppholdDTO oppholdDTO) {

        fixFolkeregisterMetadata(oppholdDTO);

        oppholdDTO.getFolkeregistermetadata().setAjourholdstidspunkt(oppholdDTO.getOppholdFra());
        oppholdDTO.getFolkeregistermetadata().setGyldighetstidspunkt(oppholdDTO.getOppholdFra());
        oppholdDTO.getFolkeregistermetadata().setOpphoerstidspunkt(oppholdDTO.getOppholdTil());
    }

    private static void fixSikkerhetstiltak(SikkerhetstiltakDTO sikkerhetstiltakDTO) {

        fixFolkeregisterMetadata(sikkerhetstiltakDTO);

        sikkerhetstiltakDTO.getFolkeregistermetadata().setAjourholdstidspunkt(sikkerhetstiltakDTO.getGyldigFraOgMed());
        sikkerhetstiltakDTO.getFolkeregistermetadata().setGyldighetstidspunkt(sikkerhetstiltakDTO.getGyldigFraOgMed());
        sikkerhetstiltakDTO.getFolkeregistermetadata().setOpphoerstidspunkt(sikkerhetstiltakDTO.getGyldigTilOgMed());
    }

    private void fixSivilstand(PersonDTO person) {

        person.getSivilstand().sort(Comparator.comparing(SivilstandDTO::getId).reversed());

        var sivilstandCopy = mapperFacade.mapAsList(person.getSivilstand(), SivilstandDTO.class);
        var dato = new AtomicReference<>(LocalDateTime.now().minusYears(5));

        sivilstandCopy
                .forEach(sivilstand -> {
                    if (isNull(sivilstand.getSivilstandsdato()) && isNull(sivilstand.getBekreftelsesdato())) {

                        sivilstand.setSivilstandsdato(dato.get());
                        dato.set(dato.get().minusYears(3));

                    } else {
                        var sivilstandsdato = nonNull(sivilstand.getSivilstandsdato()) ? sivilstand.getSivilstandsdato() : sivilstand.getBekreftelsesdato();
                        sivilstand.setSivilstandsdato(sivilstandsdato);
                        dato.set(sivilstandsdato.minusYears(3));
                    }
                });

        for (int i = 0; i < person.getSivilstand().size(); i++) {

            var sivilstand = person.getSivilstand().get(i);
            fixFolkeregisterMetadata(sivilstand);
            sivilstand.getFolkeregistermetadata().setAjourholdstidspunkt(sivilstandCopy.get(i).getSivilstandsdato());
            sivilstand.getFolkeregistermetadata().setGyldighetstidspunkt(sivilstandCopy.get(i).getSivilstandsdato());
        }
        fixOpphoert(person.getSivilstand());

        var doedsdato = person.getDoedsfall().stream()
                .map(DoedsfallDTO::getDoedsdato)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        if (!person.getSivilstand().isEmpty()) {
            person.getSivilstand().getFirst().getFolkeregistermetadata().setOpphoerstidspunkt(doedsdato);
        }
    }

    private static void fixStatsborgerskap(StatsborgerskapDTO statsborgerskapDTO) {

        fixFolkeregisterMetadata(statsborgerskapDTO);

        statsborgerskapDTO.getFolkeregistermetadata().setAjourholdstidspunkt(statsborgerskapDTO.getGyldigFraOgMed());
        statsborgerskapDTO.getFolkeregistermetadata().setGyldighetstidspunkt(statsborgerskapDTO.getGyldigFraOgMed());
        statsborgerskapDTO.getFolkeregistermetadata().setOpphoerstidspunkt(statsborgerskapDTO.getGyldigTilOgMed());
    }

    private static void fixVersioning(DbVersjonDTO artifact) {

        fixFolkeregisterMetadata(artifact);

        artifact.getFolkeregistermetadata().setAjourholdstidspunkt(LocalDateTime.now().minusMinutes(1)
                .plusSeconds(artifact.getId()));
        artifact.getFolkeregistermetadata().setGyldighetstidspunkt(LocalDateTime.now().minusMinutes(1)
                .plusSeconds(artifact.getId()));
    }

    private static void fixInnflytting(InnflyttingDTO innflyttingDTO) {

        fixFolkeregisterMetadata(innflyttingDTO);

        innflyttingDTO.getFolkeregistermetadata().setAjourholdstidspunkt(innflyttingDTO.getInnflyttingsdato());
        innflyttingDTO.getFolkeregistermetadata().setGyldighetstidspunkt(innflyttingDTO.getInnflyttingsdato());
    }

    private static void fixUtflytting(UtflyttingDTO utflyttingDTO) {

        fixFolkeregisterMetadata(utflyttingDTO);

        utflyttingDTO.getFolkeregistermetadata().setAjourholdstidspunkt(utflyttingDTO.getUtflyttingsdato());
        utflyttingDTO.getFolkeregistermetadata().setGyldighetstidspunkt(utflyttingDTO.getUtflyttingsdato());
    }

    private static void fixNavn(PersonDTO personDTO) {

        personDTO.getNavn()
                .forEach(navn -> {
                    fixFolkeregisterMetadata(navn);
                    navn.getFolkeregistermetadata().setGyldighetstidspunkt(navn.getGyldigFraOgMed());
                    navn.getFolkeregistermetadata().setAjourholdstidspunkt(LocalDateTime.now());
                    navn.getFolkeregistermetadata().setOpphoerstidspunkt(personDTO.getNavn().stream()
                            .filter(navn1 -> navn1.getId() == navn.getId() + 1)
                            .map(NavnDTO::getGyldigFraOgMed)
                            .findFirst()
                            .orElse(null));
                });
    }

    private static LocalDateTime getFoedselsdato(PersonDTO personDTO, FoedselsdatoDTO foedsel) {

        return nonNull(foedsel.getFoedselsdato()) ? foedsel.getFoedselsdato() :
                LocalDate.of(foedsel.getFoedselsaar(),
                        DatoFraIdentUtility.getDato(personDTO.getIdent()).getMonthValue(),
                        DatoFraIdentUtility.getDato(personDTO.getIdent()).getDayOfMonth()).atStartOfDay();
    }

    private static void fixDoedsfall(DoedsfallDTO doedsfallDTO) {

        fixFolkeregisterMetadata(doedsfallDTO);

        doedsfallDTO.getFolkeregistermetadata().setAjourholdstidspunkt(doedsfallDTO.getDoedsdato());
        doedsfallDTO.getFolkeregistermetadata().setGyldighetstidspunkt(doedsfallDTO.getDoedsdato());
    }

    private static void fixFoedsel(FoedselsdatoDTO foedselDTO, PersonDTO personDTO) {

        fixFolkeregisterMetadata(foedselDTO);

        var dato = getFoedselsdato(personDTO, foedselDTO);

        foedselDTO.getFolkeregistermetadata().setAjourholdstidspunkt(dato);
        foedselDTO.getFolkeregistermetadata().setGyldighetstidspunkt(dato);
    }

    private static void fixAdresser(AdresseDTO adresseDTO) {

        fixFolkeregisterMetadata(adresseDTO);

        if (nonNull(adresseDTO.getGyldigFraOgMed())) {
            adresseDTO.getFolkeregistermetadata().setAjourholdstidspunkt(adresseDTO.getGyldigFraOgMed());
            adresseDTO.getFolkeregistermetadata().setGyldighetstidspunkt(adresseDTO.getGyldigFraOgMed());
        }

        if (nonNull(adresseDTO.getGyldigTilOgMed())) {
            adresseDTO.getFolkeregistermetadata().setOpphoerstidspunkt(adresseDTO.getGyldigTilOgMed());
        }
    }

    private static void fixFolkeregisterMetadata(DbVersjonDTO artifact) {

        if (isNull(artifact.getFolkeregistermetadata())) {
            artifact.setFolkeregistermetadata(new FolkeregistermetadataDTO());
        }
    }
}
