package no.nav.registre.testnorge.personsearchservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.personsearchservice.adapter.IdentSearchAdapter;
import no.nav.testnav.libs.dto.personsearchservice.v1.IdentdataDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class IdentService {

    private final IdentSearchAdapter identSearchAdapter;

    public List<IdentdataDTO> getIdenter(String query) {

        Optional<String> ident = Stream.of(query.split(" "))
                .filter(StringUtils::isNumeric)
                .findFirst();

        List<String> navn = List.of(query.split(" ")).stream()
                .filter(fragment -> isNotBlank(fragment) && !StringUtils.isNumeric(fragment))
                .toList();

//       identSearchAdapter.
//        return personRepository.findByWildcardIdent(ident.orElse(null),
//                !navn.isEmpty() ? navn.get(0).toUpperCase() : null,
//                navn.size() > 1 ? navn.get(1).toUpperCase() : null,
//                PageRequest.of(paginering.getSidenummer(),
//                        paginering.getSidestoerrelse(),
//                        Sort.by(SORT_BY_FIELD).descending()));
//    }
//
//    @Query("from DbPerson p "
//            + "where (:partialIdent is null or :partialIdent is not null and p.ident like %:partialIdent%)"
//            + "and (:partialNavn1 is null or :partialNavn1 is not null and (upper(p.etternavn) like %:partialNavn1% or upper(p.fornavn) like %:partialNavn1%))"
//            + "and (:partialNavn2 is null or :partialNavn2 is not null and (upper(p.etternavn) like %:partialNavn2% or upper(p.fornavn) like %:partialNavn2%))")
        return null;
    }
}
