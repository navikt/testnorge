package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.pdl.forvalter.exception.NotFoundException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
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
import static org.apache.commons.lang3.BooleanUtils.isFalse;
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
    private final DeleteRelasjonerService deleteRelasjonerService;
    private final RelasjonRepository relasjonRepository;

    private static <T extends DbVersjonDTO> Mono<Void> checkExists(List<T> artifacter, Integer id, String navn) {

        if (artifacter.stream().noneMatch(type -> id.equals(type.getId()))) {
            return Mono.error(new NotFoundException(format(INFO_NOT_FOUND, navn, id)));
        }
        return Mono.empty();
    }

    public Mono<Void> deleteFoedsel(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getFoedsel(), id, PDL_FOEDSEL.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson -> hendelseIdService.deletePdlHendelse(ident, PDL_FOEDSEL.getDescription(), id)
                        .then(Mono.just(dbPerson)))
                .doOnNext(dbPerson ->
                        dbPerson.getPerson().setFoedsel(dbPerson.getPerson().getFoedsel().stream()
                                .filter(type -> !id.equals(type.getId()))
                                .toList()))
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteFoedested(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getFoedested(), id, PDL_FOEDESTED.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson ->
                        hendelseIdService.deletePdlHendelse(ident, PDL_FOEDESTED.getDescription(), id)
                                .then(Mono.just(dbPerson)))
                .doOnNext(dbPerson ->
                        dbPerson.getPerson().setFoedested(dbPerson.getPerson().getFoedested().stream()
                                .filter(type -> !id.equals(type.getId()))
                                .toList()))
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteFoedselsdato(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getFoedselsdato(), id, PDL_FOEDSELSDATO.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson ->
                        hendelseIdService.deletePdlHendelse(ident, PDL_FOEDSELSDATO.getDescription(), id)
                                .then(Mono.just(dbPerson)))
                .doOnNext(dbPerson ->
                        dbPerson.getPerson().setFoedselsdato(dbPerson.getPerson().getFoedselsdato().stream()
                                .filter(type -> !id.equals(type.getId()))
                                .toList()))
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteNavn(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getNavn(), id, PDL_NAVN.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson ->
                        hendelseIdService.deletePdlHendelse(ident, PDL_NAVN.getDescription(), id)
                                .then(Mono.just(dbPerson)))
                .doOnNext(dbPerson ->
                        dbPerson.getPerson().setNavn(dbPerson.getPerson().getNavn().stream()
                                .filter(type -> !id.equals(type.getId()))
                                .toList()))
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteKjoenn(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getKjoenn(), id, PDL_KJOENN.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson ->
                        hendelseIdService.deletePdlHendelse(ident, PDL_KJOENN.getDescription(), id)
                                .then(Mono.just(dbPerson)))
                .doOnNext(dbPerson ->
                        dbPerson.getPerson().setKjoenn(dbPerson.getPerson().getKjoenn().stream()
                                .filter(type -> !id.equals(type.getId()))
                                .toList()))
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteBostedsadresse(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getBostedsadresse(), id, PDL_BOSTEDADRESSE.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson ->
                        hendelseIdService.deletePdlHendelse(ident, PDL_BOSTEDADRESSE.getDescription(), id)
                                .then(Mono.just(dbPerson)))
                .doOnNext(dbPerson ->
                        dbPerson.getPerson().setBostedsadresse(dbPerson.getPerson().getBostedsadresse().stream()
                                .filter(type -> !id.equals(type.getId()))
                                .toList()))
                .flatMap(folkeregisterPersonstatusService::update)
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteKontaktadresse(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getKontaktadresse(), id, PDL_KONTAKTADRESSE.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson ->
                        hendelseIdService.deletePdlHendelse(ident, PDL_KONTAKTADRESSE.getDescription(), id)
                                .then(Mono.just(dbPerson)))
                .doOnNext(dbPerson ->
                        dbPerson.getPerson().setKontaktadresse(dbPerson.getPerson().getKontaktadresse().stream()
                                .filter(type -> !id.equals(type.getId()))
                                .toList()))
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteOppholdsadresse(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getOppholdsadresse(), id, PDL_OPPHOLDSADRESSE.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson ->
                        hendelseIdService.deletePdlHendelse(ident, PDL_OPPHOLDSADRESSE.getDescription(), id)
                                .then(Mono.just(dbPerson)))
                .doOnNext(dbPerson ->
                        dbPerson.getPerson().setOppholdsadresse(dbPerson.getPerson().getOppholdsadresse().stream()
                                .filter(type -> !id.equals(type.getId()))
                                .toList()))
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteInnflytting(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getInnflytting(), id, PDL_INNFLYTTING.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson ->
                        hendelseIdService.deletePdlHendelse(ident, PDL_INNFLYTTING.getDescription(), id)
                                .then(Mono.just(dbPerson)))
                .doOnNext(dbPerson ->
                        dbPerson.getPerson().setInnflytting(dbPerson.getPerson().getInnflytting().stream()
                                .filter(type -> !id.equals(type.getId()))
                                .toList()))
                .flatMap(folkeregisterPersonstatusService::update)
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteUtflytting(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getUtflytting(), id, PDL_UTFLYTTING.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson ->
                        hendelseIdService.deletePdlHendelse(ident, PDL_UTFLYTTING.getDescription(), id)
                                .then(Mono.just(dbPerson)))
                .doOnNext(dbPerson ->
                        dbPerson.getPerson().setUtflytting(dbPerson.getPerson().getUtflytting().stream()
                                .filter(type -> !id.equals(type.getId()))
                                .toList()))
                .flatMap(folkeregisterPersonstatusService::update)
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteDeltBosted(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getDeltBosted(), id, PDL_DELTBOSTED.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson ->
                        hendelseIdService.deletePdlHendelse(ident, PDL_DELTBOSTED.getDescription(), id)
                                .then(Mono.just(dbPerson)))
                .doOnNext(dbPerson ->
                        dbPerson.getPerson().setDeltBosted(dbPerson.getPerson().getDeltBosted().stream()
                                .filter(type -> !id.equals(type.getId()))
                                .toList()))
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteForelderBarnRelasjon(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getForelderBarnRelasjon(), id, PDL_FORELDRE_BARN_RELASJON.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson ->
                        hendelseIdService.deletePdlHendelse(ident, PDL_FORELDRE_BARN_RELASJON.getDescription(), id)
                                .then(Mono.just(dbPerson)))
                .flatMapMany(dbPerson ->
                        Flux.fromIterable(dbPerson.getPerson().getForelderBarnRelasjon())
                                .filter(type -> id.equals(type.getId()) &&
                                                isNotBlank(type.getRelatertPerson()))
                                .flatMap(type -> getPerson(type.getRelatertPerson())
                                        .flatMap(slettePerson ->
                                                deleteRelasjonerService.deleteRelasjoner(dbPerson, slettePerson, FAMILIERELASJON_FORELDER)
                                                        .then(Mono.just(slettePerson)))
                                        .flatMap(slettePerson -> deletePerson(slettePerson, type.isEksisterendePerson())
                                                .then(Mono.just(dbPerson)))))
                .doOnNext(dbPerson -> dbPerson.getPerson().setForelderBarnRelasjon(dbPerson.getPerson().getForelderBarnRelasjon().stream()
                        .filter(type -> !id.equals(type.getId()))
                        .toList()))
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteForeldreansvar(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getForeldreansvar(), id, PDL_FORELDREANSVAR.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson ->
                        hendelseIdService.deletePdlHendelse(ident, PDL_FORELDREANSVAR.getDescription(), id)
                                .then(Mono.just(dbPerson)))
                .flatMapMany(dbPerson -> Flux.fromIterable(dbPerson.getPerson().getForeldreansvar())
                        .filter(type -> id.equals(type.getId()) &&
                                        (isNotBlank(type.getAnsvarlig()) || isNotBlank(type.getAnsvarssubjekt())))
                        .flatMap(type -> getPerson(isNotBlank(type.getAnsvarlig()) ?
                                type.getAnsvarlig() : type.getAnsvarssubjekt())
                                .flatMap(slettePerson ->
                                        deleteRelasjonerService.deleteRelasjoner(dbPerson, slettePerson, FORELDREANSVAR_FORELDER)
                                                .then(Mono.just(slettePerson)))
                                .flatMap(slettePerson -> deletePerson(slettePerson, type.isEksisterendePerson())
                                        .then(Mono.just(dbPerson)))))
                .doOnNext(dbPerson ->
                        dbPerson.getPerson().setForeldreansvar(dbPerson.getPerson().getForeldreansvar().stream()
                                .filter(type -> !id.equals(type.getId()))
                                .toList()))
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteKontaktinformasjonForDoedsbo(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getKontaktinformasjonForDoedsbo(), id, PDL_KONTAKTINFORMASJON_FOR_DODESDBO.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson ->
                        hendelseIdService.deletePdlHendelse(ident, PDL_KONTAKTINFORMASJON_FOR_DODESDBO.getDescription(), id)
                                .then(Mono.just(dbPerson)))
                .flatMapMany(dbPerson -> Flux.fromIterable(dbPerson.getPerson().getKontaktinformasjonForDoedsbo())
                        .filter(doedsbo -> id.equals(doedsbo.getId()) &&
                                           nonNull(doedsbo.getPersonSomKontakt()) &&
                                           isNotBlank(doedsbo.getPersonSomKontakt().getIdentifikasjonsnummer()))
                        .flatMap(doedsbo -> getPerson(doedsbo.getPersonSomKontakt().getIdentifikasjonsnummer())
                                .flatMap(slettePerson ->
                                        deleteRelasjonerService.deleteRelasjoner(dbPerson, slettePerson, KONTAKT_FOR_DOEDSBO)
                                                .then(Mono.just(slettePerson)))
                                .flatMap(slettePerson -> deletePerson(slettePerson, doedsbo.getPersonSomKontakt().isEksisterendePerson())
                                        .then(Mono.just(dbPerson)))))
                .doOnNext(dbPerson ->
                        dbPerson.getPerson().setKontaktinformasjonForDoedsbo(
                                dbPerson.getPerson().getKontaktinformasjonForDoedsbo().stream()
                                        .filter(type -> !id.equals(type.getId()))
                                        .toList()))
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteUtenlandskIdentifikasjonsnummer(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getUtenlandskIdentifikasjonsnummer(), id, PDL_UTENLANDS_IDENTIFIKASJON_NUMMER.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson ->
                        hendelseIdService.deletePdlHendelse(ident, PDL_UTENLANDS_IDENTIFIKASJON_NUMMER.getDescription(), id)
                                .then(Mono.just(dbPerson)))
                .doOnNext(dbPerson ->
                        dbPerson.getPerson().setUtenlandskIdentifikasjonsnummer(dbPerson.getPerson().getUtenlandskIdentifikasjonsnummer().stream()
                                .filter(type -> !id.equals(type.getId()))
                                .toList()))
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteFalskIdentitet(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getFalskIdentitet(), id, PDL_FALSK_IDENTITET.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson -> hendelseIdService.deletePdlHendelse(ident, PDL_FALSK_IDENTITET.getDescription(), id)
                        .then(Mono.just(dbPerson)))
                .flatMapMany(dbPerson -> Flux.fromIterable(dbPerson.getPerson().getFalskIdentitet())
                        .filter(falskId -> id.equals(falskId.getId()) &&
                                           isNotBlank(falskId.getRettIdentitetVedIdentifikasjonsnummer()))
                        .flatMap(falskId -> getPerson(falskId.getRettIdentitetVedIdentifikasjonsnummer())
                                .flatMap(slettePerson ->
                                        deleteRelasjonerService.deleteRelasjoner(dbPerson, slettePerson, FALSK_IDENTITET)
                                                .then(Mono.just(slettePerson)))
                                .flatMap(slettePerson -> deletePerson(slettePerson, falskId.isEksisterendePerson())
                                        .then(Mono.just(dbPerson)))))
                .doOnNext(dbPerson ->
                        dbPerson.getPerson().setFalskIdentitet(dbPerson.getPerson().getFalskIdentitet().stream()
                                .filter(type -> !id.equals(type.getId()))
                                .toList()))
                .flatMap(folkeregisterPersonstatusService::update)
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteAdressebeskyttelse(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getAdressebeskyttelse(), id, PDL_ADRESSEBESKYTTELSE.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson ->
                        hendelseIdService.deletePdlHendelse(ident, PDL_ADRESSEBESKYTTELSE.getDescription(), id)
                                .then(Mono.just(dbPerson)))
                .doOnNext(dbPerson ->
                        dbPerson.getPerson().setAdressebeskyttelse(dbPerson.getPerson().getAdressebeskyttelse().stream()
                                .filter(type -> !id.equals(type.getId()))
                                .toList()))
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteDoedsfall(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getDoedsfall(), id, PDL_DOEDSFALL.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson ->
                        hendelseIdService.deletePdlHendelse(ident, PDL_DOEDSFALL.getDescription(), id)
                                .then(Mono.just(dbPerson)))
                .doOnNext(dbPerson ->
                        dbPerson.getPerson().setDoedsfall(dbPerson.getPerson().getDoedsfall().stream()
                                .filter(type -> !id.equals(type.getId()))
                                .toList()))
                .flatMap(folkeregisterPersonstatusService::update)
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteFolkeregisterPersonstatus(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getFolkeregisterPersonstatus(), id, PDL_FOLKEREGISTER_PERSONSTATUS.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson ->
                        hendelseIdService.deletePdlHendelse(ident, PDL_FOLKEREGISTER_PERSONSTATUS.getDescription(), id)
                                .then(Mono.just(dbPerson)))
                .doOnNext(dbPerson -> {
                    dbPerson.getPerson().setFolkeregisterPersonstatus(dbPerson.getPerson().getFolkeregisterPersonstatus().stream()
                            .filter(type -> !id.equals(type.getId()))
                            .toList());
                    FolkeregisterPersonstatusService.setGyldigTilOgMed(dbPerson.getPerson());
                })
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteSikkerhetstiltak(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getSikkerhetstiltak(), id, PDL_SIKKERHETSTILTAK.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson ->
                        hendelseIdService.deletePdlHendelse(ident, PDL_SIKKERHETSTILTAK.getDescription(), id)
                                .then(Mono.just(dbPerson)))
                .doOnNext(dbPerson ->
                        dbPerson.getPerson().setSikkerhetstiltak(dbPerson.getPerson().getSikkerhetstiltak().stream()
                                .filter(type -> !id.equals(type.getId()))
                                .toList()))
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteTilrettelagtKommunikasjon(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getTilrettelagtKommunikasjon(), id, PDL_TILRETTELAGT_KOMMUNIKASJON.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson ->
                        hendelseIdService.deletePdlHendelse(ident, PDL_TILRETTELAGT_KOMMUNIKASJON.getDescription(), id)
                                .then(Mono.just(dbPerson)))
                .doOnNext(dbPerson ->
                        dbPerson.getPerson().setTilrettelagtKommunikasjon(dbPerson.getPerson().getTilrettelagtKommunikasjon().stream()
                                .filter(type -> !id.equals(type.getId()))
                                .toList()))
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteStatsborgerskap(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getStatsborgerskap(), id, PDL_STATSBORGERSKAP.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson ->
                        hendelseIdService.deletePdlHendelse(ident, PDL_STATSBORGERSKAP.getDescription(), id)
                                .then(Mono.just(dbPerson)))
                .doOnNext(dbPerson ->
                        dbPerson.getPerson().setStatsborgerskap(dbPerson.getPerson().getStatsborgerskap().stream()
                                .filter(type -> !id.equals(type.getId()))
                                .toList()))
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteOpphold(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getOpphold(), id, PDL_OPPHOLD.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson ->
                        hendelseIdService.deletePdlHendelse(ident, PDL_OPPHOLD.getDescription(), id)
                                .then(Mono.just(dbPerson)))
                .doOnNext(dbPerson ->
                        dbPerson.getPerson().setOpphold(dbPerson.getPerson().getOpphold().stream()
                                .filter(type -> !id.equals(type.getId()))
                                .toList()))
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteSivilstand(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getSivilstand(), id, PDL_SIVILSTAND.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson -> hendelseIdService.deletePdlHendelse(ident, PDL_SIVILSTAND.getDescription(), id)
                        .then(Mono.just(dbPerson)))
                .flatMapMany(dbPerson -> Flux.fromIterable(dbPerson.getPerson().getSivilstand())
                        .filter(type -> id.equals(type.getId()) && isNotBlank(type.getRelatertVedSivilstand()))
                        .flatMap(type -> getPerson(type.getRelatertVedSivilstand())
                                .flatMap(slettePerson ->
                                        deleteRelasjonerService.deleteRelasjoner(dbPerson, slettePerson, EKTEFELLE_PARTNER)
                                                .then(Mono.just(slettePerson)))
                                .flatMap(slettePerson -> deletePerson(slettePerson, type.isEksisterendePerson())
                                        .then(Mono.just(dbPerson)))))
                .doOnNext(dbPerson ->
                        dbPerson.getPerson().setSivilstand(dbPerson.getPerson().getSivilstand().stream()
                                .filter(type -> !id.equals(type.getId()))
                                .toList()))
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteTelefonnummer(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getTelefonnummer(), id, PDL_TELEFONUMMER.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson ->
                        hendelseIdService.deletePdlHendelse(ident, PDL_TELEFONUMMER.getDescription(), id)
                                .then(Mono.just(dbPerson)))
                .doOnNext(dbPerson ->
                        dbPerson.getPerson().setTelefonnummer(dbPerson.getPerson().getTelefonnummer().stream()
                                .filter(type -> !id.equals(type.getId()))
                                .toList()))
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteVergemaal(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getVergemaal(), id, PDL_VERGEMAAL.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson ->
                        hendelseIdService.deletePdlHendelse(ident, PDL_VERGEMAAL.getDescription(), id)
                                .then(Mono.just(dbPerson)))
                .flatMapMany(dbPerson -> Flux.fromIterable(dbPerson.getPerson().getVergemaal())
                        .filter(type -> id.equals(type.getId()) &&
                                        isNotBlank(type.getVergeIdent()))
                        .flatMap(type -> getPerson(type.getVergeIdent())
                                .flatMap(slettePerson ->
                                        deleteRelasjonerService.deleteRelasjoner(dbPerson, slettePerson, RelasjonType.VERGE_MOTTAKER)
                                                .then(Mono.just(slettePerson)))
                                .flatMap(slettePerson -> deletePerson(slettePerson, type.isEksisterendePerson())
                                        .then(Mono.just(dbPerson)))))
                .doOnNext(dbPerson -> dbPerson.getPerson().setVergemaal(dbPerson.getPerson().getVergemaal().stream()
                        .filter(type -> !id.equals(type.getId()))
                        .toList()))
                .flatMap(personRepository::save)
                .then();
    }

    public Mono<Void> deleteDoedfoedtBarn(String ident, Integer id) {

        return getPerson(ident)
                .flatMap(dbPerson ->
                        checkExists(dbPerson.getPerson().getDoedfoedtBarn(), id, PDL_DOEDFOEDT_BARN.getDescription())
                                .thenReturn(dbPerson))
                .flatMap(dbPerson ->
                        hendelseIdService.deletePdlHendelse(ident, PDL_DOEDFOEDT_BARN.getDescription(), id)
                                .then(Mono.just(dbPerson)))
                .doOnNext(dbPerson ->
                        dbPerson.getPerson().setDoedfoedtBarn(dbPerson.getPerson().getDoedfoedtBarn().stream()
                                .filter(type -> !id.equals(type.getId()))
                                .toList()))
                .flatMap(personRepository::save)
                .then();
    }

    private Mono<DbPerson> getPerson(String ident) {

        return personRepository.findByIdent(ident)
                .switchIfEmpty(Mono.error(new NotFoundException(format(IDENT_NOT_FOUND, ident))));
    }

    private Mono<Void> deletePerson(DbPerson person, boolean isStandalonePerson) {

        return relasjonRepository.existsByPersonIdOrRelatertPersonId(person.getId())
                .flatMap(exists -> {
                    if (isFalse(exists) && !isStandalonePerson) {
                        return personService.deletePerson(person.getIdent());
                    }
                    return Mono.empty();
                });
    }
}
