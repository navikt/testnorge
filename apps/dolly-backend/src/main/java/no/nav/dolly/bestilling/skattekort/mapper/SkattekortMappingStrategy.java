package no.nav.dolly.bestilling.skattekort.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.skattekort.domain.Forskuddstrekk;
import no.nav.dolly.bestilling.skattekort.domain.Skattekortmelding;
import no.nav.dolly.bestilling.skattekort.domain.SokosSkattekortRequest;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Component
public class SkattekortMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Skattekortmelding.class, SokosSkattekortRequest.class)
                .mapNulls(false)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Skattekortmelding source, SokosSkattekortRequest destination, MappingContext context) {
                        var ident = (String) context.getProperty("ident");
                        destination.setFnr(ident);

                        var sokosSkattekort = mapperFacade.map(source, SokosSkattekortRequest.SokosSkattekortDTO.class);
                        destination.setSkattekort(sokosSkattekort);
                    }
                })
                .byDefault()
                .register();

        factory.classMap(Skattekortmelding.class, SokosSkattekortRequest.SokosSkattekortDTO.class)
                .field("skattekort.utstedtDato", "utstedtDato")
                .field("inntektsaar", "inntektsaar")
                .field("tilleggsopplysning", "tilleggsopplysningList")
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Skattekortmelding source, SokosSkattekortRequest.SokosSkattekortDTO destination, MappingContext context) {
                        if (source.getResultatPaaForespoersel() != null) {
                            destination.setResultatForSkattekort(source.getResultatPaaForespoersel().getValue());
                        }

                        if (source.getSkattekort() != null && source.getSkattekort().getForskuddstrekk() != null) {
                            var flattened = source.getSkattekort().getForskuddstrekk().stream()
                                    .map(SkattekortMappingStrategy::flattenForskuddstrekk)
                                    .toList();
                            destination.setForskuddstrekkList(flattened);
                        }
                    }
                })
                .byDefault()
                .register();
    }

    private static SokosSkattekortRequest.SokosForskuddstrekkDTO flattenForskuddstrekk(Forskuddstrekk original) {
        if (original == null) {
            return null;
        }

        SokosSkattekortRequest.SokosForskuddstrekkDTO.SokosForskuddstrekkDTOBuilder builder =
                SokosSkattekortRequest.SokosForskuddstrekkDTO.builder();

        if (original.getTrekktabell() != null) {
            builder.trekkode(original.getTrekktabell().getTrekkode())
                    .tabell(original.getTrekktabell().getTabellnummer())
                    .prosentSats(original.getTrekktabell().getProsentsats() != null
                            ? original.getTrekktabell().getProsentsats().doubleValue() : null)
                    .antallMndForTrekk(original.getTrekktabell().getAntallMaanederForTrekk() != null
                            ? original.getTrekktabell().getAntallMaanederForTrekk().doubleValue() : null);
        }

        if (original.getFrikort() != null) {
            builder.frikortBeloep(original.getFrikort().getFrikortbeloep());
        }

        if (original.getTrekkprosent() != null) {
            builder.prosentSats(original.getTrekkprosent().getProsentsats() != null
                    ? original.getTrekkprosent().getProsentsats().doubleValue() : null);
        }

        return builder.build();
    }
}

