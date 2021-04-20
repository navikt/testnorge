package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.domain.PdlDbVersjon;
import no.nav.pdl.forvalter.domain.PdlPerson;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
public class MergeService implements Callable<PdlPerson> {

    private final PdlPerson request;
    private final PdlPerson dbPerson;
    private final MapperFacade mapperFacade;

    private static Object getValue(Object object, String field) {

        Method method = null;
        try {
            method = object.getClass().getMethod(
                    format("get%s%s", field.substring(0, 1).toUpperCase(), field.substring(1)),
                    null);
            return method.invoke(object, null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {

            log.error("Feilet å lese verdi fra {}, felt {}", object, nonNull(method) ? method.getName() : null, e);
            return null;
        }
    }

    public PdlPerson call() {

        Field[] fields = request.getClass().getDeclaredFields();
        Stream.of(fields).forEach(field -> {

            if (List.class.equals(field.getType())) {

                List<PdlDbVersjon> infoElementRequest = (List<PdlDbVersjon>) getValue(request, field.getName());

                List<PdlDbVersjon> infoElementDbPerson = (List<PdlDbVersjon>) getValue(dbPerson, field.getName());
                AtomicInteger dbId = new AtomicInteger(infoElementDbPerson.isEmpty() ? 0 :
                        infoElementDbPerson.stream().findFirst().get().getId());

                infoElementRequest.forEach(requestElement -> {
                    if (isNull(requestElement.getId()) || requestElement.getId() == 0) {
                        requestElement.setId(dbId.incrementAndGet());
                        infoElementDbPerson.add(0, requestElement);
                    } else if (requestElement.equals(infoElementRequest.stream().anyMatch(dbElement -> dbElement.equals(requestElement)))) {
                        for (int i = 0; i < infoElementDbPerson.size(); i++) {
                            if (infoElementDbPerson.get(i).getId() == requestElement.getId()) {
                                infoElementDbPerson.set(i, mapperFacade.map(requestElement, infoElementDbPerson.get(i).getClass()));
                            }
                        }
                    }
                });
            }
        });

        return dbPerson;
    }
}
