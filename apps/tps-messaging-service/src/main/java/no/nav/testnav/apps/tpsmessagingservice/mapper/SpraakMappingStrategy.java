package no.nav.testnav.apps.tpsmessagingservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.tpsmessagingservice.dto.SpraakRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsSystemInfo;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.SpraakDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@Component
public class SpraakMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(SpraakDTO.class, SpraakRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SpraakDTO source, SpraakRequest target, MappingContext context) {

                        target.setSfeAjourforing(SpraakRequest.SfeAjourforing.builder()
                                .systemInfo(TpsSystemInfo.getDefault())
                                .endreSprak(SpraakRequest.EndreSpraak.builder()
                                        .offentligIdent((String) context.getProperty("ident"))
                                        .sprakKode(source.getSprakKode())
                                        .datoSprak((nonNull(source.getDatoSprak()) ? source.getDatoSprak() :
                                                LocalDateTime.now())
                                                .toLocalDate().toString())
                                        .build())
                                .build());
                    }
                })
                .register();
    }
}
