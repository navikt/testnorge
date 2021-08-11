package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.KjoennFraIdentUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.consumer.command.VegadresseServiceCommand.defaultAdresse;
import static no.nav.pdl.forvalter.utils.SyntetiskFraIdentUtility.isSyntetisk;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO.Kjoenn.KVINNE;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO.Kjoenn.MANN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.GIFT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.REGISTRERT_PARTNER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.UGIFT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.UOPPGITT;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class SivilstandService {

    private static final String TYPE_EMPTY_ERROR = "Type av sivilstand må oppgis";
    private static final String INVALID_RELATERT_VED_SIVILSTAND = "Sivilstand: Relatert person finnes ikke";
    private static final String SIVILSTAND_DATO_REQUIRED = "Sivilstand: dato for sivilstand må oppgis";
    private static final String SIVILSTAND_OVERLAPPENDE_DATOER_ERROR = "Sivilstand: overlappende datoer er ikke gyldig";

    private static final Random RANDOM = new SecureRandom();

    private final PersonRepository personRepository;
    private final CreatePersonService createPersonService;
    private final RelasjonService relasjonService;
    private final MapperFacade mapperFacade;
    private final MergeService mergeService;

    public List<SivilstandDTO> convert(PersonDTO person) {

        for (var type : person.getSivilstand()) {

            if (isTrue(type.getIsNew())) {
                validate(type);

                type.setKilde(isNotBlank(type.getKilde()) ? type.getKilde() : "Dolly");
                type.setMaster(nonNull(type.getMaster()) ? type.getMaster() : Master.FREG);
                handle(type, person);
            }
        }
        enforceIntegrity(person.getSivilstand());
        return person.getSivilstand();
    }

    private void validate(SivilstandDTO sivilstand) {

        if (isNull(sivilstand.getType())) {
            throw new InvalidRequestException(TYPE_EMPTY_ERROR);
        }

        if (isNull(sivilstand.getSivilstandsdato()) &&
                UOPPGITT != sivilstand.getType() &&
                UGIFT != sivilstand.getType()) {
            throw new InvalidRequestException(SIVILSTAND_DATO_REQUIRED);
        }

        if ((sivilstand.getType() == GIFT ||
                sivilstand.getType() == REGISTRERT_PARTNER) &&
                isNotBlank(sivilstand.getRelatertVedSivilstand()) &&
                !personRepository.existsByIdent(sivilstand.getRelatertVedSivilstand())) {

            throw new InvalidRequestException(INVALID_RELATERT_VED_SIVILSTAND);
        }
    }

    private void handle(SivilstandDTO sivilstand, PersonDTO hovedperson) {

        if (sivilstand.getType() == GIFT ||
                sivilstand.getType() == REGISTRERT_PARTNER) {
            if (isBlank(sivilstand.getRelatertVedSivilstand())) {

                if (isNull(sivilstand.getNyRelatertPerson())) {
                    sivilstand.setNyRelatertPerson(new PersonRequestDTO());
                }
                if (isNull(sivilstand.getNyRelatertPerson().getAlder()) &&
                        isNull(sivilstand.getNyRelatertPerson().getFoedtEtter()) &&
                        isNull(sivilstand.getNyRelatertPerson().getFoedtFoer())) {

                    sivilstand.getNyRelatertPerson().setFoedtFoer(LocalDateTime.now().minusYears(30));
                    sivilstand.getNyRelatertPerson().setFoedtEtter(LocalDateTime.now().minusYears(60));
                }
                if (isNull(sivilstand.getNyRelatertPerson().getKjoenn())) {
                    KjoennDTO.Kjoenn kjonn = hovedperson.getKjoenn().stream().findFirst()
                            .map(KjoennDTO::getKjoenn)
                            .orElse(KjoennFraIdentUtility.getKjoenn(hovedperson.getIdent()));
                    sivilstand.getNyRelatertPerson().setKjoenn(kjonn == MANN ? KVINNE : MANN);
                }
                if (isNull(sivilstand.getNyRelatertPerson().getSyntetisk())) {
                    sivilstand.getNyRelatertPerson().setSyntetisk(isSyntetisk(hovedperson.getIdent()));
                }

                PersonDTO relatertPerson = createPersonService.execute(sivilstand.getNyRelatertPerson());

                if (isNotTrue(sivilstand.getBorIkkeSammen()) && !hovedperson.getBostedsadresse().isEmpty()) {
                    BostedadresseDTO fellesAdresse = hovedperson.getBostedsadresse().stream()
                            .findFirst()
                            .orElse(BostedadresseDTO.builder()
                                    .vegadresse(mapperFacade.map(defaultAdresse(), VegadresseDTO.class))
                                    .build());
                    fellesAdresse.setGyldigFraOgMed(sivilstand.getSivilstandsdato());
                    relatertPerson.getBostedsadresse().add(0, fellesAdresse);
                }

                sivilstand.setRelatertVedSivilstand(relatertPerson.getIdent());
            }
            relasjonService.setRelasjoner(hovedperson.getIdent(), RelasjonType.SIVILSTAND_RELATERT_PERSON,
                    sivilstand.getRelatertVedSivilstand(), RelasjonType.SIVILSTAND_PERSON);
            createRelatertSivilstand(sivilstand, hovedperson.getIdent());
        }

        sivilstand.setBorIkkeSammen(null);
        sivilstand.setNyRelatertPerson(null);
    }

    private void createRelatertSivilstand(SivilstandDTO sivilstand, String hovedperson) {

        DbPerson relatertPerson = personRepository.findByIdent(sivilstand.getRelatertVedSivilstand()).get();
        SivilstandDTO relatertSivilstand = mapperFacade.map(sivilstand, SivilstandDTO.class);
        relatertSivilstand.setRelatertVedSivilstand(hovedperson);
        relatertSivilstand.setBorIkkeSammen(null);
        relatertSivilstand.setNyRelatertPerson(null);
        relatertPerson.getPerson().getSivilstand().add(relatertSivilstand);
        mergeService.merge(relatertPerson.getPerson(), relatertPerson.getPerson());
        personRepository.save(relatertPerson);
    }

    private KjoennDTO randomKjoenn() {
        return KjoennDTO.builder()
                .kjoenn(RANDOM.nextBoolean() ? MANN : KVINNE)
                .build();
    }

    protected void enforceIntegrity(List<SivilstandDTO> sivilstand) {

        for (var i = 0; i < sivilstand.size(); i++) {
            if (i + 1 < sivilstand.size() &&
                sivilstand.get(i+1).getSivilstandsdato().isAfter(sivilstand.get(i).getSivilstandsdato())) {

                    throw new InvalidRequestException(SIVILSTAND_OVERLAPPENDE_DATOER_ERROR);
            }
        }
    }
}
