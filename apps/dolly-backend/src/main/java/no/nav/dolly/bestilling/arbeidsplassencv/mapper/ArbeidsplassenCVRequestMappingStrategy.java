package no.nav.dolly.bestilling.arbeidsplassencv.mapper;

import lombok.SneakyThrows;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.arbeidsplassencv.dto.PAMCVDTO;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.data.arbeidsplassencv.v1.ArbeidsplassenCVDTO;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.util.DateZoneUtil.CET;

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

        factory.classMap(ArbeidsplassenCVDTO.class, PAMCVDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(ArbeidsplassenCVDTO kilde,
                                        PAMCVDTO destiasjon, MappingContext context) {

                        destiasjon.getUtdanning().stream()
                                .filter(utdanning -> isNull(utdanning.getHasAuthorization()))
                                .forEach(utdanning -> utdanning.setHasAuthorization(false));

                        if (isNull(destiasjon.getSistEndretAvNav())) {
                            destiasjon.setSistEndretAvNav(true);
                        }
                        destiasjon.setSistEndret(ZonedDateTime.now(CET));

                        if (nonNull(destiasjon.getJobboensker()) && isNull(destiasjon.getJobboensker().getActive())) {
                            destiasjon.getJobboensker().setActive(true);
                        }
                    }
                })
                .byDefault()
                .register();
    }

    private static void prepCommonFacts(ArbeidsplassenCVDTO arbeidsplassenCV) {
        var artifacter = Arrays
                .stream(ReflectionUtils.getUniqueDeclaredMethods(
                        arbeidsplassenCV.getClass(),
                        method -> method.getName().contains("get") && method.getReturnType().getName().contains("List"))
                )
                .map(method -> {
                    try {
                        return method.invoke(arbeidsplassenCV);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new DollyFunctionalException(String.format("Henting av data feilet: %s", e.getMessage()), e);
                    }
                })
                .map(list -> (List<?>) list)
                .flatMap(Collection::stream)
                .toList();
        artifacter.forEach(artifact -> Arrays.stream(artifact.getClass().getMethods())
                .filter(method -> method.getName().contains("set"))
                .forEach(method -> {
                    if ("setUuid".equals(method.getName())) {
                        invoke(artifact, method, UUID.randomUUID().toString());
                    } else if ("setUpdatedAt".equals(method.getName())) {
                        invoke(artifact, method, LocalDateTime.now(CET));
                    }
                }));
    }

    @SneakyThrows
    private static void invoke(Object artifact, Method method, Object value) {
        method.invoke(artifact, value);
    }

}
