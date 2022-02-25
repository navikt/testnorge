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

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.consumer.command.VegadresseServiceCommand.defaultAdresse;
import static no.nav.pdl.forvalter.utils.SyntetiskFraIdentUtility.isSyntetisk;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO.Kjoenn.KVINNE;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO.Kjoenn.MANN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.GIFT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.REGISTRERT_PARTNER;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class SivilstandService implements Validation<SivilstandDTO> {

    private static final String INVALID_RELATERT_VED_SIVILSTAND = "Sivilstand: Relatert person finnes ikke";
    private static final String SIVILSTAND_OVERLAPPENDE_DATOER_ERROR = "Sivilstand: overlappende datoer er ikke gyldig";

    private final PersonRepository personRepository;
    private final CreatePersonService createPersonService;
    private final RelasjonService relasjonService;
    private final MapperFacade mapperFacade;

    public List<SivilstandDTO> convert(PersonDTO person) {

        for (var type : person.getSivilstand()) {

            if (isTrue(type.getIsNew())) {

                type.setKilde(isNotBlank(type.getKilde()) ? type.getKilde() : "Dolly");
                type.setMaster(nonNull(type.getMaster()) ? type.getMaster() : Master.FREG);
                type.setGjeldende(nonNull(type.getGjeldende()) ? type.getGjeldende(): true);
                handle(type, person);
            }
        }
        enforceIntegrity(person.getSivilstand());
        return person.getSivilstand();
    }

    @Override
    public void validate(SivilstandDTO sivilstand) {

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
                            .map(adresse -> mapperFacade.map(adresse, BostedadresseDTO.class))
                            .findFirst()
                            .orElse(BostedadresseDTO.builder()
                                    .vegadresse(mapperFacade.map(defaultAdresse(), VegadresseDTO.class))
                                    .build());

                    fellesAdresse.setGyldigFraOgMed(sivilstand.getSivilstandsdato());
                    fellesAdresse.setId(relatertPerson.getBostedsadresse().stream()
                            .map(BostedadresseDTO::getId).findFirst()
                            .orElse(0) + 1);
                    relatertPerson.getBostedsadresse().add(0, fellesAdresse);
                }

                sivilstand.setBorIkkeSammen(null);
                sivilstand.setNyRelatertPerson(null);
                sivilstand.setRelatertVedSivilstand(relatertPerson.getIdent());
            }

            relasjonService.setRelasjoner(hovedperson.getIdent(), RelasjonType.EKTEFELLE_PARTNER,
                    sivilstand.getRelatertVedSivilstand(), RelasjonType.EKTEFELLE_PARTNER);
            createRelatertSivilstand(sivilstand, hovedperson.getIdent());
        }
    }

    private void createRelatertSivilstand(SivilstandDTO sivilstand, String hovedperson) {

        DbPerson relatertPerson = personRepository.findByIdent(sivilstand.getRelatertVedSivilstand()).get();
        SivilstandDTO relatertSivilstand = mapperFacade.map(sivilstand, SivilstandDTO.class);
        relatertSivilstand.setRelatertVedSivilstand(hovedperson);
        relatertSivilstand.setId(relatertPerson.getPerson().getSivilstand().stream()
                .map(SivilstandDTO::getId)
                .findFirst()
                .orElse(0) + 1);
        relatertPerson.getPerson().getSivilstand().add(relatertSivilstand);
        personRepository.save(relatertPerson);
    }

    protected void enforceIntegrity(List<SivilstandDTO> sivilstand) {

        for (var i = 0; i < sivilstand.size(); i++) {
            if (i + 1 < sivilstand.size() &&
                    sivilstand.get(i + 1).getSivilstandsdato().isAfter(sivilstand.get(i).getSivilstandsdato())) {

                throw new InvalidRequestException(SIVILSTAND_OVERLAPPENDE_DATOER_ERROR);
            }
        }
    }
}
