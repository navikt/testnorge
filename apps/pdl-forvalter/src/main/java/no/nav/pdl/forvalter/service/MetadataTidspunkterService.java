package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DeltBostedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedsfallDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FalskIdentitetDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class MetadataTidspunkterService {

    private final PersonRepository personRepository;

    @Transactional
    public void updateMetadata(String ident) {

        var person = personRepository.findByIdent(ident);

        if (person.isPresent()) {

            fixPerson(person.get().getPerson());
            person.get().getRelasjoner().stream()
                    .map(DbRelasjon::getPerson)
                    .map(DbPerson::getPerson)
                    .forEach(this::fixPerson);
        }
    }

    private void fixPerson(PersonDTO person) {

        person.getAdressebeskyttelse()
                .forEach(this::fixVersioning);
        person.getBostedsadresse()
                .forEach(this::fixAdresser);
        person.getDeltBosted()
                .forEach(this::fixDeltBosted);
        person.getDoedfoedtBarn()
                .forEach(this::fixVersioning);
        person.getDoedsfall()
                .forEach(this::fixDoedsfall);
        person.getFoedsel()
                .forEach(this::fixFoedsel);
        person.getFalskIdentitet()
                .forEach(this::fixFalskIdentitet);
        person.getFolkeregisterPersonstatus()
                .forEach(this::fixFolkerregisterPersonstatus);
        person.getForelderBarnRelasjon()
                .forEach(this::fixVersioning);
        person.getForeldreansvar()
                .forEach(this::fixForeldreansvar);
        person.getFullmakt()
                .forEach(this::fixFullmakt);
        person.getInnflytting()
                .forEach(this::fixInnflytting);
        person.getKjoenn()
                .forEach(kjoenn -> fixKjoenn(kjoenn, person));
        person.getKontaktadresse()
                .forEach(this::fixAdresser);
        person.getKontaktinformasjonForDoedsbo()
                .forEach(this::fixVersioning);
        person.getNavn()
                .forEach(navn -> fixNavn(navn, person));
        person.getOpphold()
                .forEach(this::fixOpphold);
        person.getOppholdsadresse()
                .forEach(this::fixAdresser);
        person.getSikkerhetstiltak()
                .forEach(this::fixSikkerhetstiltak);
        person.getSivilstand()
                .forEach(this::fixSivilstand);
        person.getStatsborgerskap()
                .forEach(this::fixStatsborgerskap);
        person.getTelefonnummer()
                .forEach(this::fixVersioning);
        person.getTilrettelagtKommunikasjon()
                .forEach(this::fixVersioning);
        person.getUtenlandskIdentifikasjonsnummer()
                .forEach(this::fixVersioning);
        person.getUtflytting()
                .forEach(this::fixUtflytting);
        person.getVergemaal()
                .forEach(this::fixVersioning);
    }

    private void fixKjoenn(KjoennDTO kjoennDTO, PersonDTO personDTO) {

        fixFolkeregisterMetadata(kjoennDTO);

        if (kjoennDTO.getId() == 1) {

            kjoennDTO.getFolkeregistermetadata().setAjourholdstidspunkt(personDTO.getFoedsel().stream()
                    .map(FoedselDTO::getFoedselsdato)
                    .findFirst().orElse(LocalDateTime.now().minusHours(1)));

        } else {
            kjoennDTO.getFolkeregistermetadata().setAjourholdstidspunkt(LocalDateTime.now().minusMinutes(1)
                    .plusSeconds(kjoennDTO.getId()));
        }

        kjoennDTO.getFolkeregistermetadata()
                .setGyldighetstidspunkt(kjoennDTO.getFolkeregistermetadata().getAjourholdstidspunkt());
    }

    private void fixForeldreansvar(ForeldreansvarDTO foreldreansvarDTO) {

        fixFolkeregisterMetadata(foreldreansvarDTO);

        foreldreansvarDTO.getFolkeregistermetadata().setAjourholdstidspunkt(foreldreansvarDTO.getGyldigFraOgMed());
        foreldreansvarDTO.getFolkeregistermetadata().setGyldighetstidspunkt(foreldreansvarDTO.getGyldigFraOgMed());
        foreldreansvarDTO.getFolkeregistermetadata().setOpphoerstidspunkt(foreldreansvarDTO.getGyldigTilOgMed());
    }

    private void fixFolkerregisterPersonstatus(FolkeregisterPersonstatusDTO personstatusDTO) {

        fixFolkeregisterMetadata(personstatusDTO);

        personstatusDTO.getFolkeregistermetadata().setAjourholdstidspunkt(personstatusDTO.getGyldigFraOgMed());
        personstatusDTO.getFolkeregistermetadata().setGyldighetstidspunkt(personstatusDTO.getGyldigFraOgMed());
        personstatusDTO.getFolkeregistermetadata().setOpphoerstidspunkt(personstatusDTO.getGyldigTilOgMed());
    }

    private void fixFalskIdentitet(FalskIdentitetDTO falskIdentitetDTO) {

        fixFolkeregisterMetadata(falskIdentitetDTO);

        falskIdentitetDTO.getFolkeregistermetadata().setAjourholdstidspunkt(falskIdentitetDTO.getGyldigFraOgMed());
        falskIdentitetDTO.getFolkeregistermetadata().setGyldighetstidspunkt(falskIdentitetDTO.getGyldigFraOgMed());
        falskIdentitetDTO.getFolkeregistermetadata().setOpphoerstidspunkt(falskIdentitetDTO.getGyldigTilOgMed());
    }

    private void fixDeltBosted(DeltBostedDTO deltBostedDTO) {

        fixFolkeregisterMetadata(deltBostedDTO);

        deltBostedDTO.getFolkeregistermetadata().setAjourholdstidspunkt(deltBostedDTO.getStartdatoForKontrakt());
        deltBostedDTO.getFolkeregistermetadata().setGyldighetstidspunkt(deltBostedDTO.getStartdatoForKontrakt());
        deltBostedDTO.getFolkeregistermetadata().setOpphoerstidspunkt(deltBostedDTO.getSluttdatoForKontrakt());
    }

    private void fixFullmakt(FullmaktDTO fullmaktDTO) {

        fixFolkeregisterMetadata(fullmaktDTO);

        fullmaktDTO.getFolkeregistermetadata().setAjourholdstidspunkt(fullmaktDTO.getGyldigFraOgMed());
        fullmaktDTO.getFolkeregistermetadata().setGyldighetstidspunkt(fullmaktDTO.getGyldigFraOgMed());
        fullmaktDTO.getFolkeregistermetadata().setOpphoerstidspunkt(fullmaktDTO.getGyldigTilOgMed());
    }

    private void fixOpphold(OppholdDTO oppholdDTO) {

        fixFolkeregisterMetadata(oppholdDTO);

        oppholdDTO.getFolkeregistermetadata().setAjourholdstidspunkt(oppholdDTO.getOppholdFra());
        oppholdDTO.getFolkeregistermetadata().setGyldighetstidspunkt(oppholdDTO.getOppholdFra());
        oppholdDTO.getFolkeregistermetadata().setOpphoerstidspunkt(oppholdDTO.getOppholdTil());
    }

    private void fixSikkerhetstiltak(SikkerhetstiltakDTO sikkerhetstiltakDTO) {

        fixFolkeregisterMetadata(sikkerhetstiltakDTO);

        sikkerhetstiltakDTO.getFolkeregistermetadata().setAjourholdstidspunkt(sikkerhetstiltakDTO.getGyldigFraOgMed());
        sikkerhetstiltakDTO.getFolkeregistermetadata().setGyldighetstidspunkt(sikkerhetstiltakDTO.getGyldigFraOgMed());
        sikkerhetstiltakDTO.getFolkeregistermetadata().setOpphoerstidspunkt(sikkerhetstiltakDTO.getGyldigTilOgMed());
    }

    private void fixSivilstand(SivilstandDTO sivilstandDTO) {

        fixFolkeregisterMetadata(sivilstandDTO);

        sivilstandDTO.getFolkeregistermetadata().setGyldighetstidspunkt(nonNull(sivilstandDTO.getSivilstandsdato()) ?
                sivilstandDTO.getSivilstandsdato() : LocalDateTime.now().minusSeconds(10).plusSeconds(sivilstandDTO.getId()));
        sivilstandDTO.getFolkeregistermetadata()
                .setAjourholdstidspunkt(sivilstandDTO.getFolkeregistermetadata().getGyldighetstidspunkt());
    }

    private void fixStatsborgerskap(StatsborgerskapDTO statsborgerskapDTO) {

        fixFolkeregisterMetadata(statsborgerskapDTO);

        statsborgerskapDTO.getFolkeregistermetadata().setAjourholdstidspunkt(statsborgerskapDTO.getGyldigFraOgMed());
        statsborgerskapDTO.getFolkeregistermetadata().setGyldighetstidspunkt(statsborgerskapDTO.getGyldigFraOgMed());
        statsborgerskapDTO.getFolkeregistermetadata().setOpphoerstidspunkt(statsborgerskapDTO.getGyldigTilOgMed());
    }

    private void fixVersioning(DbVersjonDTO artifact) {

        fixFolkeregisterMetadata(artifact);

        artifact.getFolkeregistermetadata().setAjourholdstidspunkt(LocalDateTime.now().minusMinutes(1)
                .plusSeconds(artifact.getId()));
        artifact.getFolkeregistermetadata().setGyldighetstidspunkt(LocalDateTime.now().minusMinutes(1)
                .plusSeconds(artifact.getId()));
    }

    private void fixInnflytting(InnflyttingDTO innflyttingDTO) {

        fixFolkeregisterMetadata(innflyttingDTO);

        innflyttingDTO.getFolkeregistermetadata().setAjourholdstidspunkt(innflyttingDTO.getInnflyttingsdato());
        innflyttingDTO.getFolkeregistermetadata().setGyldighetstidspunkt(innflyttingDTO.getInnflyttingsdato());
    }

    private void fixUtflytting(UtflyttingDTO utflyttingDTO) {

        fixFolkeregisterMetadata(utflyttingDTO);

        utflyttingDTO.getFolkeregistermetadata().setAjourholdstidspunkt(utflyttingDTO.getUtflyttingsdato());
        utflyttingDTO.getFolkeregistermetadata().setGyldighetstidspunkt(utflyttingDTO.getUtflyttingsdato());
    }

    private void fixNavn(NavnDTO navnDTO, PersonDTO personDTO) {

        fixFolkeregisterMetadata(navnDTO);

        if (navnDTO.getId() == 1) {

            navnDTO.setGyldigFraOgMed(personDTO.getFoedsel().stream()
                    .map(FoedselDTO::getFoedselsdato)
                    .findFirst().orElse(LocalDateTime.now().minusHours(1)));

        } else {
            navnDTO.setGyldigFraOgMed(LocalDateTime.now().minusMinutes(1).plusSeconds(navnDTO.getId()));
        }

        navnDTO.getFolkeregistermetadata().setAjourholdstidspunkt(navnDTO.getGyldigFraOgMed());
        navnDTO.getFolkeregistermetadata().setGyldighetstidspunkt(navnDTO.getGyldigFraOgMed());
    }

    private void fixDoedsfall(DoedsfallDTO doedsfallDTO) {

        fixFolkeregisterMetadata(doedsfallDTO);

        doedsfallDTO.getFolkeregistermetadata().setAjourholdstidspunkt(doedsfallDTO.getDoedsdato());
        doedsfallDTO.getFolkeregistermetadata().setGyldighetstidspunkt(doedsfallDTO.getDoedsdato());
    }

    private void fixFoedsel(FoedselDTO foedselDTO) {

        fixFolkeregisterMetadata(foedselDTO);

        foedselDTO.getFolkeregistermetadata().setAjourholdstidspunkt(foedselDTO.getFoedselsdato());
        foedselDTO.getFolkeregistermetadata().setGyldighetstidspunkt(foedselDTO.getFoedselsdato());
    }

    private void fixAdresser(AdresseDTO adresseDTO) {

        fixFolkeregisterMetadata(adresseDTO);

        if (nonNull(adresseDTO.getGyldigFraOgMed())) {
            adresseDTO.getFolkeregistermetadata().setAjourholdstidspunkt(adresseDTO.getGyldigFraOgMed());
            adresseDTO.getFolkeregistermetadata().setGyldighetstidspunkt(adresseDTO.getGyldigFraOgMed());
        }

        if (nonNull(adresseDTO.getGyldigTilOgMed())) {
            adresseDTO.getFolkeregistermetadata().setOpphoerstidspunkt(adresseDTO.getGyldigTilOgMed());
        }
    }

    private void fixFolkeregisterMetadata(DbVersjonDTO artifact) {

        if (isNull(artifact.getFolkeregistermetadata())) {
            artifact.setFolkeregistermetadata(new FolkeregistermetadataDTO());
        }
    }
}
