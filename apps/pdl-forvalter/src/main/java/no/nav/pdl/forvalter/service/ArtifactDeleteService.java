package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.NotFoundException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class ArtifactDeleteService {

    private static final String IDENT_NOT_FOUND = "Person med ident: %s ble ikke funnet";
    private static final String INFO_NOT_FOUND = "%s med id: %s ble ikke funnet";

    private final PersonRepository personRepository;
    private final PersonService personService;

    private static <T extends DbVersjonDTO> void checkExists(List<T> artifacter, Integer id, String navn) {

        if (artifacter.stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new NotFoundException(format(INFO_NOT_FOUND, navn, id));
        }
    }

    @Transactional
    public void deleteFoedsel(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        checkExists(dbPerson.getPerson().getFoedsel(), id, "Foedsel");
        dbPerson.getPerson().setFoedsel(dbPerson.getPerson().getFoedsel().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    @Transactional
    public void deleteNavn(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        checkExists(dbPerson.getPerson().getNavn(), id, "Navn");
        dbPerson.getPerson().setNavn(dbPerson.getPerson().getNavn().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    @Transactional
    public void deleteKjoenn(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        checkExists(dbPerson.getPerson().getKjoenn(), id, "Kjoenn");
        dbPerson.getPerson().setKjoenn(dbPerson.getPerson().getKjoenn().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    @Transactional
    public void deleteBostedsadresse(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        checkExists(dbPerson.getPerson().getBostedsadresse(), id, "Bostedsadresse");
        dbPerson.getPerson().setBostedsadresse(dbPerson.getPerson().getBostedsadresse().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    @Transactional
    public void deleteKontaktadresse(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        checkExists(dbPerson.getPerson().getKontaktadresse(), id, "Kontaktadresse");
        dbPerson.getPerson().setKontaktadresse(dbPerson.getPerson().getKontaktadresse().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    @Transactional
    public void deleteOppholdsadresse(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        checkExists(dbPerson.getPerson().getOppholdsadresse(), id, "Oppholdsadresse");
        dbPerson.getPerson().setOppholdsadresse(dbPerson.getPerson().getOppholdsadresse().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    @Transactional
    public void deleteInnflytting(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        checkExists(dbPerson.getPerson().getInnflytting(), id, "Innflytting");
        dbPerson.getPerson().setInnflytting(dbPerson.getPerson().getInnflytting().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    @Transactional
    public void deleteUtflytting(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        checkExists(dbPerson.getPerson().getUtflytting(), id, "Utflytting");
        dbPerson.getPerson().setUtflytting(dbPerson.getPerson().getUtflytting().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    @Transactional
    public void deleteDeltBosted(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        checkExists(dbPerson.getPerson().getDeltBosted(), id, "DeltBosted");
        dbPerson.getPerson().setDeltBosted(dbPerson.getPerson().getDeltBosted().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    @Transactional
    public void deleteForelderBarnRelasjon(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        checkExists(dbPerson.getPerson().getForelderBarnRelasjon(), id, "ForelderBarnRelasjon");
        dbPerson.getPerson().setForelderBarnRelasjon(dbPerson.getPerson().getForelderBarnRelasjon().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    @Transactional
    public void deleteForeldreansvar(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        checkExists(dbPerson.getPerson().getForeldreansvar(), id, "Foreldreansvar");
        dbPerson.getPerson().setForeldreansvar(dbPerson.getPerson().getForeldreansvar().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    @Transactional
    public void deleteKontaktinformasjonForDoedsbo(String ident, Integer id) {

        var hovedPerson = fetchPerson(ident);

        checkExists(hovedPerson.getPerson().getKontaktinformasjonForDoedsbo(), id, "KontaktinformasjonForDoedsbo");
        hovedPerson.getPerson().getKontaktinformasjonForDoedsbo().stream()
                .filter(doedsbo -> id.equals(doedsbo.getId()) &&
                        nonNull(doedsbo.getPersonSomKontakt()) &&
                        isNotBlank(doedsbo.getPersonSomKontakt().getIdentifikasjonsnummer()))
                .forEach(doedsbo -> {
                    deleteRelasjon(hovedPerson, doedsbo.getPersonSomKontakt().getIdentifikasjonsnummer(), RelasjonType.KONTAKT_FOR_DOEDSBO);
                    deleteRelasjon(fetchPerson(doedsbo.getPersonSomKontakt().getIdentifikasjonsnummer()), hovedPerson.getIdent(), RelasjonType.AVDOEDD_FOR_KONTAKT);

                    if (isNotTrue(doedsbo.getPersonSomKontakt().getIsIdentExternal())) {
                        personService.deletePerson(doedsbo.getPersonSomKontakt().getIdentifikasjonsnummer());
                    }
                });

        hovedPerson.getPerson().setKontaktinformasjonForDoedsbo(
                hovedPerson.getPerson().getKontaktinformasjonForDoedsbo().stream()
                        .filter(type -> !id.equals(type.getId()))
                        .toList());
    }

    @Transactional
    public void deleteUtenlandskIdentifikasjonsnummer(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        checkExists(dbPerson.getPerson().getUtenlandskIdentifikasjonsnummer(), id, "UtenlandskIdentifikasjonsnummer");
        dbPerson.getPerson().setUtenlandskIdentifikasjonsnummer(dbPerson.getPerson().getUtenlandskIdentifikasjonsnummer().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    @Transactional
    public void deleteFalskIdentitet(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        checkExists(dbPerson.getPerson().getFalskIdentitet(), id, "FalskIdentitet");
        dbPerson.getPerson().setFalskIdentitet(dbPerson.getPerson().getFalskIdentitet().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    @Transactional
    public void deleteAdressebeskyttelse(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        checkExists(dbPerson.getPerson().getAdressebeskyttelse(), id, "Adressebeskyttelse");
        dbPerson.getPerson().setAdressebeskyttelse(dbPerson.getPerson().getAdressebeskyttelse().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    @Transactional
    public void deleteDoedsfall(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        checkExists(dbPerson.getPerson().getDoedsfall(), id, "Doedsfall");
        dbPerson.getPerson().setDoedsfall(dbPerson.getPerson().getDoedsfall().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    @Transactional
    public void deleteFolkeregisterPersonstatus(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        checkExists(dbPerson.getPerson().getFolkeregisterPersonstatus(), id, "FolkeregisterPersonstatus");
        dbPerson.getPerson().setFolkeregisterPersonstatus(dbPerson.getPerson().getFolkeregisterPersonstatus().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    @Transactional
    public void deleteSikkerhetstiltak(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        checkExists(dbPerson.getPerson().getSikkerhetstiltak(), id, "Sikkerhetstiltak");
        dbPerson.getPerson().setSikkerhetstiltak(dbPerson.getPerson().getSikkerhetstiltak().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    @Transactional
    public void deleteTilrettelagtKommunikasjon(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        checkExists(dbPerson.getPerson().getTilrettelagtKommunikasjon(), id, "TilrettelagtKommunikasjon");
        dbPerson.getPerson().setTilrettelagtKommunikasjon(dbPerson.getPerson().getTilrettelagtKommunikasjon().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    @Transactional
    public void deleteStatsborgerskap(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        checkExists(dbPerson.getPerson().getStatsborgerskap(), id, "Statsborgerskap");
        dbPerson.getPerson().setStatsborgerskap(dbPerson.getPerson().getStatsborgerskap().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    @Transactional
    public void deleteOpphold(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        checkExists(dbPerson.getPerson().getOpphold(), id, "Opphold");
        dbPerson.getPerson().setOpphold(dbPerson.getPerson().getOpphold().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    @Transactional
    public void deleteSivilstand(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        checkExists(dbPerson.getPerson().getSivilstand(), id, "Sivilstand");
        dbPerson.getPerson().setSivilstand(dbPerson.getPerson().getSivilstand().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    @Transactional
    public void deleteTelefonnummer(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        checkExists(dbPerson.getPerson().getTelefonnummer(), id, "Telefonnummer");
        dbPerson.getPerson().setTelefonnummer(dbPerson.getPerson().getTelefonnummer().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    @Transactional
    public void deleteFullmakt(String ident, Integer id) {

        var hovedPerson = fetchPerson(ident);

        checkExists(hovedPerson.getPerson().getFullmakt(), id, "Fullmakt");
        hovedPerson.getPerson().getFullmakt().stream()
                .filter(type -> id.equals(type.getId()))
                .forEach(fullmakt -> {
                    deleteRelasjon(hovedPerson, fullmakt.getMotpartsPersonident(), RelasjonType.FULLMEKTIG);
                    deleteRelasjon(fetchPerson(fullmakt.getMotpartsPersonident()), hovedPerson.getIdent(), RelasjonType.FULLMAKTSGIVER);

                    if (isNotTrue(fullmakt.getIsIdentExternal())) {
                        personService.deletePerson(fullmakt.getMotpartsPersonident());
                    }
                });

        hovedPerson.getPerson().setFullmakt(hovedPerson.getPerson().getFullmakt().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    @Transactional
    public void deleteVergemaal(String ident, Integer id) {

        var hovedPerson = fetchPerson(ident);

        checkExists(hovedPerson.getPerson().getVergemaal(), id, "Vergemaal");
        hovedPerson.getPerson().getVergemaal().stream()
                .filter(type -> id.equals(type.getId()))
                .forEach(vergemaal -> {
                    deleteRelasjon(hovedPerson, vergemaal.getVergeIdent(), RelasjonType.VERGE);
                    deleteRelasjon(fetchPerson(vergemaal.getVergeIdent()), hovedPerson.getIdent(), RelasjonType.VERGE_MOTTAKER);

                    if (isNotTrue(vergemaal.getIsIdentExternal())) {
                        personService.deletePerson(vergemaal.getVergeIdent());
                    }
                });

        hovedPerson.getPerson().setVergemaal(hovedPerson.getPerson().getVergemaal().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    @Transactional
    public void deleteDoedfoedtBarn(String ident, Integer id) {

        var dbPerson = fetchPerson(ident);

        checkExists(dbPerson.getPerson().getDoedfoedtBarn(), id, "DoedfoedtBarn");
        dbPerson.getPerson().setDoedfoedtBarn(dbPerson.getPerson().getDoedfoedtBarn().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    private DbPerson fetchPerson(String ident) {

        return personRepository.findByIdent(ident)
                .orElseThrow(() -> new NotFoundException(format(IDENT_NOT_FOUND, ident)));
    }

    private void deleteRelasjon(DbPerson person, String relasjonIdent, RelasjonType type) {

        var relasjonIterator = person.getRelasjoner().iterator();
        while (relasjonIterator.hasNext()) {

            var thisRelasjon = relasjonIterator.next();
            if (thisRelasjon.getRelasjonType() == type && thisRelasjon.getRelatertPerson().getIdent().equals(relasjonIdent)) {
                relasjonIterator.remove();
                break;
            }
        }
    }
}
