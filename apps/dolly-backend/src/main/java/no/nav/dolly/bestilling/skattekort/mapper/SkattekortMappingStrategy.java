package no.nav.dolly.bestilling.skattekort.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import no.nav.dolly.bestilling.skattekort.domain.ForskuddstrekkDTO;
import no.nav.dolly.bestilling.skattekort.domain.OpprettSkattekortRequest;
import no.nav.dolly.bestilling.skattekort.domain.ResultatForSkattekort;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortDTO;
import no.nav.dolly.bestilling.skattekort.domain.TilleggsopplysningType;
import no.nav.dolly.bestilling.skattekort.domain.TrekkodeType;
import no.nav.dolly.domain.resultset.skattekort.SkattekortRequestDTO;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.skattekortservice.v1.Forskuddstrekk;
import no.nav.testnav.libs.dto.skattekortservice.v1.Frikort;
import no.nav.testnav.libs.dto.skattekortservice.v1.Resultatstatus;
import no.nav.testnav.libs.dto.skattekortservice.v1.Skattekortmelding;
import no.nav.testnav.libs.dto.skattekortservice.v1.Tilleggsopplysning;
import no.nav.testnav.libs.dto.skattekortservice.v1.Trekkode;
import no.nav.testnav.libs.dto.skattekortservice.v1.Trekkprosent;
import no.nav.testnav.libs.dto.skattekortservice.v1.Trekktabell;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
public class SkattekortMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        registerConverters(factory);

        factory.classMap(SkattekortRequestDTO.class, no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortRequestDTO.class)
                .mapNulls(false)
                .field("arbeidsgiverSkatt", "arbeidsgiver")
                .register();

        factory.classMap(Skattekortmelding.class, OpprettSkattekortRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Skattekortmelding source, OpprettSkattekortRequest target, MappingContext context) {
                        target.setFnr(source.getArbeidstakeridentifikator());
                        target.setSkattekort(mapperFacade.map(source, SkattekortDTO.class));
                    }
                })
                .register();

        factory.classMap(Skattekortmelding.class, SkattekortDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Skattekortmelding source, SkattekortDTO target, MappingContext context) {
                        target.setInntektsaar(source.getInntektsaar());
                        target.setResultatForSkattekort(mapperFacade.map(source.getResultatPaaForespoersel(), ResultatForSkattekort.class));
                        target.setTilleggsopplysningList(mapperFacade.mapAsList(source.getTilleggsopplysning(), TilleggsopplysningType.class));
                        if (nonNull(source.getSkattekort())) {
                            target.setUtstedtDato(source.getSkattekort().getUtstedtDato());
                            target.setForskuddstrekkList(mapperFacade.mapAsList(source.getSkattekort().getForskuddstrekk(), ForskuddstrekkDTO.class));
                        }
                    }
                })
                .register();

        factory.classMap(Forskuddstrekk.class, ForskuddstrekkDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Forskuddstrekk source, ForskuddstrekkDTO target, MappingContext context) {
                        if (nonNull(source.getFrikort())) {
                            mapperFacade.map(source.getFrikort(), target);
                        } else if (nonNull(source.getTrekktabell())) {
                            mapperFacade.map(source.getTrekktabell(), target);
                        } else if (nonNull(source.getTrekkprosent())) {
                            mapperFacade.map(source.getTrekkprosent(), target);
                        }
                    }
                })
                .register();

        factory.classMap(Frikort.class, ForskuddstrekkDTO.class)
                .field("frikortbeloep", "frikortBeloep")
                .byDefault()
                .register();

        factory.classMap(Trekktabell.class, ForskuddstrekkDTO.class)
                .field("tabellnummer", "tabell")
                .field("antallMaanederForTrekk", "antallMndForTrekk")
                .field("prosentsats", "prosentSats")
                .byDefault()
                .register();

        factory.classMap(Trekkprosent.class, ForskuddstrekkDTO.class)
                .field("antallMaanederForTrekk", "antallMndForTrekk")
                .field("prosentsats", "prosentSats")
                .byDefault()
                .register();
    }

    private void registerConverters(MapperFactory factory) {
        factory.getConverterFactory().registerConverter(new BidirectionalConverter<Tilleggsopplysning, TilleggsopplysningType>() {
            @Override
            public TilleggsopplysningType convertTo(Tilleggsopplysning source, Type<TilleggsopplysningType> destinationType, MappingContext context) {
                return isNull(source) ? null : TilleggsopplysningType.valueOf(source.name());
            }

            @Override
            public Tilleggsopplysning convertFrom(TilleggsopplysningType source, Type<Tilleggsopplysning> destinationType, MappingContext context) {
                return isNull(source) ? null : Tilleggsopplysning.valueOf(source.name());
            }
        });

        factory.getConverterFactory().registerConverter(new BidirectionalConverter<Trekkode, TrekkodeType>() {
            @Override
            public TrekkodeType convertTo(Trekkode source, Type<TrekkodeType> destinationType, MappingContext context) {
                return isNull(source) ? null : TrekkodeType.valueOf(source.name());
            }

            @Override
            public Trekkode convertFrom(TrekkodeType source, Type<Trekkode> destinationType, MappingContext context) {
                return isNull(source) ? null : Trekkode.valueOf(source.name());
            }
        });

        factory.getConverterFactory().registerConverter(new BidirectionalConverter<Resultatstatus, ResultatForSkattekort>() {
            @Override
            public ResultatForSkattekort convertTo(Resultatstatus source, Type<ResultatForSkattekort> destinationType, MappingContext context) {
                return isNull(source) ? null : ResultatForSkattekort.valueOf(source.name());
            }

            @Override
            public Resultatstatus convertFrom(ResultatForSkattekort source, Type<Resultatstatus> destinationType, MappingContext context) {
                return isNull(source) ? null : Resultatstatus.valueOf(source.name());
            }
        });
    }
}
