package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class ArtifactDeleteService {

    private static final String IDENT_NOT_FOUND = "Person med ident: %s ble ikke funnet";
    private static final String INFO_NOT_FOUND = "%s med id: %s ble ikke funnet";

    private final PersonRepository personRepository;

    @Transactional
    public void deleteFoedsel(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        if (dbPerson.getPerson().getFoedsel().stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new InvalidRequestException(format(INFO_NOT_FOUND, "Foedsel", id));

        } else {
            dbPerson.getPerson().setFoedsel(dbPerson.getPerson().getFoedsel().stream()
                    .filter(type -> !id.equals(type.getId()))
                    .toList());
        }
    }

    @Transactional
    public void deleteNavn(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        if (dbPerson.getPerson().getNavn().stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new InvalidRequestException(format(INFO_NOT_FOUND, "Navn", id));

        } else {
            dbPerson.getPerson().setNavn(dbPerson.getPerson().getNavn().stream()
                    .filter(type -> !id.equals(type.getId()))
                    .toList());
        }
    }

    @Transactional
    public void deleteKjoenn(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        if (dbPerson.getPerson().getKjoenn().stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new InvalidRequestException(format(INFO_NOT_FOUND, "Kjoenn", id));

        } else {
            dbPerson.getPerson().setKjoenn(dbPerson.getPerson().getKjoenn().stream()
                    .filter(type -> !id.equals(type.getId()))
                    .toList());
        }
    }

    @Transactional
    public void deleteBostedsadresse(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        if (dbPerson.getPerson().getBostedsadresse().stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new InvalidRequestException(format(INFO_NOT_FOUND, "Bostedsadresse", id));

        } else {
            dbPerson.getPerson().setBostedsadresse(dbPerson.getPerson().getBostedsadresse().stream()
                    .filter(type -> !id.equals(type.getId()))
                    .toList());
        }
    }

    @Transactional
    public void deleteKontaktadresse(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        if (dbPerson.getPerson().getKontaktadresse().stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new InvalidRequestException(format(INFO_NOT_FOUND, "Kontaktadresse", id));

        } else {
            dbPerson.getPerson().setKontaktadresse(dbPerson.getPerson().getKontaktadresse().stream()
                    .filter(type -> !id.equals(type.getId()))
                    .toList());
        }
    }

    @Transactional
    public void deleteOppholdsadresse(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        if (dbPerson.getPerson().getOppholdsadresse().stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new InvalidRequestException(format(INFO_NOT_FOUND, "Oppholdsadresse", id));

        } else {
            dbPerson.getPerson().setOppholdsadresse(dbPerson.getPerson().getOppholdsadresse().stream()
                    .filter(type -> !id.equals(type.getId()))
                    .toList());
        }
    }

    @Transactional
    public void deleteInnflytting(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        if (dbPerson.getPerson().getInnflytting().stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new InvalidRequestException(format(INFO_NOT_FOUND, "Innflytting", id));

        } else {
            dbPerson.getPerson().setInnflytting(dbPerson.getPerson().getInnflytting().stream()
                    .filter(type -> !id.equals(type.getId()))
                    .toList());
        }
    }

    @Transactional
    public void deleteUtflytting(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        if (dbPerson.getPerson().getUtflytting().stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new InvalidRequestException(format(INFO_NOT_FOUND, "Utflyttin", id));

        } else {
            dbPerson.getPerson().setUtflytting(dbPerson.getPerson().getUtflytting().stream()
                    .filter(type -> !id.equals(type.getId()))
                    .toList());
        }
    }

    @Transactional
    public void deleteDeltBosted(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        if (dbPerson.getPerson().getDeltBosted().stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new InvalidRequestException(format(INFO_NOT_FOUND, "DeltBosted", id));

        } else {
            dbPerson.getPerson().setDeltBosted(dbPerson.getPerson().getDeltBosted().stream()
                    .filter(type -> !id.equals(type.getId()))
                    .toList());
        }
    }

    @Transactional
    public void deleteForelderBarnRelasjon(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        if (dbPerson.getPerson().getForelderBarnRelasjon().stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new InvalidRequestException(format(INFO_NOT_FOUND, "ForelderBarnRelasjon", id));

        } else {
            dbPerson.getPerson().setForelderBarnRelasjon(dbPerson.getPerson().getForelderBarnRelasjon().stream()
                    .filter(type -> !id.equals(type.getId()))
                    .toList());
        }
    }

    @Transactional
    public void deleteKontaktinformasjonForDoedsbo(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        if (dbPerson.getPerson().getKontaktinformasjonForDoedsbo().stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new InvalidRequestException(format(INFO_NOT_FOUND, "KontaktinformasjonForDoedsbo", id));

        } else {

            dbPerson.getPerson().setKontaktinformasjonForDoedsbo(
                    dbPerson.getPerson().getKontaktinformasjonForDoedsbo().stream()
                            .filter(type -> !id.equals(type.getId()))
                            .toList());
        }
    }

    @Transactional
    public void deleteUtenlandskIdentifikasjonsnummer(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        if (dbPerson.getPerson().getUtenlandskIdentifikasjonsnummer().stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new InvalidRequestException(format(INFO_NOT_FOUND, "UtenlandskIdentifikasjonsnummer", id));

        } else {
            dbPerson.getPerson().setUtenlandskIdentifikasjonsnummer(dbPerson.getPerson().getUtenlandskIdentifikasjonsnummer().stream()
                    .filter(type -> !id.equals(type.getId()))
                    .toList());
        }
    }

    @Transactional
    public void deleteFalskIdentitet(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        if (dbPerson.getPerson().getFalskIdentitet().stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new InvalidRequestException(format(INFO_NOT_FOUND, "FalskIdentitet", id));

        } else {
            dbPerson.getPerson().setFalskIdentitet(dbPerson.getPerson().getFalskIdentitet().stream()
                    .filter(type -> !id.equals(type.getId()))
                    .toList());
        }
    }

    @Transactional
    public void deleteAdressebeskyttelse(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        if (dbPerson.getPerson().getAdressebeskyttelse().stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new InvalidRequestException(format(INFO_NOT_FOUND, "Adressebeskyttelse", id));

        } else {
            dbPerson.getPerson().setAdressebeskyttelse(dbPerson.getPerson().getAdressebeskyttelse().stream()
                    .filter(type -> !id.equals(type.getId()))
                    .toList());
        }
    }

    @Transactional
    public void deleteDoedsfall(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        if (dbPerson.getPerson().getDoedsfall().stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new InvalidRequestException(format(INFO_NOT_FOUND, "Doedsfall", id));

        } else {
            dbPerson.getPerson().setDoedsfall(dbPerson.getPerson().getDoedsfall().stream()
                    .filter(type -> !id.equals(type.getId()))
                    .toList());
        }
    }

    @Transactional
    public void deleteFolkeregisterPersonstatus(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        if (dbPerson.getPerson().getFolkeregisterPersonstatus().stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new InvalidRequestException(format(INFO_NOT_FOUND, "FolkeregisterPersonstatus", id));

        } else {
            dbPerson.getPerson().setFolkeregisterpersonstatus(dbPerson.getPerson().getFolkeregisterPersonstatus().stream()
                    .filter(type -> !id.equals(type.getId()))
                    .toList());
        }
    }

    @Transactional
    public void deleteTilrettelagtKommunikasjon(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        if (dbPerson.getPerson().getTilrettelagtKommunikasjon().stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new InvalidRequestException(format(INFO_NOT_FOUND, "TilrettelagtKommunikasjon", id));

        } else {
            dbPerson.getPerson().setTilrettelagtKommunikasjon(dbPerson.getPerson().getTilrettelagtKommunikasjon().stream()
                    .filter(type -> !id.equals(type.getId()))
                    .toList());
        }
    }

    @Transactional
    public void deleteStatsborgerskap(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        if (dbPerson.getPerson().getStatsborgerskap().stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new InvalidRequestException(format(INFO_NOT_FOUND, "Statsborgerskap", id));

        } else {
            dbPerson.getPerson().setStatsborgerskap(dbPerson.getPerson().getStatsborgerskap().stream()
                    .filter(type -> !id.equals(type.getId()))
                    .toList());
        }
    }

    @Transactional
    public void deleteOpphold(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        if (dbPerson.getPerson().getOpphold().stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new InvalidRequestException(format(INFO_NOT_FOUND, "Opphold", id));

        } else {
            dbPerson.getPerson().setOpphold(dbPerson.getPerson().getOpphold().stream()
                    .filter(type -> !id.equals(type.getId()))
                    .toList());
        }
    }

    @Transactional
    public void deleteSivilstand(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        if (dbPerson.getPerson().getSivilstand().stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new InvalidRequestException(format(INFO_NOT_FOUND, "Sivilstand", id));

        } else {
            dbPerson.getPerson().setSivilstand(dbPerson.getPerson().getSivilstand().stream()
                    .filter(type -> !id.equals(type.getId()))
                    .toList());
        }
    }

    @Transactional
    public void deleteTelefonnummer(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        if (dbPerson.getPerson().getTelefonnummer().stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new InvalidRequestException(format(INFO_NOT_FOUND, "Telefonnummer", id));

        } else {
            dbPerson.getPerson().setTelefonnummer(dbPerson.getPerson().getTelefonnummer().stream()
                    .filter(type -> !id.equals(type.getId()))
                    .toList());
        }
    }

    @Transactional
    public void deleteFullmakt(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        if (dbPerson.getPerson().getFullmakt().stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new InvalidRequestException(format(INFO_NOT_FOUND, "Fullmakt", id));

        } else {
            dbPerson.getPerson().setFullmakt(dbPerson.getPerson().getFullmakt().stream()
                    .filter(type -> !id.equals(type.getId()))
                    .toList());
        }
    }

    @Transactional
    public void deleteVergemaal(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        if (dbPerson.getPerson().getVergemaal().stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new InvalidRequestException(format(INFO_NOT_FOUND, "Vergemaal", id));

        } else {
            dbPerson.getPerson().setVergemaal(dbPerson.getPerson().getVergemaal().stream()
                    .filter(type -> !id.equals(type.getId()))
                    .toList());
        }
    }

    @Transactional
    public void deleteDoedfoedtBarn(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        if (dbPerson.getPerson().getDoedfoedtBarn().stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new InvalidRequestException(format(INFO_NOT_FOUND, "DoedfoedtBarn", id));

        } else {
            dbPerson.getPerson().setDoedfoedtBarn(dbPerson.getPerson().getDoedfoedtBarn().stream()
                    .filter(type -> !id.equals(type.getId()))
                    .toList());
        }
    }

    private DbPerson fetchPerson(String ident) {

        return personRepository.findByIdent(ident)
                .orElseThrow(() -> new NotFoundException(format(IDENT_NOT_FOUND, ident)));
    }
}
