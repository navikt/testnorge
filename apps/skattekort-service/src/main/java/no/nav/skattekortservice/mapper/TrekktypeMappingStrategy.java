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

                        skattekort2.getForskuddstrekk().addAll(skattekort.getForskuddstrekk().stream()
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
                                    } else {
                                        return null;
                                    }
                                })
                                .filter(Objects::nonNull)
                                .toList());
                    }
                })
                .exclude("forskuddstrekk")
                .byDefault()
                .register();

        factory.classMap(SkattekortResponsIntermediate.Skattekort.class, Skattekort.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SkattekortResponsIntermediate.Skattekort skattekort, Skattekort skattekort2, MappingContext context) {

                        skattekort2.setSkattekortidentifikator(skattekort.getSkattekortidentifikator());
                        skattekort2.setUtstedtDato(skattekort.getUtstedtDato());
                        skattekort.getForskuddstrekk()
                                .forEach(forskuddstrekk -> skattekort2.getForskuddstrekk().add(
                                        Forskuddstrekk.builder()
                                                .frikort(forskuddstrekk instanceof SkattekortResponsIntermediate.Frikort ?
                                                        mapperFacade.map(forskuddstrekk, Frikort.class) : null)
                                                .trekktabell(forskuddstrekk instanceof SkattekortResponsIntermediate.Trekktabell ?
                                                        mapperFacade.map(forskuddstrekk, Trekktabell.class) : null)
                                                .trekkprosent(forskuddstrekk instanceof SkattekortResponsIntermediate.Trekkprosent ?
                                                        mapperFacade.map(forskuddstrekk, Trekkprosent.class) : null)
                                                .build()
                                ));
                    }
                })
                .register();
    }
}