package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.FoedselsdatoUtility;
import no.nav.pdl.forvalter.utils.KjoennFraIdentUtility;
import no.nav.pdl.forvalter.utils.KjoennUtility;
import no.nav.testnav.libs.data.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType;
import no.nav.testnav.libs.data.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.VegadresseDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.consumer.command.VegadresseServiceCommand.defaultAdresse;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.renumberId;
import static no.nav.pdl.forvalter.utils.SyntetiskFraIdentUtility.isSyntetisk;
import static no.nav.pdl.forvalter.utils.TestnorgeIdentUtility.isTestnorgeIdent;
import static no.nav.testnav.libs.data.pdlforvalter.v1.SivilstandDTO.Sivilstand.SAMBOER;
import static no.nav.testnav.libs.data.pdlforvalter.v1.SivilstandDTO.Sivilstand.UGIFT;
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

    public List<SivilstandDTO> convert(PersonDTO person) {

        for (var type : person.getSivilstand()) {

            if (isTrue(type.getIsNew())) {

                type.setKilde(getKilde(type));
                type.setMaster(getMaster(type, person));

                handle(type, person);
            }
        }

        var oppdatertSivilstand = enforceIntegrity(person);
        renumberId(oppdatertSivilstand);

        return oppdatertSivilstand;
    }

    @Override
    public void validate(SivilstandDTO sivilstand, PersonDTO person) {

        if (!isTestnorgeIdent(person.getIdent()) && (sivilstand.isGift() ||
                sivilstand.isSeparert() ||
                sivilstand.getType() == SAMBOER) &&
                isNotBlank(sivilstand.getRelatertVedSivilstand()) &&
                !personRepository.existsByIdent(sivilstand.getRelatertVedSivilstand())) {

            throw new InvalidRequestException(INVALID_RELATERT_VED_SIVILSTAND);
        }
    }

    private void handle(SivilstandDTO sivilstand, PersonDTO hovedperson) {

        if (isNull(sivilstand.getType())) {

            sivilstand.setType(UGIFT);
        }

        if (sivilstand.isGift() || sivilstand.isSeparert() || sivilstand.isSamboer()) {

            sivilstand.setEksisterendePerson(isNotBlank(sivilstand.getRelatertVedSivilstand()));
            if (isBlank(sivilstand.getRelatertVedSivilstand())) {

                if (isNull(sivilstand.getNyRelatertPerson())) {
                    sivilstand.setNyRelatertPerson(new PersonRequestDTO());
                }
                if (isNull(sivilstand.getNyRelatertPerson().getAlder()) &&
                        isNull(sivilstand.getNyRelatertPerson().getFoedtEtter()) &&
                        isNull(sivilstand.getNyRelatertPerson().getFoedtFoer())) {
                    var foedselsdato = FoedselsdatoUtility.getFoedselsdato(hovedperson);
                    sivilstand.getNyRelatertPerson().setFoedtFoer(foedselsdato.plusYears(2));
                    sivilstand.getNyRelatertPerson().setFoedtEtter(foedselsdato.minusYears(2));
                }
                if (isNull(sivilstand.getNyRelatertPerson().getKjoenn())) {
                    KjoennDTO.Kjoenn kjoenn = hovedperson.getKjoenn().stream().findFirst()
                            .map(KjoennDTO::getKjoenn)
                            .orElse(KjoennFraIdentUtility.getKjoenn(hovedperson.getIdent()));
                    sivilstand.getNyRelatertPerson().setKjoenn(KjoennUtility.getPartnerKjoenn(kjoenn));
                }
                if (isNull(sivilstand.getNyRelatertPerson().getSyntetisk())) {
                    sivilstand.getNyRelatertPerson().setSyntetisk(isSyntetisk(hovedperson.getIdent()));
                }

                PersonDTO relatertPerson = createPersonService.execute(sivilstand.getNyRelatertPerson());

                if (isNotTrue(sivilstand.getBorIkkeSammen()) && !hovedperson.getBostedsadresse().isEmpty()) {
                    var fellesAdresse = mapperFacade.map(hovedperson.getBostedsadresse().stream()
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
                    fellesAdresse.setId(relatertPerson.getBostedsadresse().stream()
                            .map(BostedadresseDTO::getId).findFirst()
                            .orElse(0) + 1);
                    relatertPerson.getBostedsadresse().addFirst(fellesAdresse);
                }

                sivilstand.setBorIkkeSammen(null);
                sivilstand.setNyRelatertPerson(null);
                sivilstand.setRelatertVedSivilstand(relatertPerson.getIdent());
            }

            relasjonService.setRelasjoner(hovedperson.getIdent(), RelasjonType.EKTEFELLE_PARTNER,
                    sivilstand.getRelatertVedSivilstand(), RelasjonType.EKTEFELLE_PARTNER);
            createRelatertSivilstand(sivilstand, hovedperson.getIdent());

        } else {
            sivilstand.setRelatertVedSivilstand(null);
        }
    }

    private void createRelatertSivilstand(SivilstandDTO sivilstand, String hovedperson) {

        var relatertPerson = new AtomicReference<>(new DbPerson());
        personRepository.findByIdent(sivilstand.getRelatertVedSivilstand())
                .ifPresentOrElse(relatertPerson::set,
                        () -> relatertPerson.set(personRepository.save(DbPerson.builder()
                                .ident(sivilstand.getRelatertVedSivilstand())
                                .person(PersonDTO.builder()
                                        .ident(sivilstand.getRelatertVedSivilstand())
                                        .build())
                                .sistOppdatert(now())
                                .build())));

        var relatertSivilstand = mapperFacade.map(sivilstand, SivilstandDTO.class);
        relatertSivilstand.setRelatertVedSivilstand(hovedperson);
        relatertSivilstand.setId(relatertPerson.get().getPerson().getSivilstand().stream()
                .max(Comparator.comparing(SivilstandDTO::getId))
                .map(SivilstandDTO::getId)
                .orElse(0) + 1);

        relatertPerson.get().getPerson().getSivilstand().addFirst(relatertSivilstand);

        relatertPerson.get().getPerson().setSivilstand(enforceIntegrity(relatertPerson.get().getPerson()));
    }

    protected List<SivilstandDTO> enforceIntegrity(PersonDTO person) {

        var tidligsteSivilstandDato = person.getSivilstand().stream()
                .map(SivilstandDTO::getSivilstandsdato)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        var myndighetsdato = FoedselsdatoUtility.getMyndighetsdato(person);

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
