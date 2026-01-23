package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregistermetadataDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class MergeService {

    private final MapperFacade mapperFacade;

    private static List<DbVersjonDTO> getValue(Object object, String field) {

        Method method = null;
        var methodName = format("get%s%s", field.substring(0, 1).toUpperCase(), field.substring(1));
        try {
            method = object
                    .getClass()
                    .getMethod(methodName);
            return castValue(method.invoke(object));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("Feilet å lese verdi fra {}, felt {}", object, nonNull(method) ? method.getName() : null, e);
            return null;
        }

    }

    @SuppressWarnings("unchecked")
    private static List<DbVersjonDTO> castValue(Object value) {
        return (List<DbVersjonDTO>) value;
    }

    public PersonDTO merge(PersonDTO request, PersonDTO dbPerson) {

        if (!request.getTelefonnummer().isEmpty()) {
            dbPerson.setTelefonnummer(null);
        }
        Stream
                .of(request.getClass().getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers()))
                .forEach(field -> {

                    if (List.class.equals(field.getType()) && !(getValue(request, field.getName())).isEmpty()) {

                        var infoElementRequest = getValue(request, field.getName());
                        if (infoElementRequest != null) {
                            var infoElementDbPerson = getValue(dbPerson, field.getName());
                            if (infoElementDbPerson != null) {
                                var dbId = new AtomicInteger(infoElementDbPerson.stream()
                                        .mapToInt(DbVersjonDTO::getId)
                                        .max().orElse(0));
                                infoElementRequest.forEach(requestElement -> mergeElements(field, infoElementDbPerson, dbId, requestElement));
                            }
                        }

                    }
                });

        return dbPerson;
    }

    private void mergeElements(java.lang.reflect.Field field, List<DbVersjonDTO> infoElementDbPerson, AtomicInteger dbId, DbVersjonDTO requestElement) {

        if (infoElementDbPerson.stream()
                .anyMatch(dbElement -> nonNull(requestElement.getId()) && requestElement.getId().equals(dbElement.getId()))) {
            for (var i = 0; i < infoElementDbPerson.size(); i++) {
                if (infoElementDbPerson.get(i).getId().equals(requestElement.getId())) {
                    infoElementDbPerson.set(i, mapperFacade.map(requestElement, infoElementDbPerson.get(i).getClass()));
                }
            }
        } else if (nonNull(requestElement.getId()) && requestElement.getId() > dbId.get()) {
            throw new InvalidRequestException(
                    format("Merge-error: id:%s ikke funnet for element:'%s'", requestElement.getId(), field.getName()));
        } else {
            requestElement.setId(dbId.incrementAndGet());
            requestElement.setIsNew(true);
            if (isNull(requestElement.getFolkeregistermetadata())) {
                requestElement.setFolkeregistermetadata(new FolkeregistermetadataDTO());
            }
            infoElementDbPerson.addFirst(mapperFacade.map(requestElement, requestElement.getClass()));
        }
    }
}
