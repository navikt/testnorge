package no.nav.skattekortservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.skattekortservice.dto.SkattekortResponsIntermediate;
import no.nav.testnav.libs.dto.skattekortservice.v1.Forskuddstrekk;
import no.nav.testnav.libs.dto.skattekortservice.v1.Frikort;
import no.nav.testnav.libs.dto.skattekortservice.v1.Skattekort;
import no.nav.testnav.libs.dto.skattekortservice.v1.Trekkprosent;
import no.nav.testnav.libs.dto.skattekortservice.v1.Trekktabell;
import no.nav.testnav.libs.dto.skattekortservice.v1.Trekktype;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static java.util.Objects.nonNull;

@Component
public class TrekktypeMappingStrategy implements MappingStrategy {
    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Skattekort.class, no.skatteetaten.fastsetting.formueinntekt.forskudd.skattekorttilarbeidsgiver.v3.Skattekort.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Skattekort skattekort, no.skatteetaten.fastsetting.formueinntekt.forskudd.skattekorttilarbeidsgiver.v3.Skattekort skattekort2, MappingContext context) {

                        skattekort2.getForskuddstrekk().addAll(skattekort.getTrekktype().stream()
                                .map(trekktype -> {
                                    if (nonNull(trekktype.getFrikort())) {
                                        return mapperFacade.map(trekktype.getFrikort(),
                                                no.skatteetaten.fastsetting.formueinntekt.forskudd.skattekorttilarbeidsgiver.v3.Frikort.class);
                                    } else if (nonNull(trekktype.getTrekktabell())) {
                                        return mapperFacade.map(trekktype.getTrekktabell(),
                                                no.skatteetaten.fastsetting.formueinntekt.forskudd.skattekorttilarbeidsgiver.v3.Trekktabell.class);
                                    } else if (nonNull(trekktype.getTrekkprosent())) {
                                        return mapperFacade.map(trekktype.getTrekkprosent(),
                                                no.skatteetaten.fastsetting.formueinntekt.forskudd.skattekorttilarbeidsgiver.v3.Trekkprosent.class);
                                    } else if (nonNull(trekktype.getForskuddstrekk())) {
                                        return mapperFacade.map(trekktype.getForskuddstrekk(),
                                                no.skatteetaten.fastsetting.formueinntekt.forskudd.skattekorttilarbeidsgiver.v3.Forskuddstrekk.class);
                                    } else {
                                        return null;
                                    }
                                })
                                .filter(Objects::nonNull)
                                .toList());
                    }
                })
                .exclude("trekktype")
                .byDefault()
                .register();

        factory.classMap(SkattekortResponsIntermediate.Skattekort.class, Skattekort.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SkattekortResponsIntermediate.Skattekort skattekort, Skattekort skattekort2, MappingContext context) {

                        skattekort2.getTrekktype().addAll(skattekort.getFrikort().stream()
                                .map(frikort -> Trekktype.builder()
                                        .frikort(mapperFacade.map(frikort, Frikort.class))
                                        .build())
                                .toList());
                        skattekort2.getTrekktype().addAll(skattekort.getForskuddstrekk().stream()
                                .map(forskuddstrekk -> Trekktype.builder()
                                        .forskuddstrekk(mapperFacade.map(forskuddstrekk, Forskuddstrekk.class))
                                        .build())
                                .toList());
                        skattekort2.getTrekktype().addAll(skattekort.getTrekkprosent().stream()
                                .map(trekkprosent -> Trekktype.builder()
                                        .trekkprosent(mapperFacade.map(trekkprosent, Trekkprosent.class))
                                        .build())
                                .toList());
                        skattekort2.getTrekktype().addAll(skattekort.getTrekktabell().stream()
                                .map(trekktabell -> Trekktype.builder()
                                        .trekktabell(mapperFacade.map(trekktabell, Trekktabell.class))
                                        .build())
                                .toList());
                    }
                })
                .byDefault()
                .register();
    }
}