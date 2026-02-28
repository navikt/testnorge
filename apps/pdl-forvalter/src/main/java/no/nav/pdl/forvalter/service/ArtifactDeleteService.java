package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.NotFoundException;
import no.nav.pdl.forvalter.utils.DeleteRelasjonerUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_ADRESSEBESKYTTELSE;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_BOSTEDADRESSE;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_DELTBOSTED;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_DOEDFOEDT_BARN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_DOEDSFALL;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_FALSK_IDENTITET;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_FOEDESTED;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_FOEDSEL;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_FOEDSELSDATO;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_FOLKEREGISTER_PERSONSTATUS;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_FORELDREANSVAR;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_FORELDRE_BARN_RELASJON;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_INNFLYTTING;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_KJOENN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_KONTAKTADRESSE;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_KONTAKTINFORMASJON_FOR_DODESDBO;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_NAVN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_OPPHOLD;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_OPPHOLDSADRESSE;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_SIKKERHETSTILTAK;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_SIVILSTAND;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_STATSBORGERSKAP;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_TELEFONUMMER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_TILRETTELAGT_KOMMUNIKASJON;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_UTENLANDS_IDENTIFIKASJON_NUMMER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_UTFLYTTING;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_VERGEMAAL;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.EKTEFELLE_PARTNER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FALSK_IDENTITET;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FAMILIERELASJON_FORELDER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FORELDREANSVAR_FORELDER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.KONTAKT_FOR_DOEDSBO;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Transactional
@RequiredArgsConstructor
public class ArtifactDeleteService {

    private static final String IDENT_NOT_FOUND = "Person med ident: %s ble ikke funnet";
    private static final String INFO_NOT_FOUND = "%s med id: %s ble ikke funnet";

    private final PersonRepository personRepository;
    private final PersonService personService;
    private final FolkeregisterPersonstatusService folkeregisterPersonstatusService;
    private final HendelseIdService hendelseIdService;

    private static <T extends DbVersjonDTO> void checkExists(List<T> artifacter, Integer id, String navn) {

        if (artifacter.stream().noneMatch(type -> id.equals(type.getId()))) {
            throw new NotFoundException(format(INFO_NOT_FOUND, navn, id));
        }
    }

    public Mono<Void> deleteFoedsel(String ident, Integer id) {

        var dbPerson = getPerson(ident);

        checkExists(dbPerson.getPerson().getFoedsel(), id, PDL_FOEDSEL.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_FOEDSEL.getDescription(), id);

        dbPerson.getPerson().setFoedsel(dbPerson.getPerson().getFoedsel().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    public Mono<Void> deleteFoedested(String ident, Integer id) {

        var dbPerson = getPerson(ident);

        checkExists(dbPerson.getPerson().getFoedested(), id, PDL_FOEDESTED.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_FOEDESTED.getDescription(), id);

        dbPerson.getPerson().setFoedested(dbPerson.getPerson().getFoedested().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    public Mono<Void> deleteFoedselsdato(String ident, Integer id) {

        var dbPerson = getPerson(ident);

        checkExists(dbPerson.getPerson().getFoedselsdato(), id, PDL_FOEDSELSDATO.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_FOEDSELSDATO.getDescription(), id);

        dbPerson.getPerson().setFoedselsdato(dbPerson.getPerson().getFoedselsdato().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    public Mono<Void> deleteNavn(String ident, Integer id) {

        var dbPerson = getPerson(ident);

        checkExists(dbPerson.getPerson().getNavn(), id, PDL_NAVN.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_NAVN.getDescription(), id);

        dbPerson.getPerson().setNavn(dbPerson.getPerson().getNavn().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    public Mono<Void> deleteKjoenn(String ident, Integer id) {

        var dbPerson = getPerson(ident);

        checkExists(dbPerson.getPerson().getKjoenn(), id, PDL_KJOENN.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_KJOENN.getDescription(), id);

        dbPerson.getPerson().setKjoenn(dbPerson.getPerson().getKjoenn().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    public Mono<Void> deleteBostedsadresse(String ident, Integer id) {

        var dbPerson = getPerson(ident);

        checkExists(dbPerson.getPerson().getBostedsadresse(), id, PDL_BOSTEDADRESSE.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_BOSTEDADRESSE.getDescription(), id);

        dbPerson.getPerson().setBostedsadresse(dbPerson.getPerson().getBostedsadresse().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());

        folkeregisterPersonstatusService.update(dbPerson.getPerson());
    }

    public Mono<Void> deleteKontaktadresse(String ident, Integer id) {

        var dbPerson = getPerson(ident);

        checkExists(dbPerson.getPerson().getKontaktadresse(), id, PDL_KONTAKTADRESSE.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_KONTAKTADRESSE.getDescription(), id);

        dbPerson.getPerson().setKontaktadresse(dbPerson.getPerson().getKontaktadresse().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    public Mono<Void> deleteOppholdsadresse(String ident, Integer id) {

        var dbPerson = getPerson(ident);

        checkExists(dbPerson.getPerson().getOppholdsadresse(), id, PDL_OPPHOLDSADRESSE.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_OPPHOLDSADRESSE.getDescription(), id);

        dbPerson.getPerson().setOppholdsadresse(dbPerson.getPerson().getOppholdsadresse().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    public Mono<Void> deleteInnflytting(String ident, Integer id) {

        var dbPerson = getPerson(ident);

        checkExists(dbPerson.getPerson().getInnflytting(), id, PDL_INNFLYTTING.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_INNFLYTTING.getDescription(), id);

        dbPerson.getPerson().setInnflytting(dbPerson.getPerson().getInnflytting().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());

        folkeregisterPersonstatusService.update(dbPerson.getPerson());
    }

    public Mono<Void> deleteUtflytting(String ident, Integer id) {

        var dbPerson = getPerson(ident);

        checkExists(dbPerson.getPerson().getUtflytting(), id, PDL_UTFLYTTING.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_UTFLYTTING.getDescription(), id);

        dbPerson.getPerson().setUtflytting(dbPerson.getPerson().getUtflytting().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());

        folkeregisterPersonstatusService.update(dbPerson.getPerson());
    }

    public Mono<Void> deleteDeltBosted(String ident, Integer id) {

        var dbPerson = getPerson(ident);

        checkExists(dbPerson.getPerson().getDeltBosted(), id, PDL_DELTBOSTED.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_DELTBOSTED.getDescription(), id);

        dbPerson.getPerson().setDeltBosted(dbPerson.getPerson().getDeltBosted().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    public Mono<Void> deleteForelderBarnRelasjon(String ident, Integer id) {

        var dbPerson = getPerson(ident);

        checkExists(dbPerson.getPerson().getForelderBarnRelasjon(), id, PDL_FORELDRE_BARN_RELASJON.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_FORELDRE_BARN_RELASJON.getDescription(), id);

        dbPerson.getPerson().getForelderBarnRelasjon().stream()
                .filter(type -> id.equals(type.getId()) &&
                        isNotBlank(type.getRelatertPerson()))
                .forEach(type -> {
                    var slettePerson = getPerson(type.getRelatertPerson());

                    DeleteRelasjonerUtility.deleteRelasjoner(dbPerson, slettePerson, FAMILIERELASJON_FORELDER);

                    deletePerson(slettePerson, type.isEksisterendePerson());
                });

        dbPerson.getPerson().setForelderBarnRelasjon(dbPerson.getPerson().getForelderBarnRelasjon().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    public Mono<Void> deleteForeldreansvar(String ident, Integer id) {

        var dbPerson = getPerson(ident);

        checkExists(dbPerson.getPerson().getForeldreansvar(), id, PDL_FORELDREANSVAR.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_FORELDREANSVAR.getDescription(), id);

        dbPerson.getPerson().getForeldreansvar().stream()
                .filter(type -> id.equals(type.getId()) &&
                        (isNotBlank(type.getAnsvarlig()) || isNotBlank(type.getAnsvarssubjekt())))
                .forEach(type -> {
                    var slettePerson = getPerson(isNotBlank(type.getAnsvarlig()) ?
                            type.getAnsvarlig() : type.getAnsvarssubjekt());

                    DeleteRelasjonerUtility.deleteRelasjoner(dbPerson, slettePerson, FORELDREANSVAR_FORELDER);

                    deletePerson(slettePerson, type.isEksisterendePerson());
                });

        dbPerson.getPerson().setForeldreansvar(dbPerson.getPerson().getForeldreansvar().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    public Mono<Void> deleteKontaktinformasjonForDoedsbo(String ident, Integer id) {

        var hovedPerson = getPerson(ident);

        checkExists(hovedPerson.getPerson().getKontaktinformasjonForDoedsbo(), id, PDL_KONTAKTINFORMASJON_FOR_DODESDBO.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_KONTAKTINFORMASJON_FOR_DODESDBO.getDescription(), id);

        hovedPerson.getPerson().getKontaktinformasjonForDoedsbo().stream()
                .filter(doedsbo -> id.equals(doedsbo.getId()) &&
                        nonNull(doedsbo.getPersonSomKontakt()) &&
                        isNotBlank(doedsbo.getPersonSomKontakt().getIdentifikasjonsnummer()))
                .forEach(doedsbo -> {
                    var slettePerson = getPerson(doedsbo.getPersonSomKontakt().getIdentifikasjonsnummer());

                    DeleteRelasjonerUtility.deleteRelasjoner(hovedPerson, slettePerson, KONTAKT_FOR_DOEDSBO);

                    deletePerson(slettePerson, doedsbo.getPersonSomKontakt().isEksisterendePerson());
                });

        hovedPerson.getPerson().setKontaktinformasjonForDoedsbo(
                hovedPerson.getPerson().getKontaktinformasjonForDoedsbo().stream()
                        .filter(type -> !id.equals(type.getId()))
                        .toList());
    }

    public Mono<Void> deleteUtenlandskIdentifikasjonsnummer(String ident, Integer id) {

        var dbPerson = getPerson(ident);

        checkExists(dbPerson.getPerson().getUtenlandskIdentifikasjonsnummer(), id, PDL_UTENLANDS_IDENTIFIKASJON_NUMMER.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_UTENLANDS_IDENTIFIKASJON_NUMMER.getDescription(), id);

        dbPerson.getPerson().setUtenlandskIdentifikasjonsnummer(dbPerson.getPerson().getUtenlandskIdentifikasjonsnummer().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    public Mono<Void> deleteFalskIdentitet(String ident, Integer id) {

        var person = getPerson(ident);

        checkExists(person.getPerson().getFalskIdentitet(), id, PDL_FALSK_IDENTITET.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_FALSK_IDENTITET.getDescription(), id);

        person.getPerson().getFalskIdentitet().stream()
                .filter(falskId -> id.equals(falskId.getId()) &&
                        isNotBlank(falskId.getRettIdentitetVedIdentifikasjonsnummer()))
                .forEach(falskId -> {
                    var slettePerson = getPerson(falskId.getRettIdentitetVedIdentifikasjonsnummer());

                    DeleteRelasjonerUtility.deleteRelasjoner(person, slettePerson, FALSK_IDENTITET);

                    deletePerson(slettePerson, falskId.isEksisterendePerson());
                });

        person.getPerson().setFalskIdentitet(person.getPerson().getFalskIdentitet().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());

        folkeregisterPersonstatusService.update(person.getPerson());
    }

    public Mono<Void> deleteAdressebeskyttelse(String ident, Integer id) {

        var dbPerson = getPerson(ident);

        checkExists(dbPerson.getPerson().getAdressebeskyttelse(), id, PDL_ADRESSEBESKYTTELSE.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_ADRESSEBESKYTTELSE.getDescription(), id);

        dbPerson.getPerson().setAdressebeskyttelse(dbPerson.getPerson().getAdressebeskyttelse().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    public Mono<Void> deleteDoedsfall(String ident, Integer id) {

        var dbPerson = getPerson(ident);

        checkExists(dbPerson.getPerson().getDoedsfall(), id, PDL_DOEDSFALL.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_DOEDSFALL.getDescription(), id);

        dbPerson.getPerson().setDoedsfall(dbPerson.getPerson().getDoedsfall().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());

        folkeregisterPersonstatusService.update(dbPerson.getPerson());
    }

    public Mono<Void> deleteFolkeregisterPersonstatus(String ident, Integer id) {

        var dbPerson = getPerson(ident);

        checkExists(dbPerson.getPerson().getFolkeregisterPersonstatus(), id, PDL_FOLKEREGISTER_PERSONSTATUS.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_FOLKEREGISTER_PERSONSTATUS.getDescription(), id);

        dbPerson.getPerson().setFolkeregisterPersonstatus(dbPerson.getPerson().getFolkeregisterPersonstatus().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());

        FolkeregisterPersonstatusService.setGyldigTilOgMed(dbPerson.getPerson());
    }

    public Mono<Void> deleteSikkerhetstiltak(String ident, Integer id) {

        var dbPerson = getPerson(ident);

        checkExists(dbPerson.getPerson().getSikkerhetstiltak(), id, PDL_SIKKERHETSTILTAK.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_SIKKERHETSTILTAK.getDescription(), id);

        dbPerson.getPerson().setSikkerhetstiltak(dbPerson.getPerson().getSikkerhetstiltak().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    public Mono<Void> deleteTilrettelagtKommunikasjon(String ident, Integer id) {

        var dbPerson = getPerson(ident);

        checkExists(dbPerson.getPerson().getTilrettelagtKommunikasjon(), id, PDL_TILRETTELAGT_KOMMUNIKASJON.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_TILRETTELAGT_KOMMUNIKASJON.getDescription(), id);

        dbPerson.getPerson().setTilrettelagtKommunikasjon(dbPerson.getPerson().getTilrettelagtKommunikasjon().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    public Mono<Void> deleteStatsborgerskap(String ident, Integer id) {

        var dbPerson = getPerson(ident);

        checkExists(dbPerson.getPerson().getStatsborgerskap(), id, PDL_STATSBORGERSKAP.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_STATSBORGERSKAP.getDescription(), id);

        dbPerson.getPerson().setStatsborgerskap(dbPerson.getPerson().getStatsborgerskap().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    public Mono<Void> deleteOpphold(String ident, Integer id) {

        var dbPerson = getPerson(ident);

        checkExists(dbPerson.getPerson().getOpphold(), id, PDL_OPPHOLD.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_OPPHOLD.getDescription(), id);

        dbPerson.getPerson().setOpphold(dbPerson.getPerson().getOpphold().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    public Mono<Void> deleteSivilstand(String ident, Integer id) {

        var dbPerson = getPerson(ident);

        checkExists(dbPerson.getPerson().getSivilstand(), id, PDL_SIVILSTAND.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_SIVILSTAND.getDescription(), id);

        dbPerson.getPerson().getSivilstand().stream()
                .filter(type -> id.equals(type.getId()) &&
                        isNotBlank(type.getRelatertVedSivilstand()))
                .forEach(type -> {
                    var slettePerson = getPerson(type.getRelatertVedSivilstand());

                    DeleteRelasjonerUtility.deleteRelasjoner(dbPerson, slettePerson, EKTEFELLE_PARTNER);

                    deletePerson(slettePerson, type.isEksisterendePerson());
                });

        dbPerson.getPerson().setSivilstand(dbPerson.getPerson().getSivilstand().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    public Mono<Void> deleteTelefonnummer(String ident, Integer id) {

        var dbPerson = getPerson(ident);

        checkExists(dbPerson.getPerson().getTelefonnummer(), id, PDL_TELEFONUMMER.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_TELEFONUMMER.getDescription(), id);

        dbPerson.getPerson().setTelefonnummer(dbPerson.getPerson().getTelefonnummer().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    public Mono<Void> deleteVergemaal(String ident, Integer id) {

        var hovedPerson = getPerson(ident);

        checkExists(hovedPerson.getPerson().getVergemaal(), id, PDL_VERGEMAAL.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_VERGEMAAL.getDescription(), id);

        hovedPerson.getPerson().getVergemaal().stream()
                .filter(type -> id.equals(type.getId()))
                .forEach(vergemaal -> {
                    var slettePerson = getPerson(vergemaal.getVergeIdent());

                    DeleteRelasjonerUtility.deleteRelasjoner(hovedPerson, slettePerson, RelasjonType.VERGE_MOTTAKER);

                    deletePerson(slettePerson, vergemaal.isEksisterendePerson());
                });

        hovedPerson.getPerson().setVergemaal(hovedPerson.getPerson().getVergemaal().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    public Mono<Void> deleteDoedfoedtBarn(String ident, Integer id) {

        var dbPerson = getPerson(ident);

        checkExists(dbPerson.getPerson().getDoedfoedtBarn(), id, PDL_DOEDFOEDT_BARN.getDescription());
        hendelseIdService.deletePdlHendelse(ident, PDL_DOEDFOEDT_BARN.getDescription(), id);

        dbPerson.getPerson().setDoedfoedtBarn(dbPerson.getPerson().getDoedfoedtBarn().stream()
                .filter(type -> !id.equals(type.getId()))
                .toList());
    }

    private DbPerson getPerson(String ident) {

        return personRepository.findByIdent(ident)
                .orElseThrow(() -> new NotFoundException(format(IDENT_NOT_FOUND, ident)));
    }

    private Mono<Void> deletePerson(DbPerson person, boolean isEksisterendePerson) {

        if (person.getRelasjoner().isEmpty() && !isEksisterendePerson) {

            return personService.deletePerson(person.getIdent());
        }
    }
}
