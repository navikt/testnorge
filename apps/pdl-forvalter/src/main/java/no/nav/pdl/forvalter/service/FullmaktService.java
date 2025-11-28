package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.utils.EgenskaperFraHovedperson;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.isNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class FullmaktService implements BiValidation<FullmaktDTO, PersonDTO> {

    private final PersonRepository personRepository;
    private final CreatePersonService createPersonService;
    private final RelasjonService relasjonService;

    public List<FullmaktDTO> convert(PersonDTO person) {

        for (var type : person.getFullmakt()) {

            if (isTrue(type.getIsNew())) {

                type.setKilde(getKilde(type));
                type.setMaster(getMaster(type, person));
                handle(type, person.getIdent());
            }
        }
        return person.getFullmakt();
    }

    @Override
    public void validate(FullmaktDTO fullmakt, PersonDTO person) {

    }

    private void handle(FullmaktDTO fullmakt, String ident) {

        fullmakt.setEksisterendePerson(isNotBlank(fullmakt.getMotpartsPersonident()));

        if (isBlank(fullmakt.getMotpartsPersonident())) {

            if (isNull(fullmakt.getNyFullmektig())) {
                fullmakt.setNyFullmektig(new PersonRequestDTO());
            }

            if (isNull(fullmakt.getNyFullmektig().getAlder()) &&
                    isNull(fullmakt.getNyFullmektig().getFoedtEtter()) &&
                    isNull(fullmakt.getNyFullmektig().getFoedtFoer())) {

                fullmakt.getNyFullmektig().setFoedtFoer(LocalDateTime.now().minusYears(18));
                fullmakt.getNyFullmektig().setFoedtEtter(LocalDateTime.now().minusYears(75));
            }

            EgenskaperFraHovedperson.kopierData(ident, fullmakt.getNyFullmektig());

            fullmakt.setMotpartsPersonident(createPersonService.execute(fullmakt.getNyFullmektig()).getIdent());

        } else {

            var motpartPerson = new AtomicReference<DbPerson>();
            personRepository.findByIdent(fullmakt.getMotpartsPersonident())
                    .ifPresentOrElse(motpartPerson::set,
                            () -> motpartPerson.set(personRepository.save(DbPerson.builder()
                                    .ident(fullmakt.getMotpartsPersonident())
                                    .person(PersonDTO.builder()
                                            .ident(fullmakt.getMotpartsPersonident())
                                            .build())
                                    .sistOppdatert(LocalDateTime.now())
                                    .build())));
        }

        relasjonService.setRelasjoner(ident, RelasjonType.FULLMAKTSGIVER,
                fullmakt.getMotpartsPersonident(), RelasjonType.FULLMEKTIG);

        fullmakt.setMaster(Master.PDL);
    }
}