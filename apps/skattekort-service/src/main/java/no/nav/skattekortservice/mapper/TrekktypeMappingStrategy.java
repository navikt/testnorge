package no.nav.skattekortservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortTilArbeidsgiverDTO;
import no.skatteetaten.fastsetting.formueinntekt.forskudd.skattekorttilarbeidsgiver.v3.Forskuddstrekk;
import no.skatteetaten.fastsetting.formueinntekt.forskudd.skattekorttilarbeidsgiver.v3.Frikort;
import no.skatteetaten.fastsetting.formueinntekt.forskudd.skattekorttilarbeidsgiver.v3.Skattekort;
import no.skatteetaten.fastsetting.formueinntekt.forskudd.skattekorttilarbeidsgiver.v3.Trekkprosent;
import no.skatteetaten.fastsetting.formueinntekt.forskudd.skattekorttilarbeidsgiver.v3.Trekktabell;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static java.util.Objects.nonNull;

@Component
public class TrekktypeMappingStrategy implements MappingStrategy {
    @Override
    public void register(MapperFactory factory) {

        factory.classMap(SkattekortTilArbeidsgiverDTO.Skattekort.class, Skattekort.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SkattekortTilArbeidsgiverDTO.Skattekort skattekort, Skattekort skattekort2, MappingContext context) {

                        skattekort2.getForskuddstrekk().addAll(skattekort.getTrekktype().stream()
                                .map(trekktype -> {
                                    if (nonNull(trekktype.getFrikort())) {
                                        return mapperFacade.map(trekktype.getFrikort(), Frikort.class);
                                    } else if (nonNull(trekktype.getTrekktabell())) {
                                        return mapperFacade.map(trekktype.getTrekktabell(), Trekktabell.class);
                                    } else if (nonNull(trekktype.getTrekkprosent())) {
                                        return mapperFacade.map(trekktype.getTrekkprosent(), Trekkprosent.class);
                                    } else if (nonNull(trekktype.getForskuddstrekk())) {
                                        return mapperFacade.map(trekktype.getForskuddstrekk(), Forskuddstrekk.class);
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
    }
}