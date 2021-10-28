package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.consumer.IdentPoolConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.dto.IdentpoolStatusDTO;
import no.nav.pdl.forvalter.utils.IdentValidCheck;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AvailibilityResponseDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.nav.pdl.forvalter.dto.IdentpoolStatusDTO.Rekvireringsstatus.I_BRUK;
import static no.nav.pdl.forvalter.dto.IdentpoolStatusDTO.Rekvireringsstatus.LEDIG;

@Slf4j
@Service
@RequiredArgsConstructor
public class EksistensService {

    private static final String INVALID_IDENT = "Ikke gyldig ident";
    private static final String IDENT_EXISTS_IN_DB = "Ikke ledig -- ident finnes allerede i database";
    private static final String IDENT_EXISTS_IN_ENV = "Ikke ledig -- ident er i bruk";
    private static final String IDENT_AVAIL = "Gyldig og ledig";

    private final PersonRepository personRepository;
    private final IdentPoolConsumer identPoolConsumer;


    public List<AvailibilityResponseDTO> checkAvailibility(List<String> identer) {

        var validIdents = IdentValidCheck.isIdentValid(identer);

        var existsInDatabase = personRepository.findByIdentIn(validIdents, PageRequest.of(0, 1000)).stream()
                .map(DbPerson::getIdent)
                .collect(Collectors.toSet());

        var identpoolStatus = identPoolConsumer.getIdents(validIdents.stream()
                        .filter(ident -> !existsInDatabase.contains(ident))
                        .collect(Collectors.toSet()))
                .collectList()
                .block();

        return Stream.of(identer.stream()
                                .filter(ident -> !validIdents.contains(ident))
                                .map(ident -> new AvailibilityResponseDTO(ident, INVALID_IDENT, false))
                                .toList(),
                        existsInDatabase.stream()
                                .map(ident -> new AvailibilityResponseDTO(ident, IDENT_EXISTS_IN_DB, false))
                                .toList(),
                        identpoolStatus.stream()
                                .filter(identStatus -> I_BRUK == identStatus.getRekvireringsstatus())
                                .map(IdentpoolStatusDTO::getPersonidentifikator)
                                .map(ident -> new AvailibilityResponseDTO(ident, IDENT_EXISTS_IN_ENV, false))
                                .toList(),
                        identpoolStatus.stream()
                                .filter(identStatus -> LEDIG == identStatus.getRekvireringsstatus())
                                .map(IdentpoolStatusDTO::getPersonidentifikator)
                                .map(ident -> new AvailibilityResponseDTO(ident, IDENT_AVAIL, true))
                                .toList()
                )
                .flatMap(Collection::stream)
                .toList();
    }
}