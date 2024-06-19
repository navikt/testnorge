package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.dto.Paginering;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonIDDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
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

        personRepository.findByIdent(ident)
                .ifPresent(dbPerson -> dbPerson.getRelasjoner().stream()
                        .map(DbRelasjon::getRelatertPerson)
                        .map(DbPerson::getPerson)
                        .distinct()
                        .forEach(person -> {
                            person.getSivilstand()
                                    .forEach(sivilstand -> {
                                        if (ident.equals(sivilstand.getRelatertVedSivilstand())) {
                                            sivilstand.setEksisterendePerson(standalone);
                                        }
                                    });

                            person.getForelderBarnRelasjon()
                                    .forEach(relasjon -> {
                                        if (ident.equals(relasjon.getRelatertPerson())) {
                                            relasjon.setEksisterendePerson(standalone);
                                        }
                                    });

                            person.getForeldreansvar()
                                    .forEach(ansvar -> {
                                        if (ident.equals(ansvar.getAnsvarlig())) {
                                            ansvar.setEksisterendePerson(standalone);
                                        }
                                    });

                            person.getKontaktinformasjonForDoedsbo()
                                    .forEach(doedsbo -> {
                                        if (nonNull(doedsbo.getPersonSomKontakt()) &&
                                                ident.equals(doedsbo.getPersonSomKontakt().getIdentifikasjonsnummer())) {
                                            doedsbo.getPersonSomKontakt().setEksisterendePerson(standalone);
                                        }
                                    });

                            person.getVergemaal()
                                    .forEach(vergemaal -> {
                                        if (ident.equals(vergemaal.getVergeIdent())) {
                                            vergemaal.setEksisterendePerson(standalone);
                                        }
                                    });

                            person.getFullmakt()
                                    .forEach(fullmakt -> {
                                        if (ident.equals(fullmakt.getMotpartsPersonident())) {
                                            fullmakt.setEksisterendePerson(standalone);
                                        }
                                    });

                            person.getFalskIdentitet()
                                    .forEach(falskId -> {
                                        if (ident.equals(falskId.getRettIdentitetVedIdentifikasjonsnummer())) {
                                            falskId.setEksisterendePerson(standalone);
                                        }
                                    });
                        }));
    }
}