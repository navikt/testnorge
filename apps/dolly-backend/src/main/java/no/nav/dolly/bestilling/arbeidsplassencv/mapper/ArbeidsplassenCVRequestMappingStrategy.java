package no.nav.dolly.bestilling.arbeidsplassencv.mapper;

import lombok.SneakyThrows;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.arbeidsplassencv.v1.ArbeidsplassenCVDTO;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
public class ArbeidsplassenCVRequestMappingStrategy implements MappingStrategy {
    @Override
    public void register(MapperFactory factory) {

        factory.classMap(ArbeidsplassenCVDTO.class, ArbeidsplassenCVDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(ArbeidsplassenCVDTO kilde,
                                        ArbeidsplassenCVDTO destiasjon, MappingContext context) {

                        prepCommonFacts(destiasjon);
                    }
                })
                .byDefault()
                .register();
    }

    private static void prepCommonFacts(ArbeidsplassenCVDTO arbeidsplassenCV) {

        var artifacter = Arrays.stream(ArbeidsplassenCVDTO.class.getMethods())
                .filter(method -> method.getName().contains("get"))
                .filter(method -> method.getReturnType().getName().contains("List"))
                .map(method -> {
                    try {
                        return method.invoke(arbeidsplassenCV, null);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new DollyFunctionalException(String.format("Henting av data feilet: %s", e.getMessage()), e);
                    }
                })
                .map(List.class::cast)
                .flatMap(Collection::stream)
                .toList();

        artifacter.forEach(artifact -> Arrays.stream(artifact.getClass().getMethods())
                .filter(method -> method.getName().contains("set"))
                .forEach(method -> {
                    if ("setUuid".equals(method.getName())) {
                        invoke(artifact, method, UUID.randomUUID().toString());
                    } else if ("setUpdatedAt".equals(method.getName())) {
                        invoke(artifact, method, LocalDateTime.now());
                    }
                }));
    }

    @SneakyThrows
    private static Object invoke(Object artifact, Method method, Object value) {

        return method.invoke(artifact, value);
    }
}
