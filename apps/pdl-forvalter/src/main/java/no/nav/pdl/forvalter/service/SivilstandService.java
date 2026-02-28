package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.ArtifactUtils;
import no.nav.pdl.forvalter.utils.EgenskaperFraHovedperson;
import no.nav.pdl.forvalter.utils.FoedselsdatoUtility;
import no.nav.pdl.forvalter.utils.KjoennFraIdentUtility;
import no.nav.pdl.forvalter.utils.KjoennUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.consumer.command.VegadresseServiceCommand.defaultAdresse;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static no.nav.pdl.forvalter.utils.TestnorgeIdentUtility.isTestnorgeIdent;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.SAMBOER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.UGIFT;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class SivilstandService implements BiValidation<SivilstandDTO, PersonDTO> {

    private static final String INVALID_RELATERT_VED_SIVILSTAND = "Sivilstand: Relatert person finnes ikke";

    private final PersonRepository personRepository;
    private final CreatePersonService createPersonService;
    private final RelasjonService relasjonService;
    private final MapperFacade mapperFacade;

    public Mono<Void> convert(PersonDTO person) {

        return Flux.fromIterable(person.getSivilstand())
                .filter(type -> isTrue(type.getIsNew()))
                .flatMap(type -> handle(type, person))
                .filter(Objects::nonNull)
                .doOnNext(type -> {
                    type.setKilde(getKilde(type));
                    type.setMaster(getMaster(type, person));
                })
                .collectList()
                .map(sivilstand -> enforceIntegrity(person))
                .doOnNext(ArtifactUtils::renumberId)
                .then();
    }

    @Override
    public Mono<Void> validate(SivilstandDTO sivilstand, PersonDTO person) {

        if (!isTestnorgeIdent(person.getIdent()) && (sivilstand.isGift() ||
                                                     sivilstand.isSeparert() ||
                                                     sivilstand.getType() == SAMBOER) &&
            isNotBlank(sivilstand.getRelatertVedSivilstand())) {
            return personRepository.existsByIdent(sivilstand.getRelatertVedSivilstand())
                    .flatMap(isRelatert -> isFalse(isRelatert) ?
                            Mono.error(new InvalidRequestException(INVALID_RELATERT_VED_SIVILSTAND)) :
                            Mono.empty());
        }
        return Mono.empty();
    }

    private Mono<SivilstandDTO> handle(SivilstandDTO sivilstand, PersonDTO hovedperson) {

        if (isNull(sivilstand.getType())) {

            sivilstand.setType(UGIFT);
        }

        if (sivilstand.isGift() || sivilstand.isSeparert() || sivilstand.isSamboer()) {

            sivilstand.setEksisterendePerson(isNotBlank(sivilstand.getRelatertVedSivilstand()));
            return setRelatertVedSivilstand(sivilstand, hovedperson)
                    .then(relasjonService.setRelasjoner(hovedperson.getIdent(), RelasjonType.EKTEFELLE_PARTNER,
                            sivilstand.getRelatertVedSivilstand(), RelasjonType.EKTEFELLE_PARTNER))
                    .then(createRelatertSivilstand(sivilstand, hovedperson.getIdent()))
                    .thenReturn(sivilstand);

        } else {
            sivilstand.setRelatertVedSivilstand(null);
        }
        return Mono.just(sivilstand);
    }

    private Mono<Void> setRelatertVedSivilstand(SivilstandDTO sivilstand, PersonDTO hovedperson) {

        if (isBlank(sivilstand.getRelatertVedSivilstand())) {

            if (isNull(sivilstand.getNyRelatertPerson())) {
                sivilstand.setNyRelatertPerson(new PersonRequestDTO());
            }
            if (isNull(sivilstand.getNyRelatertPerson().getAlder()) &&
                isNull(sivilstand.getNyRelatertPerson().getFoedtEtter()) &&
                isNull(sivilstand.getNyRelatertPerson().getFoedtFoer())) {
                val foedselsdato = FoedselsdatoUtility.getFoedselsdato(hovedperson);
                sivilstand.getNyRelatertPerson().setFoedtFoer(foedselsdato.plusYears(2));
                sivilstand.getNyRelatertPerson().setFoedtEtter(foedselsdato.minusYears(2));
            }
            if (isNull(sivilstand.getNyRelatertPerson().getKjoenn())) {
                KjoennDTO.Kjoenn kjoenn = hovedperson.getKjoenn().stream()
                        .findFirst()
                        .map(KjoennDTO::getKjoenn)
                        .orElse(KjoennFraIdentUtility.getKjoenn(hovedperson.getIdent()));
                sivilstand.getNyRelatertPerson().setKjoenn(KjoennUtility.getPartnerKjoenn(kjoenn));
            }

            EgenskaperFraHovedperson.kopierData(hovedperson, sivilstand.getNyRelatertPerson());

            return createPersonService.execute(sivilstand.getNyRelatertPerson())
                    .flatMap(relatertPerson -> {
                        if (isNotTrue(sivilstand.getBorIkkeSammen()) && !hovedperson.getBostedsadresse().isEmpty()) {
                            val fellesAdresse = mapperFacade.map(hovedperson.getBostedsadresse().stream()
                                    .map(adresse -> mapperFacade.map(adresse, BostedadresseDTO.class))
                                    .findFirst()
                                    .orElse(BostedadresseDTO.builder()
                                            .vegadresse(mapperFacade.map(defaultAdresse(), VegadresseDTO.class))
                                            .build()), BostedadresseDTO.class);
                            var adressedato = nonNull(sivilstand.getSivilstandsdato()) ?
                                    sivilstand.getSivilstandsdato() :
                                    sivilstand.getBekreftelsesdato();
                            adressedato = nonNull(adressedato) ? adressedato : LocalDateTime.now().minusYears(3);
                            fellesAdresse.setGyldigFraOgMed(adressedato);
                            fellesAdresse.setAngittFlyttedato(adressedato);
                            fellesAdresse.setId(relatertPerson.getPerson().getBostedsadresse().stream()
                                                        .map(BostedadresseDTO::getId).findFirst()
                                                        .orElse(0) + 1);
                            relatertPerson.getPerson().getBostedsadresse().addFirst(fellesAdresse);
                            return personRepository.save(relatertPerson);
                        }
                        return Mono.just(relatertPerson);
                    })
                    .doOnNext(relatertPerson -> {
                        sivilstand.setBorIkkeSammen(null);
                        sivilstand.setNyRelatertPerson(null);
                        sivilstand.setRelatertVedSivilstand(relatertPerson.getIdent());
                    })
                    .then();
        }
        return Mono.empty();
    }

    private Mono<Void> createRelatertSivilstand(SivilstandDTO sivilstand, String hovedperson) {

        return personRepository.findByIdent(sivilstand.getRelatertVedSivilstand())
                .switchIfEmpty(Mono.just(DbPerson.builder()
                                .ident(sivilstand.getRelatertVedSivilstand())
                                .person(PersonDTO.builder()
                                        .ident(sivilstand.getRelatertVedSivilstand())
                                        .build())
                                .sistOppdatert(now())
                                .build())
                        .flatMap(personRepository::save))
                .doOnNext(relatertPerson -> {

                    val relatertSivilstand = mapperFacade.map(sivilstand, SivilstandDTO.class);
                    relatertSivilstand.setRelatertVedSivilstand(hovedperson);
                    relatertSivilstand.setId(relatertPerson.getPerson().getSivilstand().stream()
                                                     .max(Comparator.comparing(SivilstandDTO::getId))
                                                     .map(SivilstandDTO::getId)
                                                     .orElse(0) + 1);

                    relatertPerson.getPerson().getSivilstand().addFirst(relatertSivilstand);

                    relatertPerson.getPerson().setSivilstand(enforceIntegrity(relatertPerson.getPerson()));
                })
                .flatMap(personRepository::save)
                .then();
    }

    protected List<SivilstandDTO> enforceIntegrity(PersonDTO person) {

        val tidligsteSivilstandDato = person.getSivilstand().stream()
                .map(SivilstandDTO::getSivilstandsdato)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        val myndighetsdato = FoedselsdatoUtility.getMyndighetsdato(person);

        person.getSivilstand().forEach(stand -> {
            if (stand.isUgift() && isNull(stand.getSivilstandsdato())) {
                stand.setSivilstandsdato(tidligsteSivilstandDato.isPresent() &&
                                         tidligsteSivilstandDato.get().isBefore(myndighetsdato) ?
                        tidligsteSivilstandDato.get().minusMonths(3) :
                        FoedselsdatoUtility.getFoedselsdato(person));
            }
        });

        return person.getSivilstand().stream().noneMatch(sivilstand -> isNull(sivilstand.getSivilstandsdato())) ?
                new ArrayList<>(person.getSivilstand().stream()
                        .sorted(Comparator.comparing(SivilstandDTO::getSivilstandsdato).reversed())
                        .toList()) :
                person.getSivilstand();
    }
}
