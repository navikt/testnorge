package no.nav.dolly.bestilling.skattekort.mapper;

import lombok.val;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.skattekort.domain.Forskuddstrekk;
import no.nav.dolly.bestilling.skattekort.domain.Resultatstatus;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortDTO;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortDTO.ForskuddstrekkDTO;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortDTO.FrikortDTO;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortDTO.ProsentkortDTO;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortRequest;
import no.nav.dolly.bestilling.skattekort.domain.Skattekortmelding;
import no.nav.dolly.bestilling.skattekort.domain.Tilleggsopplysning;
import no.nav.dolly.bestilling.skattekort.domain.Trekkode;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class SkattekortMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        // Mapper for release av skattekort - setter alltid resultat til IKKE_SKATTEKORT
        factory.classMap(SkattekortDTO.class, SkattekortRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SkattekortDTO skattekortDTO, SkattekortRequest skattekortRequest, MappingContext context) {
                        val ident = (String) context.getProperty("ident");
                        skattekortRequest.setFnr(ident);

                        val sokosSkattekort = mapperFacade.map(skattekortDTO, SkattekortDTO.class);
                        sokosSkattekort.setResultatForSkattekort(Resultatstatus.IKKE_SKATTEKORT.getValue());
                        skattekortRequest.setSkattekort(sokosSkattekort);
                    }
                })
                .register();

        factory.classMap(Skattekortmelding.class, SkattekortRequest.class)
                .mapNulls(false)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Skattekortmelding source, SkattekortRequest destination, MappingContext context) {
                        val ident = (String) context.getProperty("ident");
                        destination.setFnr(ident);

                        val sokosSkattekort = mapperFacade.map(source, SkattekortDTO.class, context);
                        destination.setSkattekort(sokosSkattekort);
                    }
                })
                .register();

        factory.classMap(Skattekortmelding.class, SkattekortDTO.class)
                .field("skattekort.utstedtDato", "utstedtDato")
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Skattekortmelding source, SkattekortDTO destination, MappingContext context) {

                        if (nonNull(source.getResultatPaaForespoersel())) {
                            destination.setResultatForSkattekort(source.getResultatPaaForespoersel().getValue());
                        }

                        val tilleggsopplysningValues = source.getTilleggsopplysning().stream()
                                .filter(java.util.Objects::nonNull)
                                .map(Tilleggsopplysning::getValue)
                                .toList();
                        destination.setTilleggsopplysningList(tilleggsopplysningValues);

                        if (nonNull(source.getSkattekort())) {
                            val flattened = source.getSkattekort().getForskuddstrekk().stream()
                                    .map(SkattekortMappingStrategy::mapForskuddstrekk)
                                    .filter(forskuddstrekk -> nonNull(forskuddstrekk) &&
                                            nonNull(forskuddstrekk.getTrekkode()))
                                    .toList();
                            destination.setForskuddstrekkList(flattened);
                        }
                    }
                })
                .byDefault()
                .register();
    }

    private static ForskuddstrekkDTO mapForskuddstrekk(Forskuddstrekk original) {

        if (isNull(original)) {
            return null;
        }

        val builder = ForskuddstrekkDTO.builder();

        if (nonNull(original.getTrekktabell())) {

            val trekktabell = original.getTrekktabell();
            builder.trekkode(getTrekkode(trekktabell.getTrekkode()))
                    .trekktabell(isNotBlank(trekktabell.getTabellnummer()) &&
                            nonNull(trekktabell.getProsentsats()) &&
                            nonNull(trekktabell.getAntallMaanederForTrekk()) ?
                            SkattekortDTO.TabellkortDTO.builder()
                                    .tabell(original.getTrekktabell().getTabellnummer())
                                    .antallMndForTrekk(getDouble(original.getTrekktabell().getAntallMaanederForTrekk()))
                                    .prosentSats(getDouble(original.getTrekktabell().getProsentsats()))
                                    .build() : null);
        }

        if (nonNull(original.getFrikort())) {

            builder.trekkode(getTrekkode(original.getFrikort().getTrekkode()))
                    .frikort(FrikortDTO.builder()
                            .frikortBeloep(nonNull(original.getFrikort().getFrikortbeloep()) ?
                                    original.getFrikort().getFrikortbeloep().toString() : null)
                            .build());
        }

        if (nonNull(original.getTrekkprosent())) {

            builder.trekkode(getTrekkode(original.getTrekkprosent().getTrekkode()))
                    .prosentkort(ProsentkortDTO.builder()
                            .prosentSats(getDouble(original.getTrekkprosent().getProsentsats()))
                            .antallMndForTrekk(getDouble(original.getTrekkprosent().getAntallMaanederForTrekk()))
                            .build())
                    .build();
        }

        return builder.build();
    }

    private static String getTrekkode(Trekkode trekkode) {
        return nonNull(trekkode) ? trekkode.getValue() : null;
    }

    private static Double getDouble(Integer value) {
        return nonNull(value) ? value.doubleValue() : null;
    }
}

