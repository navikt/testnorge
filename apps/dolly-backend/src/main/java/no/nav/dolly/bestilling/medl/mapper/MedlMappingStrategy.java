package no.nav.dolly.bestilling.medl.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.medl.MedlData;
import no.nav.dolly.domain.resultset.medl.MedlDataResponse;
import no.nav.dolly.domain.resultset.medl.RsMedl;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
public class MedlMappingStrategy implements MappingStrategy {
    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsMedl.class, MedlData.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsMedl rsMedl, MedlData medlDataRequest, MappingContext context) {

                        medlDataRequest.setIdent((String) context.getProperty("ident"));
                        if (nonNull(rsMedl.getStudieinformasjon())) {
                            medlDataRequest.setStudieinformasjon(mapperFacade.map(rsMedl.getStudieinformasjon(), MedlData.Studieinformasjon.class));
                        }
                    }
                })
                .exclude("studieinformasjon")
                .byDefault()
                .register();

        factory.classMap(MedlDataResponse.class, MedlData.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(MedlDataResponse dataResponse, MedlData medlDataRequest, MappingContext context) {

                        medlDataRequest.setId(dataResponse.getUnntakId());
                        if (nonNull(dataResponse.getStudieinformasjon())) {
                            medlDataRequest.setStudieinformasjon(mapperFacade.map(dataResponse.getStudieinformasjon(), MedlData.Studieinformasjon.class));
                        }
                        if (nonNull(dataResponse.getSporingsinformasjon())) {
                            medlDataRequest.setKilde(dataResponse.getSporingsinformasjon().getKilde());
                            medlDataRequest.setVersjon(dataResponse.getSporingsinformasjon().getVersjon());
                            medlDataRequest.setKildedokument(dataResponse.getSporingsinformasjon().getKildedokument());
                        }
                    }
                })
                .exclude("studieinformasjon")
                .exclude("sporingsinformasjon")
                .exclude("kilde")
                .exclude("versjon")
                .exclude("kildedokument")
                .byDefault()
                .register();
    }
}