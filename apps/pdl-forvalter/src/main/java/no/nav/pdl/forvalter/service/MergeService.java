package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.domain.DbVersjonDTO;
import no.nav.pdl.forvalter.domain.PersonDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

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

    public PersonDTO merge(PersonDTO request, PersonDTO dbPerson) {

        Stream.of(request.getClass().getDeclaredFields()).forEach(field -> {

            if (List.class.equals(field.getType()) && !((List<DbVersjonDTO>) getValue(request, field.getName())).isEmpty()) {

                var infoElementRequest = (List<DbVersjonDTO>) getValue(request, field.getName());
                var infoElementDbPerson = (List<DbVersjonDTO>) getValue(dbPerson, field.getName());
                var dbId = new AtomicInteger(infoElementDbPerson.stream()
                        .mapToInt(DbVersjonDTO::getId)
                        .max().orElse(0));

                infoElementRequest.forEach(requestElement -> {
                    if (infoElementDbPerson.stream()
                            .anyMatch(dbElement -> nonNull(requestElement.getId()) && requestElement.getId().equals(dbElement.getId()))) {
                        for (var i = 0; i < infoElementDbPerson.size(); i++) {
                            if (infoElementDbPerson.get(i).getId().equals(requestElement.getId())) {
                                infoElementDbPerson.set(i, mapperFacade.map(requestElement, infoElementDbPerson.get(i).getClass()));
                            }
                        }
                    } else if (nonNull(requestElement.getId()) && requestElement.getId() > dbId.get()) {
                        throw new HttpClientErrorException(BAD_REQUEST,
                                format("Merge-error: id:%s ikke funnet for element:'%s'", requestElement.getId(), field.getName()));
                    } else {
                        requestElement.setId(dbId.incrementAndGet());
                        requestElement.setNew(true);
                        infoElementDbPerson.add(0, mapperFacade.map(requestElement, requestElement.getClass()));
                    }
                });
            }
        });

        return dbPerson;
    }
}
