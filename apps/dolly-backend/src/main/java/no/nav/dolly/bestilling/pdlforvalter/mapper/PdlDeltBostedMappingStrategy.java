package no.nav.dolly.bestilling.pdlforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlDeltBosted;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlDeltBosted.PdlDelteBosteder;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlMatrikkeladresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpplysning.Master;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlVegadresse;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.adresse.BoAdresse;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Component
public class PdlDeltBostedMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Person.class, PdlDelteBosteder.class)

                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Person person, PdlDelteBosteder deltBosted, MappingContext context) {

                        List<BoAdresse> boadresser = new ArrayList<>(person.getBoadresse());
                        Collections.reverse(boadresser);

                        deltBosted.getDelteBosteder()
                                .addAll(
                                        Stream.of(
                                                boadresser.stream()
                                                        .filter(boAdresse -> isTrue(boAdresse.getDeltAdresse()) &&
                                                                boAdresse.isGateadresse() && !person.isKode6())
                                                        .map(boAdresse -> PdlDeltBosted.builder()
                                                                .vegadresse(mapperFacade.map(boAdresse, PdlVegadresse.class))
                                                                .kilde(CONSUMER)
                                                                .master(Master.FREG)
                                                                .startdatoForKontrakt(LocalDate.now())
                                                                .build())
                                                        .collect(Collectors.toList()),

                                                boadresser.stream()
                                                        .filter(boAdresse -> isTrue(boAdresse.getDeltAdresse()) &&
                                                                boAdresse.isMatrikkeladresse() && !person.isKode6())
                                                        .map(boAdresse -> PdlDeltBosted.builder()
                                                                .matrikkeladresse(mapperFacade.map(boAdresse, PdlMatrikkeladresse.class))
                                                                .kilde(CONSUMER)
                                                                .master(Master.FREG)
                                                                .startdatoForKontrakt(LocalDate.now())
                                                                .build())
                                                        .collect(Collectors.toList())
                                        )
                                                .flatMap(Collection::stream)
                                                .collect(Collectors.toList()));
                    }
                })
                .register();
    }
}
