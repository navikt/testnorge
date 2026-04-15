package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregistermetadataDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class MergeService {

    private final MapperFacade mapperFacade;

    private static Object getValue(Object object, String field) {

        Method method = null;
        try {
            method = object.getClass().getMethod(
                    format("get%s%s", field.substring(0, 1).toUpperCase(), field.substring(1)), null);
            return method.invoke(object, null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {

            log.error("Feilet å lese verdi fra {}, felt {}", object, nonNull(method) ? method.getName() : null, e);
            return null;
        }
    }

    public Mono<DbPerson> merge(PersonDTO request, DbPerson dbPerson) {

        if (isNull(dbPerson.getPerson())) {
            dbPerson.setPerson(PersonDTO.builder()
                    .ident(request.getIdent())
                    .build());
        }

        if (!request.getTelefonnummer().isEmpty()) {
            dbPerson.getPerson().setTelefonnummer(null);
        }

        return Flux.fromArray(request.getClass().getDeclaredFields())
                .filter(field ->
                        !Modifier.isStatic(field.getModifiers()) &&
                        !Modifier.isFinal(field.getModifiers()))
                .filter(field -> List.class.equals(field.getType()) && !((List<DbVersjonDTO>) getValue(request, field.getName())).isEmpty())
                .flatMap(field -> {
                    val infoElementRequest = (List<DbVersjonDTO>) getValue(request, field.getName());
                    val infoElementDbPerson = (List<DbVersjonDTO>) getValue(dbPerson.getPerson(), field.getName());
                    val dbId = new AtomicInteger(infoElementDbPerson.stream()
                            .mapToInt(DbVersjonDTO::getId)
                            .max().orElse(0));
                    return Mono.zip(Mono.just(field), Mono.just(infoElementRequest), Mono.just(infoElementDbPerson), Mono.just(dbId));
                })
                .flatMap(tuple -> Flux.fromIterable(tuple.getT2())
                        .flatMap(requestElement -> mergeElements(tuple.getT1(), tuple.getT3(), tuple.getT4(), requestElement)))
                .then(Mono.just(dbPerson));
    }

    private Mono<Void> mergeElements(java.lang.reflect.Field field, List<DbVersjonDTO> infoElementDbPerson, AtomicInteger dbId, DbVersjonDTO requestElement) {

        if (infoElementDbPerson.stream()
                .anyMatch(dbElement -> nonNull(requestElement.getId()) && requestElement.getId().equals(dbElement.getId()))) {
            for (var i = 0; i < infoElementDbPerson.size(); i++) {
                if (infoElementDbPerson.get(i).getId().equals(requestElement.getId())) {
                    infoElementDbPerson.set(i, mapperFacade.map(requestElement, infoElementDbPerson.get(i).getClass()));
                }
            }
        } else if (nonNull(requestElement.getId()) && requestElement.getId() > dbId.get()) {
            return Mono.error(new InvalidRequestException(
                    "Merge-error: id:%s ikke funnet for element:'%s'".formatted(requestElement.getId(), field.getName())));
        } else {
            requestElement.setId(dbId.incrementAndGet());
            requestElement.setIsNew(true);
            if (isNull(requestElement.getFolkeregistermetadata())) {
                requestElement.setFolkeregistermetadata(new FolkeregistermetadataDTO());
            }
            infoElementDbPerson.addFirst(mapperFacade.map(requestElement, requestElement.getClass()));
        }
        return Mono.empty();
    }
}
