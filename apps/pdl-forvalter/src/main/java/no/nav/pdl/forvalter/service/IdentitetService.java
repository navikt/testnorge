package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.dto.Paginering;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonIDDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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

        List<String> navn = List.of(query.split(" ")).stream()
                .filter(fragment -> isNotBlank(fragment) && !StringUtils.isNumeric(fragment))
                .toList();

        return personRepository.findByWildcardIdent(ident.orElse(null),
                !navn.isEmpty() ? navn.get(0).toUpperCase() : null,
                navn.size() > 1 ? navn.get(1).toUpperCase() : null,
                PageRequest.of(paginering.getSidenummer(),
                        paginering.getSidestoerrelse(),
                        Sort.by(SORT_BY_FIELD).descending()));
    }
}