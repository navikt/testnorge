package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.pdl.forvalter.database.model.DbAlias;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.AliasRepository;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.pdl.forvalter.dto.FolkeregisterPersonstatus;
import no.nav.pdl.forvalter.dto.NpidIdentDTO;
import no.nav.pdl.forvalter.dto.OpprettIdent;
import no.nav.pdl.forvalter.dto.OpprettRequest;
import no.nav.pdl.forvalter.dto.Ordre;
import no.nav.pdl.forvalter.dto.OrdreRequest;
import no.nav.pdl.forvalter.dto.PdlDelete;
import no.nav.pdl.forvalter.dto.PdlFalskIdentitet;
import no.nav.pdl.forvalter.dto.PdlKontaktadresse;
import no.nav.pdl.forvalter.dto.PdlTilrettelagtKommunikasjon;
import no.nav.pdl.forvalter.dto.PdlVergemaal;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.exception.NotFoundException;
import no.nav.pdl.forvalter.mapper.MappingContextUtils;
import no.nav.pdl.forvalter.utils.IdenttypeUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedestedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO.PersonHendelserDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.IdenttypeUtility.getIdenttype;
import static no.nav.pdl.forvalter.utils.IdenttypeUtility.isNpidIdent;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.OPPHOERT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_ADRESSEBESKYTTELSE;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_BOSTEDADRESSE;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_DELTBOSTED;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_DOEDFOEDT_BARN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_DOEDSFALL;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_FALSK_IDENTITET;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_FOEDESTED;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_FOEDSELSDATO;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_FOLKEREGISTER_PERSONSTATUS;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_FORELDREANSVAR;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_FORELDRE_BARN_RELASJON;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_INNFLYTTING;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_KJOENN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_KONTAKTADRESSE;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_KONTAKTINFORMASJON_FOR_DODESDBO;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_NAVN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_NAVPERSONIDENTIFIKATOR;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_NPID_SPLIT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_OPPHOLD;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_OPPHOLDSADRESSE;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_OPPRETT_PERSON;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_PERSON_MERGE;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_SIKKERHETSTILTAK;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_SIVILSTAND;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_SLETTING;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_SLETTING_HENDELSEID;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_STATSBORGERSKAP;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_TELEFONUMMER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_TILRETTELAGT_KOMMUNIKASJON;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_UTENLANDS_IDENTIFIKASJON_NUMMER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_UTFLYTTING;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_VERGEMAAL;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdlOrdreService {

    private static final String VIOLATION_ALIAS_EXISTS = "Utgått ident kan ikke sendes. Benytt gjeldende ident %s for denne operasjonen";

    private final DeployService deployService;
    private final PersonRepository personRepository;
    private final RelasjonRepository relasjonRepository;
    private final AliasRepository aliasRepository;
    private final MapperFacade mapperFacade;
    private final HendelseIdService hendelseIdService;
    private final KodeverkConsumer kodeverkConsumer;

    public Mono<OrdreResponseDTO> send(String ident, Boolean ekskluderEksterenePersoner) {

        var timestamp = System.currentTimeMillis();

        return checkAlias(ident)
                .then(Mono.defer(() -> personRepository.findByIdent(ident)))
                .switchIfEmpty(Mono.error(new NotFoundException(String.format("Ident %s finnes ikke i databasen", ident))))
                .flatMap(dbPerson -> getEksternePersoner(dbPerson)
                        .zipWith(Mono.just(dbPerson)))
                .flatMap(tuple -> {
                    var eksternePersoner = tuple.getT1();
                    var dbPerson = tuple.getT2();
                    return Flux.concat(
                                    Mono.just(dbPerson),
                                    relasjonRepository.findByPersonId(dbPerson.getId())
                                            .flatMap(relasjon -> personRepository.findById(relasjon.getRelatertPersonId()))
                                            .filter(dbPerson1 -> isNotTrue(ekskluderEksterenePersoner) ||
                                                                 eksternePersoner.stream()
                                                                         .noneMatch(ekstern -> ekstern.equals(dbPerson1.getIdent()))),
                                    Flux.fromIterable(dbPerson.getPerson().getForelderBarnRelasjon())
                                            .filter(ForelderBarnRelasjonDTO::hasBarn)
                                            .filter(relasjon -> isNotBlank(relasjon.getRelatertPerson()))
                                            .map(ForelderBarnRelasjonDTO::getRelatertPerson)
                                            .flatMap(personRepository::findByIdent)
                                            .map(DbPerson::getPerson)
                                            .map(PersonDTO::getForeldreansvar)
                                            .flatMap(Flux::fromIterable)
                                            .filter(foreldreansvar -> foreldreansvar.getAnsvar() == ForeldreansvarDTO.Ansvar.ANDRE)
                                            .map(ForeldreansvarDTO::getAnsvarlig)
                                            .filter(andre -> isNotTrue(ekskluderEksterenePersoner) ||
                                                             eksternePersoner.stream()
                                                                     .noneMatch(ekstern -> ekstern.equals(andre)))
                                            .flatMap(personRepository::findByIdent))
                            .distinct()
                            .map(person -> OpprettRequest.builder()
                                    .person(person)
                                    .build())
                            .collectList()
                            .flatMap(requesterTilOppretting ->
                                    sendAlleInformasjonselementer(new ArrayList<>(requesterTilOppretting), ident)
                                            .collectList()
                                            .zipWith(Mono.just(requesterTilOppretting)));
                })
                .map(tuple -> OrdreResponseDTO.builder()
                        .hovedperson(
                                PersonHendelserDTO.builder()
                                        .ident(ident)
                                        .ordrer(getPersonHendelser(ident, tuple.getT1()))
                                        .build())
                        .relasjoner(tuple.getT2().stream()
                                .map(OpprettRequest::getPerson)
                                .map(DbPerson::getIdent)
                                .filter(relasjon -> !relasjon.equals(ident))
                                .map(personIdent -> PersonHendelserDTO.builder()
                                        .ident(personIdent)
                                        .ordrer(getPersonHendelser(personIdent, tuple.getT1()))
                                        .build())
                                .toList())
                        .build())
                .flatMap(response -> hendelseIdService.oppdaterPerson(response)
                        .thenReturn(response))
                .doOnNext(response ->
                        log.info("PDL ordre for ident: {} tid: {} ms", ident, System.currentTimeMillis() - timestamp));
    }

    private Mono<Set<String>> getEksternePersoner(DbPerson dbPerson) {

        return Flux.concat(
                        Flux.fromIterable(dbPerson.getPerson().getSivilstand())
                                .filter(SivilstandDTO::isEksisterendePerson)
                                .map(SivilstandDTO::getRelatertVedSivilstand),
                        Flux.fromIterable(dbPerson.getPerson().getForelderBarnRelasjon())
                                .filter(relasjon -> isNotBlank(relasjon.getRelatertPerson()))
                                .filter(ForelderBarnRelasjonDTO::isEksisterendePerson)
                                .map(ForelderBarnRelasjonDTO::getRelatertPerson),
                        Flux.fromIterable(dbPerson.getPerson().getForeldreansvar())
                                .filter(ForeldreansvarDTO::isEksisterendePerson)
                                .map(ForeldreansvarDTO::getAnsvarlig),
                        Flux.fromIterable(dbPerson.getPerson().getVergemaal())
                                .filter(VergemaalDTO::isEksisterendePerson)
                                .map(VergemaalDTO::getVergeIdent),
                        Flux.fromIterable(dbPerson.getPerson().getFullmakt())
                                .filter(FullmaktDTO::isEksisterendePerson)
                                .map(FullmaktDTO::getMotpartsPersonident),
                        Flux.fromIterable(dbPerson.getPerson().getKontaktinformasjonForDoedsbo())
                                .filter(kontaktinformasjon -> nonNull(kontaktinformasjon.getPersonSomKontakt()))
                                .map(KontaktinformasjonForDoedsboDTO::getPersonSomKontakt)
                                .filter(KontaktinformasjonForDoedsboDTO.KontaktpersonDTO::isEksisterendePerson)
                                .map(KontaktinformasjonForDoedsboDTO.KontaktpersonDTO::getIdentifikasjonsnummer),
                        Flux.fromIterable(dbPerson.getPerson().getForelderBarnRelasjon())
                                .filter(ForelderBarnRelasjonDTO::hasBarn)
                                .filter(relasjon -> isNotBlank(relasjon.getRelatertPerson()))
                                .map(ForelderBarnRelasjonDTO::getRelatertPerson)
                                .flatMap(personRepository::findByIdent)
                                .map(DbPerson::getPerson)
                                .map(PersonDTO::getForeldreansvar)
                                .flatMap(Flux::fromIterable)
                                .filter(ForeldreansvarDTO::isEksisterendePerson)
                                .map(ForeldreansvarDTO::getAnsvarlig))
                .collect(Collectors.toSet());
    }

    private Mono<Void> checkAlias(String ident) {

        return aliasRepository.findByTidligereIdent(ident)
                .flatMap(dbAlias -> personRepository.findById(dbAlias.getPersonId()))
                .flatMap(dbPerson -> Mono.error(new InvalidRequestException(
                        VIOLATION_ALIAS_EXISTS.formatted(dbPerson.getIdent()))));
    }

    private Flux<OrdreResponseDTO.PdlStatusDTO> sendAlleInformasjonselementer(List<OpprettRequest> opprettinger,
                                                                              String hovedpersonIdent) {

        return Flux.fromIterable(opprettinger)
                .sort(new IdentComparator(hovedpersonIdent))
                .collectList()
                .flatMap(sorterteOpprettinger -> Mono.zip(
                        Flux.fromIterable(sorterteOpprettinger)
                                .flatMap(oppretting -> oppretting.isNotTestnorgeIdent() ?
                                        deployService.createOrdre(PDL_SLETTING, oppretting.getPerson().getIdent(), List.of(new PdlDelete())) :
                                        hendelseIdService.getPdlHendelser(oppretting.getPerson())
                                                .collectList()
                                                .flatMapMany(pdlHendelser ->
                                                        deployService.createOrdre(PDL_SLETTING_HENDELSEID, oppretting.getPerson().getIdent(),
                                                                pdlHendelser)))
                                .collectList(),
                        Flux.fromIterable(sorterteOpprettinger)
                                .filter(OpprettRequest::isNotTestnorgeIdent)
                                .filter(OpprettRequest::isNpidIdent)
                                .flatMap(this::npidSplit)
                                .collectList(),
                        Flux.fromIterable(sorterteOpprettinger)
                                .filter(OpprettRequest::isNotTestnorgeIdent)
                                .flatMap(this::personOpprett)
                                .collectList(),
                        Flux.fromIterable(sorterteOpprettinger)
                                .filter(OpprettRequest::isNotTestnorgeIdent)
                                .filter(OpprettRequest::isNpidIdent)
                                .flatMap(this::npidMerge)
                                .collectList(),
                        Flux.fromIterable(sorterteOpprettinger)
                                .flatMap(oppretting -> aliasRepository.existsByPersonId(oppretting.getPerson().getId())
                                        .zipWith(Mono.just(oppretting)))
                                .filter(exist -> exist.getT2().isNotNpidIdent() ||
                                                 isTrue(exist.getT1()))
                                .flatMap(oppretting -> getOrdrer(oppretting.getT2()))
                                .collectList()))
                .flatMapMany(tuple -> deployService.sendOrders(
                        OrdreRequest.builder()
                                .sletting(tuple.getT1())
                                .split(tuple.getT2())
                                .oppretting(tuple.getT3())
                                .merge(tuple.getT4())
                                .opplysninger(tuple.getT5())
                                .build()));
    }

    private Flux<Ordre> personOpprett(OpprettRequest oppretting) {

        return getHistoriskePersoner(oppretting.getPerson())
                .map(historiske -> OpprettIdent.builder()
                        .historiskeIdenter(historiske.stream()
                                .map(DbPerson::getIdent)
                                .filter(IdenttypeUtility::isNotNpidIdent)
                                .toList())
                        .opphoert(historiske.stream()
                                .map(DbPerson::getPerson)
                                .map(PersonDTO::getFolkeregisterPersonstatus)
                                .flatMap(Collection::stream)
                                .map(FolkeregisterPersonstatusDTO::getStatus)
                                .anyMatch(status -> status == OPPHOERT))
                        .build())
                .flatMapMany(opprettIdent ->
                        deployService.createOrdre(PDL_OPPRETT_PERSON, oppretting.getPerson().getIdent(), List.of(opprettIdent)))
                .doOnNext(ordre -> log.info("Ordre for oppretting: {}", ordre));
    }

    private Mono<List<DbPerson>> getHistoriskePersoner(DbPerson hovedperson) {

        return aliasRepository.findByPersonId(hovedperson.getId())
                .map(DbAlias::getTidligereIdent)
                .collectList()
                .flatMapMany(personRepository::findByIdentIn)
                .collectList();
    }

    private Mono<DbPerson> getAktivPerson(DbPerson historiskPerson) {

        return aliasRepository.findByTidligereIdent(historiskPerson.getIdent())
                .map(DbAlias::getPersonId)
                .flatMap(personRepository::findById);
    }

    private Flux<Ordre> npidSplit(OpprettRequest oppretting) {
        return npidOrdre(oppretting, PDL_NPID_SPLIT, "npid-split");
    }

    private Flux<Ordre> npidMerge(OpprettRequest oppretting) {
        return npidOrdre(oppretting, PDL_PERSON_MERGE, "npid-merge");
    }

    private Flux<Ordre> npidOrdre(OpprettRequest oppretting, PdlArtifact artifact, String description) {
        return getAktivPerson(oppretting.getPerson())
                .flatMapMany(aktiv -> deployService.createOrdre(artifact, oppretting.getPerson().getIdent(),
                        List.of(NpidIdentDTO.builder()
                                .otherIdent(aktiv.getIdent())
                                .build())))
                .doOnNext(ordre -> log.info("Ordre for {}: {}", description, ordre));
    }

    private Flux<Ordre> getOrdrer(OpprettRequest oppretting) {
        var ident = oppretting.getPerson().getIdent();
        var person = oppretting.getPerson().getPerson();
        return Flux.concat(
                deployService.createOrdre(PDL_FOLKEREGISTER_PERSONSTATUS, ident, mapperFacade.mapAsList(person.getFolkeregisterPersonstatus(), FolkeregisterPersonstatus.class)),
                deployService.createOrdre(PDL_ADRESSEBESKYTTELSE, ident, person.getAdressebeskyttelse()),
                deployService.createOrdre(PDL_DOEDSFALL, ident, utenHistorikk(person.getDoedsfall())),
                deployService.createOrdre(PDL_NAVN, ident, person.getNavn().stream()
                        .map(this::toUpperCase)
                        .toList()),
                deployService.createOrdre(PDL_KJOENN, ident, person.getKjoenn()),
                deployService.createOrdre(PDL_FOEDESTED, ident, getFoedested(person)),
                deployService.createOrdre(PDL_FOEDSELSDATO, ident, getFoedselsdato(person)),
                deployService.createOrdre(PDL_STATSBORGERSKAP, ident, person.getStatsborgerskap()),
                deployService.createOrdre(PDL_KONTAKTADRESSE, ident, mapperFacade.mapAsList(person.getKontaktadresse(), PdlKontaktadresse.class)),
                deployService.createOrdre(PDL_BOSTEDADRESSE, ident, person.getBostedsadresse()),
                deployService.createOrdre(PDL_OPPHOLDSADRESSE, ident, person.getOppholdsadresse()),
                deployService.createOrdre(PDL_INNFLYTTING, ident, person.getInnflytting()),
                deployService.createOrdre(PDL_UTFLYTTING, ident, person.getUtflytting()),
                deployService.createOrdre(PDL_DELTBOSTED, ident, person.getDeltBosted()),
                deployService.createOrdre(PDL_FORELDREANSVAR, ident, person.getForeldreansvar().stream()
                        .filter(ForeldreansvarDTO::isNotAnsvarssubjekt)
                        .toList()),
                deployService.createOrdre(PDL_FORELDRE_BARN_RELASJON, ident, person.getForelderBarnRelasjon()),
                deployService.createOrdre(PDL_SIVILSTAND, ident, person.getSivilstand().stream()
                        .filter(SivilstandDTO::isNotSamboer)
                        .toList()),
                getVergemaalContext(oppretting)
                        .flatMapMany(context -> deployService.createOrdre(PDL_VERGEMAAL, ident, mapperFacade.mapAsList(person.getVergemaal(), PdlVergemaal.class, context))),
                deployService.createOrdre(PDL_TELEFONUMMER, ident, person.getTelefonnummer()),
                deployService.createOrdre(PDL_OPPHOLD, ident, person.getOpphold()),
                deployService.createOrdre(PDL_KONTAKTINFORMASJON_FOR_DODESDBO, ident, person.getKontaktinformasjonForDoedsbo()),
                deployService.createOrdre(PDL_UTENLANDS_IDENTIFIKASJON_NUMMER, ident, person.getUtenlandskIdentifikasjonsnummer()),
                deployService.createOrdre(PDL_FALSK_IDENTITET, ident, mapperFacade.mapAsList(utenHistorikk(person.getFalskIdentitet()), PdlFalskIdentitet.class)),
                deployService.createOrdre(PDL_TILRETTELAGT_KOMMUNIKASJON, ident, mapperFacade.mapAsList(person.getTilrettelagtKommunikasjon(), PdlTilrettelagtKommunikasjon.class)),
                deployService.createOrdre(PDL_DOEDFOEDT_BARN, ident, person.getDoedfoedtBarn()),
                deployService.createOrdre(PDL_SIKKERHETSTILTAK, ident, person.getSikkerhetstiltak()),
                deployService.createOrdre(PDL_NAVPERSONIDENTIFIKATOR, ident, person.getNavPersonIdentifikator().stream()
                        .filter(navPersonIdentifikator -> isNpidIdent(ident))
                        .filter(navPersonIdentifikator -> Objects.equals(navPersonIdentifikator.getIdentifikator(), ident))
                        .toList())
        );
    }

    private NavnDTO toUpperCase(NavnDTO artifact) {

        return NavnDTO.builder()
                .id(artifact.getId())
                .kilde(artifact.getKilde())
                .master(artifact.getMaster())
                .isNew(artifact.getIsNew())
                .opprettet(artifact.getOpprettet())
                .folkeregistermetadata(artifact.getFolkeregistermetadata())
                .hendelseId(artifact.getHendelseId())
                .fornavn(StringUtils.toRootUpperCase(artifact.getFornavn()))
                .mellomnavn(StringUtils.toRootUpperCase(artifact.getMellomnavn()))
                .etternavn(StringUtils.toRootUpperCase(artifact.getEtternavn()))
                .hasMellomnavn(artifact.getHasMellomnavn())
                .gyldigFraOgMed(artifact.getGyldigFraOgMed())
                .build();
    }

    private List<? extends DbVersjonDTO> utenHistorikk(List<? extends DbVersjonDTO> artifacter) {

        return artifacter.stream()
                .max(Comparator.comparing(DbVersjonDTO::getId))
                .stream().toList();
    }

    private List<? extends DbVersjonDTO> getFoedested(PersonDTO person) {

        return !person.getFoedested().isEmpty() ?
                person.getFoedested() :
                mapperFacade.mapAsList(person.getFoedsel(), FoedestedDTO.class);
    }

    private List<? extends DbVersjonDTO> getFoedselsdato(PersonDTO person) {

        return !person.getFoedselsdato().isEmpty() ?
                person.getFoedselsdato() :
                mapperFacade.mapAsList(person.getFoedsel(), FoedselDTO.class);
    }

    private static List<OrdreResponseDTO.PdlStatusDTO> getPersonHendelser(String ident, List<OrdreResponseDTO.PdlStatusDTO> hendelser) {

        return hendelser.stream()
                .filter(hendelse -> ident.equals(hendelse.getIdent()))
                .map(hendelse -> OrdreResponseDTO.PdlStatusDTO.builder()
                        .infoElement(hendelse.getInfoElement())
                        .hendelser(hendelse.getHendelser())
                        .build())
                .toList();
    }

    private Mono<MappingContext> getVergemaalContext(OpprettRequest oppretting) {

        var vergemaal = oppretting.getPerson().getPerson().getVergemaal();
        if (vergemaal.isEmpty()) {
            return Mono.empty();
        }
        return Mono.zip(
                        kodeverkConsumer.getFylkesmannsembeter(),
                        personRepository.findByIdentInOrderBySistOppdatertDesc(vergemaal.stream()
                                        .map(VergemaalDTO::getVergeIdent)
                                        .toList())
                                .collectList())
                .map(tuple -> {
                    var context = MappingContextUtils.getMappingContext();
                    context.setProperty("fylkesmannsembeter", tuple.getT1());
                    context.setProperty("vergepersoner", tuple.getT2());
                    return context;
                });
    }

    @RequiredArgsConstructor
    protected static class IdentComparator implements Comparator<OpprettRequest> {

        private final String hovedpersonIdent;

        @Override
        public int compare(OpprettRequest o1, OpprettRequest o2) {

            if (getIdenttype(o1.getPerson().getIdent()) ==
                getIdenttype(o2.getPerson().getIdent())) {

                if (o1.getPerson().getIdent().equals(hovedpersonIdent)) {
                    return 1;
                } else {
                    return o2.getPerson().getIdent().equals(hovedpersonIdent) ? -1 : 0;
                }

            } else if (isNpidIdent(o1.getPerson().getIdent()) == !isNpidIdent(o2.getPerson().getIdent())) {
                return isNpidIdent(o1.getPerson().getIdent()) ? -1 : 1;

            } else if (getIdenttype(o1.getPerson().getIdent()) == Identtype.DNR &&
                       getIdenttype(o2.getPerson().getIdent()) == Identtype.FNR) {
                return -1;

            } else {
                return 1;
            }
        }
    }
}