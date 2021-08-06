package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.consumer.command.VegadresseServiceCommand.defaultAdresse;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.GIFT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.REGISTRERT_PARTNER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.UGIFT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.UOPPGITT;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class SivilstandService {

    private static final String TYPE_EMPTY_ERROR = "Type av sivilstand må oppgis";
    private static final String INVALID_RELATERT_VED_SIVILSTAND = "Sivilstand: Relatert person finnes ikke";
    private static final String SIVILSTAND_DATO_REQUIRED = "Sivilstand: dato for sivilstand må oppgis";

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
                handle(type, person.getIdent());
            }
        }
        return person.getSivilstand();
    }

    protected void validate(SivilstandDTO sivilstand) {

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

    protected void handle(SivilstandDTO sivilstand, String ident) {

        if (sivilstand.getType() == GIFT ||
                sivilstand.getType() == REGISTRERT_PARTNER) {
            if (isBlank(sivilstand.getRelatertVedSivilstand())) {
                // Person ikke forhåndsvalgt, lager ny ...
                BostedadresseDTO bostedadresseDTO = null;
                if (isTrue(sivilstand.getBorIkkeSammen())) {
                    bostedadresseDTO = getBostedadresse(sivilstand, ident);
                }
                PersonDTO relatertPerson = createPersonService.execute(PersonRequestDTO.builder()
                        .bostedadresseDTO(bostedadresseDTO)
                        .build());
                sivilstand.setRelatertVedSivilstand(relatertPerson.getIdent());
            }
            relasjonService.setRelasjoner(ident, RelasjonType.SIVILSTAND_RELATERT_PERSON,
                    sivilstand.getRelatertVedSivilstand(), RelasjonType.SIVILSTAND_PERSON);
            createRelatertSivilstand(sivilstand, ident);
        }

        if (isNotBlank(sivilstand.getMyndighet()) || isNotBlank(sivilstand.getSted())) {
            sivilstand.setMaster(Master.FREG);
        }
        sivilstand.setBorIkkeSammen(null);
        sivilstand.setNyRelatertPerson(null);
    }

    private void createRelatertSivilstand(SivilstandDTO sivilstand, String hovedperson) {

        DbPerson relatertPerson = personRepository.findByIdent(sivilstand.getRelatertVedSivilstand()).get();
        SivilstandDTO relatertSivilstand = mapperFacade.map(sivilstand, SivilstandDTO.class);
        relatertSivilstand.setRelatertVedSivilstand(hovedperson);
        relatertPerson.getPerson().getSivilstand().add(relatertSivilstand);
        mergeService.merge(relatertPerson.getPerson(), relatertPerson.getPerson());
        personRepository.save(relatertPerson);
    }

    private BostedadresseDTO getBostedadresse(SivilstandDTO sivilstand, String ident) {
        return nonNull(sivilstand.getNyRelatertPerson()) &&
                nonNull(sivilstand.getNyRelatertPerson().getBostedadresseDTO()) ?
                sivilstand.getNyRelatertPerson().getBostedadresseDTO() :
                personRepository.findByIdent(ident).get().getPerson()
                        .getBostedsadresse().stream().findFirst().orElse(BostedadresseDTO.builder()
                        .vegadresse(mapperFacade.map(defaultAdresse(), VegadresseDTO.class))
                        .build());
    }

    protected void enforceIntegrity(List<SivilstandDTO> type) {

        // Ingen listeintegritet å ivareta
    }
}
