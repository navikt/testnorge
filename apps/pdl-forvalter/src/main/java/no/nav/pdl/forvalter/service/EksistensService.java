package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.consumer.IdentPoolConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.dto.IdentpoolLedigDTO;
import no.nav.pdl.forvalter.dto.ProdSjekkDTO;
import no.nav.pdl.forvalter.utils.IdentValidCheck;
import no.nav.testnav.libs.data.pdlforvalter.v1.AvailibilityResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EksistensService {

    private static final String INVALID_IDENT = "Ikke gyldig ident";
    private static final String IDENT_EXISTS_IN_DB = "Ikke ledig -- ident finnes allerede i database";
    private static final String IDENT_EXISTS_IN_ENV = "Ikke ledig -- ident er i bruk";
    private static final String IDENT_EXISTS_IN_PROD = "Ikke ledig -- ident er i bruk i prod";
    private static final String IDENT_AVAIL = "Gyldig og ledig";

    private final PersonRepository personRepository;
    private final IdentPoolConsumer identPoolConsumer;


    public Flux<AvailibilityResponseDTO> checkAvailibility(List<String> identer) {

        var validIdents = IdentValidCheck.isIdentValid(identer);

        var existsInDatabase = personRepository.findByIdentIn(validIdents, Pageable.unpaged()).stream()
                .map(DbPerson::getIdent)
                .collect(Collectors.toSet());

        return Mono.zip(
                        identPoolConsumer.getErLedig(new HashSet<>(validIdents))
                                .filter(ident -> !existsInDatabase.contains(ident.getIdent()))
                                .collectList(),
                        identPoolConsumer.getProdSjekk(new HashSet<>(validIdents))
                                .filter(ident -> !existsInDatabase.contains(ident.getIdent()))
                                .collectList())
                .flatMapMany(tuple -> Flux.concat(
                        Flux.fromIterable(identer)
                                .filter(ident -> !validIdents.contains(ident))
                                .map(ident -> new AvailibilityResponseDTO(ident, INVALID_IDENT, false)),
                        Flux.fromIterable(existsInDatabase)
                                .map(ident -> new AvailibilityResponseDTO(ident, IDENT_EXISTS_IN_DB, false)),
                        Flux.fromIterable(tuple.getT1())
                                .filter(IdentpoolLedigDTO::isIBruk)
                                .filter(ident -> tuple.getT2().stream()
                                        .noneMatch(prodSjekk ->
                                                prodSjekk.getIdent().equals(ident.getIdent()) &&
                                                prodSjekk.isInUse()))
                                .map(IdentpoolLedigDTO::getIdent)
                                .map(ident -> new AvailibilityResponseDTO(ident, IDENT_EXISTS_IN_ENV, false)),
                        Flux.fromIterable(tuple.getT2())
                                .filter(ProdSjekkDTO::isInUse)
                                .map(ProdSjekkDTO::getIdent)
                                .map(ident -> new AvailibilityResponseDTO(ident, IDENT_EXISTS_IN_PROD, false)),
                        Flux.fromIterable(validIdents)
                                .filter(ident -> !existsInDatabase.contains(ident))
                                .filter(ident -> tuple.getT1().stream()
                                        .noneMatch(identpoolLedigDTO ->
                                                identpoolLedigDTO.getIdent().equals(ident) &&
                                                identpoolLedigDTO.isIBruk()))
                                .map(ident -> new AvailibilityResponseDTO(ident, IDENT_AVAIL, true))));
    }
}