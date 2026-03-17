package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.AliasRepository;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.pdl.forvalter.dto.FolkeregisterPersonstatus;
import no.nav.pdl.forvalter.dto.MergeIdent;
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
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO.PersonHendelserDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
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
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

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
                .then(personRepository.findByIdent(ident))
                .switchIfEmpty(Mono.error(new NotFoundException(String.format("Ident %s finnes ikke i databasen", ident))))
                .flatMap(dbPerson -> getEksternePersoner(dbPerson)
                        .zipWith(Mono.just(dbPerson)))
                .flatMap(tuple -> {
                    val eksternePersoner = tuple.getT1();
                    val dbPerson = tuple.getT2();
                    return Flux.concat(
                                    Mono.just(dbPerson),
                                    relasjonRepository.findByPersonId(dbPerson.getId())
                                            .flatMap(relasjon -> personRepository.findById(relasjon.getRelatertPersonId()))
                                            .filter(dbPerson1 -> isNotTrue(ekskluderEksterenePersoner) ||
                                                                 eksternePersoner.stream()
                                                                         .noneMatch(ekstern -> ekstern.equals(dbPerson1.getIdent()))),
                                    Flux.fromIterable(dbPerson.getPerson().getForelderBarnRelasjon())
                                            .filter(ForelderBarnRelasjonDTO::hasBarn)
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
                                    sendAlleInformasjonselementer(new ArrayList<>(requesterTilOppretting))
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
                                .map(KontaktinformasjonForDoedsboDTO::getPersonSomKontakt)
                                .filter(Objects::nonNull)
                                .filter(KontaktinformasjonForDoedsboDTO.KontaktpersonDTO::isEksisterendePerson)
                                .map(KontaktinformasjonForDoedsboDTO.KontaktpersonDTO::getIdentifikasjonsnummer),
                        Flux.fromIterable(dbPerson.getPerson().getForelderBarnRelasjon())
                                .filter(ForelderBarnRelasjonDTO::hasBarn)
                                .map(ForelderBarnRelasjonDTO::getRelatertPerson)
                                .flatMap(personRepository::findByIdent)
                                .map(DbPerson::getPerson)
                                .map(PersonDTO::getForeldreansvar)
                                .flatMap(Flux::fromIterable)
                                .filter(ForeldreansvarDTO::isEksisterendePerson)
                                .map(ForeldreansvarDTO::getAnsvarlig))
                .reduce(new HashSet<>(), (set, ident) -> {
                    set.add(ident);
                    return set;
                });
    }

    private Mono<Void> checkAlias(String ident) {

        return aliasRepository.findByTidligereIdent(ident)
                .flatMap(dbAlias -> personRepository.findById(dbAlias.getPersonId()))
                .flatMap(dbPerson -> Mono.error(new InvalidRequestException(
                        VIOLATION_ALIAS_EXISTS.formatted(dbPerson.getIdent()))));
    }

    private Flux<OrdreResponseDTO.PdlStatusDTO> sendAlleInformasjonselementer(List<OpprettRequest> opprettinger) {

        return Flux.fromIterable(opprettinger)
                .flatMap(oppretting -> aliasRepository.existsByPersonId(oppretting.getPerson().getId())
                        .zipWith(Mono.just(oppretting)))
                .reduce(new ArrayList<OpprettRequest>(), (sorterteOpprettinger, tuple) -> {
                    if (isTrue(tuple.getT1())) {
                        sorterteOpprettinger.addFirst(tuple.getT2());
                    } else {
                        sorterteOpprettinger.addLast(tuple.getT2());
                    }
                    return sorterteOpprettinger;
                })
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
                                .flatMap(this::personOpprett)
                                .collectList(),
                        Flux.fromIterable(sorterteOpprettinger)
                                .filter(OpprettRequest::isNotTestnorgeIdent)
                                .flatMap(this::npidMerge)
                                .collectList(),
                        Flux.fromIterable(sorterteOpprettinger)
                                .flatMap(oppretting -> aliasRepository.existsByPersonId(oppretting.getPerson().getId())
                                        .zipWith(Mono.just(oppretting)))
                                .filter(exist -> isFalse(exist.getT1()))
                                .flatMap(oppretting -> getOrdrer(oppretting.getT2()))
                                .collectList()))
                .flatMapMany(tuple -> deployService.sendOrders(
                        OrdreRequest.builder()
                                .sletting(tuple.getT1())
                                .oppretting(tuple.getT2())
                                .merge(tuple.getT3())
                                .opplysninger(tuple.getT4())
                                .build()));
    }

    private Flux<Ordre> personOpprett(OpprettRequest oppretting) {

        return getHistoriskePersoner(new ArrayList<>(), oppretting.getPerson(), true)
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
                        deployService.createOrdre(PDL_OPPRETT_PERSON, oppretting.getPerson().getIdent(), List.of(opprettIdent)));
    }

    // Rekursiv metode som henter alle historiske personer basert på aliaser.
    // Dersom en person har en alias som peker på en tidligere identitet,
    // og denne igjen har en alias som peker på en enda tidligere identitet,
    // vil alle disse bli hentet ut og lagt i listen.
    // Listen vil inneholde alle historiske personer i rekkefølge fra den nyeste til den eldste.
    private Mono<List<DbPerson>> getHistoriskePersoner(List<DbPerson> akkumulert, DbPerson hovedperson, Boolean ekisterer) {

        if (isFalse(ekisterer)) {
            return Mono.just(akkumulert);
        }

        return aliasRepository.existsByPersonId(hovedperson.getId())
                .flatMap(exists -> isTrue(exists) ? aliasRepository.findByPersonId(hovedperson.getId())
                        .flatMap(alias -> personRepository.findByIdent(alias.getTidligereIdent())
                                .flatMap(dbPerson -> {
                                    akkumulert.add(dbPerson);
                                    return Mono.just(dbPerson)
                                            .zipWith(Mono.just(exists));
                                })) :
                        Mono.just(hovedperson)
                                .zipWith(Mono.just(exists)))
                .flatMap(tuple -> getHistoriskePersoner(akkumulert, tuple.getT1(), tuple.getT2()));
    }

    private Flux<Ordre> npidMerge(OpprettRequest oppretting) {

        return getHistoriskePersoner(new ArrayList<>(), oppretting.getPerson(), true)
                .flatMapMany(historiske -> deployService.createOrdre(PDL_PERSON_MERGE, oppretting.getPerson().getIdent(),
                        historiske.stream()
                                .map(DbPerson::getIdent)
                                .filter(IdenttypeUtility::isNpidIdent)
                                .map(ident -> MergeIdent.builder()
                                        .npid(ident)
                                        .build())
                                .toList()));
    }

    private Flux<Ordre> getOrdrer(OpprettRequest oppretting) {
        return Flux.concat(
                deployService.createOrdre(PDL_FOLKEREGISTER_PERSONSTATUS, oppretting.getPerson().getIdent(), mapperFacade.mapAsList(oppretting.getPerson().getPerson().getFolkeregisterPersonstatus(), FolkeregisterPersonstatus.class)),
                deployService.createOrdre(PDL_ADRESSEBESKYTTELSE, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getAdressebeskyttelse()),
                deployService.createOrdre(PDL_DOEDSFALL, oppretting.getPerson().getIdent(), utenHistorikk(oppretting.getPerson().getPerson().getDoedsfall())),
                deployService.createOrdre(PDL_NAVN, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getNavn().stream()
                        .map(this::toUpperCase)
                        .toList()),
                deployService.createOrdre(PDL_KJOENN, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getKjoenn()),
                deployService.createOrdre(PDL_FOEDESTED, oppretting.getPerson().getIdent(), getFoedested(oppretting.getPerson().getPerson())),
                deployService.createOrdre(PDL_FOEDSELSDATO, oppretting.getPerson().getIdent(), getFoedselsdato(oppretting.getPerson().getPerson())),
                deployService.createOrdre(PDL_STATSBORGERSKAP, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getStatsborgerskap()),
                deployService.createOrdre(PDL_KONTAKTADRESSE, oppretting.getPerson().getIdent(), mapperFacade.mapAsList(oppretting.getPerson().getPerson().getKontaktadresse(), PdlKontaktadresse.class)),
                deployService.createOrdre(PDL_BOSTEDADRESSE, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getBostedsadresse()),
                deployService.createOrdre(PDL_OPPHOLDSADRESSE, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getOppholdsadresse()),
                deployService.createOrdre(PDL_INNFLYTTING, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getInnflytting()),
                deployService.createOrdre(PDL_UTFLYTTING, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getUtflytting()),
                deployService.createOrdre(PDL_DELTBOSTED, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getDeltBosted()),
                deployService.createOrdre(PDL_FORELDREANSVAR, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getForeldreansvar().stream()
                        .filter(ForeldreansvarDTO::isNotAnsvarssubjekt)
                        .toList()),
                deployService.createOrdre(PDL_FORELDRE_BARN_RELASJON, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getForelderBarnRelasjon()),
                deployService.createOrdre(PDL_SIVILSTAND, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getSivilstand().stream()
                        .filter(SivilstandDTO::isNotSamboer)
                        .toList()),
                getVergemaalContext(oppretting)
                        .flatMapMany(context -> deployService.createOrdre(PDL_VERGEMAAL, oppretting.getPerson().getIdent(), mapperFacade.mapAsList(oppretting.getPerson().getPerson().getVergemaal(), PdlVergemaal.class, context))),
                deployService.createOrdre(PDL_TELEFONUMMER, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getTelefonnummer()),
                deployService.createOrdre(PDL_OPPHOLD, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getOpphold()),
                deployService.createOrdre(PDL_KONTAKTINFORMASJON_FOR_DODESDBO, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getKontaktinformasjonForDoedsbo()),
                deployService.createOrdre(PDL_UTENLANDS_IDENTIFIKASJON_NUMMER, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getUtenlandskIdentifikasjonsnummer()),
                deployService.createOrdre(PDL_FALSK_IDENTITET, oppretting.getPerson().getIdent(), mapperFacade.mapAsList(utenHistorikk(oppretting.getPerson().getPerson().getFalskIdentitet()), PdlFalskIdentitet.class)),
                deployService.createOrdre(PDL_TILRETTELAGT_KOMMUNIKASJON, oppretting.getPerson().getIdent(), mapperFacade.mapAsList(oppretting.getPerson().getPerson().getTilrettelagtKommunikasjon(), PdlTilrettelagtKommunikasjon.class)),
                deployService.createOrdre(PDL_DOEDFOEDT_BARN, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getDoedfoedtBarn()),
                deployService.createOrdre(PDL_SIKKERHETSTILTAK, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getSikkerhetstiltak()),
                deployService.createOrdre(PDL_NAVPERSONIDENTIFIKATOR, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getNavPersonIdentifikator())
        );
    }

    private NavnDTO toUpperCase(NavnDTO artifact) {

        var navn = mapperFacade.map(artifact, NavnDTO.class);
        navn.setFornavn(StringUtils.toRootUpperCase(artifact.getFornavn()));
        navn.setMellomnavn(StringUtils.toRootUpperCase(artifact.getMellomnavn()));
        navn.setEtternavn(StringUtils.toRootUpperCase(artifact.getEtternavn()));

        return navn;
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

        return nonNull(hendelser) ?
                hendelser.stream()
                        .filter(hendelse -> ident.equals(hendelse.getIdent()))
                        .map(hendelse -> OrdreResponseDTO.PdlStatusDTO.builder()
                                .infoElement(hendelse.getInfoElement())
                                .hendelser(hendelse.getHendelser())
                                .build())
                        .toList() :
                emptyList();
    }

    private Mono<MappingContext> getVergemaalContext(OpprettRequest oppretting) {

        return Mono.defer(() -> {
                    if (!oppretting.getPerson().getPerson().getVergemaal().isEmpty()) {
                        return Mono.zip(kodeverkConsumer.getFylkesmannsembeter(),
                                personRepository.findByIdentIn(oppretting.getPerson().getPerson().getVergemaal().stream()
                                                .map(VergemaalDTO::getVergeIdent)
                                                .toList(), Pageable.unpaged())
                                        .collectList());
                    } else {
                        return Mono.empty();
                    }
                })
                .flatMap(tuple -> {
                    val context = MappingContextUtils.getMappingContext();
                    context.setProperty("fylkesmannsembeter", tuple.getT1());
                    context.setProperty("vergepersoner", tuple.getT2());
                    return Mono.just(context);
                });
    }
}