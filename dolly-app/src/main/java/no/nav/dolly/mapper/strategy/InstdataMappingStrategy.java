package no.nav.dolly.mapper.strategy;

import static java.util.Objects.nonNull;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class InstdataMappingStrategy implements MappingStrategy {

    @Override public void register(MapperFactory factory) {
        factory.classMap(RsInstdata.class, Instdata.class)
                .customize(new CustomMapper<RsInstdata, Instdata>() {
                    @Override public void mapAtoB(RsInstdata rsInstdata,
                            Instdata instdata, MappingContext context) {

                        if (nonNull(rsInstdata.getSluttdato())) {
                            instdata.setForventetSluttdato(rsInstdata.getSluttdato().toLocalDate());
                            instdata.setFaktiskSluttdato(rsInstdata.getSluttdato().toLocalDate());
                        }
                    }
                })
                .byDefault()
                .register();
    }
}
