package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.dto.Paginering;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.exception.NotFoundException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonIDDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentitetService {

    private static final String FRAGMENT_INVALID = "Søkefragment kan ikke være tomt";

    private static final String SORT_BY_FIELD = "sistOppdatert";

    private final PersonRepository personRepository;

    @Transactional(readOnly = true)
    public List<PersonIDDTO> getfragment(String fragment, Paginering paginering) {

        if (isBlank(fragment)) {
            throw new InvalidRequestException(FRAGMENT_INVALID);
        }

        return searchPerson(fragment, paginering).stream()
                .map(person -> PersonIDDTO.builder()
                        .ident(person.getIdent())
                        .fornavn(person.getFornavn())
                        .mellomnavn(person.getMellomnavn())
                        .etternavn(person.getEtternavn())
                        .build())
                .toList();
    }

    private List<DbPerson> searchPerson(String query, Paginering paginering) {

        Optional<String> ident = Stream.of(query.split(" "))
                .filter(StringUtils::isNumeric)
                .findFirst();

        List<String> navn = Stream.of(query.split(" "))
                .filter(fragment -> isNotBlank(fragment) && !StringUtils.isNumeric(fragment))
                .toList();

        return personRepository.findByWildcardIdent(ident.orElse(null),
                !navn.isEmpty() ? navn.getFirst().toUpperCase() : null,
                navn.size() > 1 ? navn.get(1).toUpperCase() : null,
                PageRequest.of(paginering.getSidenummer(),
                        paginering.getSidestoerrelse(),
                        Sort.by(SORT_BY_FIELD).descending()));
    }

    @Transactional
    public void updateStandalone(String ident, Boolean standalone) {

        var dbPerson = personRepository.findByIdent(ident)
                .orElseThrow(() -> new NotFoundException("Ident " + ident + " ikke funnet"));

        dbPerson.getPerson().setStandalone(standalone);

        var identerRelasjon = dbPerson.getRelasjoner().stream()
                .map(DbRelasjon::getRelatertPerson)
                .map(DbPerson::getPerson)
                .flatMap(person -> Arrays.stream(PersonDTO.class.getMethods())
                        .filter(method -> method.getName().contains("get"))
                        .map(method -> {
                            try {
                                return method.invoke(person);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                log.error("Feilet å utføre updateStandalone for {} ", method);
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .filter(List.class::isInstance)
                        .map(IdentitetService::castValue)
                        .flatMap(Collection::stream)
                        .map(DbVersjonDTO::getIdentForRelasjon)
                        .filter(Objects::nonNull)
                        .filter(ident1 -> ident1.equals(ident))
                        .map(ident1 -> person.getIdent())
                        .distinct())
                .toList();

        identerRelasjon
                .forEach(relasjonsident -> setStandalonePerson(dbPerson, relasjonsident, standalone));

        personRepository.findByIdentIn(identerRelasjon, Pageable.ofSize(100))
                .forEach(relasjonPerson -> setStandalonePerson(relasjonPerson, ident, standalone));
    }

    private void setStandalonePerson(DbPerson person, String motpartsIdent, Boolean standalone) {

        Stream.of(person)
                .map(DbPerson::getPerson)
                .flatMap(person1 ->
                        Arrays.stream(PersonDTO.class.getMethods())
                                .filter(method -> method.getName().contains("get"))
                                .map(method -> {
                                    try {
                                        return method.invoke(person1);
                                    } catch (IllegalAccessException | InvocationTargetException e) {
                                        log.error("Feilet å utføre setStandalone for {} ", method);
                                        return null;
                                    }
                                })
                                .filter(Objects::nonNull)
                                .filter(List.class::isInstance)
                                .map(IdentitetService::castValue)
                                .flatMap(Collection::stream)
                )
                .forEach(opplysning -> {
                    if (motpartsIdent.equals(opplysning.getIdentForRelasjon())) {
                        try {
                            var method = opplysning.getClass().getMethod("setEksisterendePerson", Boolean.class);
                            method.invoke(opplysning, isTrue(standalone));

                        } catch (NoSuchMethodException | InvocationTargetException |
                                 IllegalAccessException e) {
                            log.error("Method setEksisterendePerson not found in {}", opplysning);
                        }
                    }
                });
    }

    @SuppressWarnings("unchecked")
    private static List<? extends DbVersjonDTO> castValue(Object value) {
        return (List<? extends DbVersjonDTO>) value;
    }

}