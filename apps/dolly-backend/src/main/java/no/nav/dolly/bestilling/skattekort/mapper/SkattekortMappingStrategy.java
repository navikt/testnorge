package no.nav.dolly.bestilling.skattekort.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.skattekort.SkattekortRequestDTO;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.skattekortservice.v1.ArbeidsgiverSkatt;
import no.nav.testnav.libs.dto.skattekortservice.v1.Forskuddstrekk;
import no.nav.testnav.libs.dto.skattekortservice.v1.Skattekortmelding;
import no.nav.testnav.libs.dto.skattekortservice.v1.SokosSkattekortRequest;
import org.springframework.stereotype.Component;

@Component
public class SkattekortMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(SkattekortRequestDTO.class, SokosSkattekortRequest.class)
                .mapNulls(false)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SkattekortRequestDTO source, SokosSkattekortRequest destination, MappingContext context) {
                        var ident = (String) context.getProperty("ident");

                        if (source.getArbeidsgiverSkatt() != null && !source.getArbeidsgiverSkatt().isEmpty()) {
                            ArbeidsgiverSkatt arbeidsgiver = source.getArbeidsgiverSkatt().getFirst();

                            if (arbeidsgiver.getArbeidstaker() != null && !arbeidsgiver.getArbeidstaker().isEmpty()) {
                                Skattekortmelding arbeidstaker = arbeidsgiver.getArbeidstaker().getFirst();

                                destination.setFnr(ident);

                                var sokosSkattekort = mapperFacade.map(arbeidstaker, SokosSkattekortRequest.SokosSkattekortDTO.class);
                                destination.setSkattekort(sokosSkattekort);
                            }
                        }
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


