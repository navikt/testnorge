package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedsfallDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static java.util.Objects.isNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.renumberId;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class DoedsfallService implements Validation<DoedsfallDTO> {

    private static final String INVALID_DATO_MISSING = "Dødsfall: dødsdato må oppgis";
    private final PersonRepository personRepository;

    public List<DoedsfallDTO> convert(PersonDTO person) {

        for (var type : person.getDoedsfall()) {
            if (isTrue(type.getIsNew())) {

                type.setKilde(getKilde(type));
                type.setMaster(getMaster(type, person));
            }
        }

        person.getDoedsfall().sort(Comparator.comparing(DoedsfallDTO::getDoedsdato).reversed());
        renumberId(person.getDoedsfall());

        handle(person);

        return person.getDoedsfall();
    }

    private void handle(PersonDTO person) {

        if (!person.getDoedsfall().isEmpty() &&
                !person.getSivilstand().isEmpty() &&
                isNotBlank(person.getSivilstand().getFirst().getRelatertVedSivilstand()) &&
                person.getSivilstand().getFirst().isGift()) {

            personRepository.findByIdent(person.getSivilstand().getFirst().getRelatertVedSivilstand())
                    .ifPresent(person1 ->
                            person1.getPerson().getSivilstand().addFirst(
                                    SivilstandDTO.builder()
                                            .type(person.getSivilstand().getFirst().getGjenlevendeSivilstand())
                                            .sivilstandsdato(person.getDoedsfall().getFirst().getDoedsdato())
                                            .kilde(person.getDoedsfall().getFirst().getKilde())
                                            .master(person.getDoedsfall().getFirst().getMaster())
                                            .id(person1.getPerson().getSivilstand().size() + 1)
                                            .build()));
        }
    }

    @Override
    public void validate(DoedsfallDTO type) {

        if (isNull(type.getDoedsdato())) {

            throw new InvalidRequestException(INVALID_DATO_MISSING);
        }
    }
}
