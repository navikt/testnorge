package no.nav.testnav.apps.tpsmessagingservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.tpsmessagingservice.dto.PostadresseRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsSystemInfo;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PostadresseUtlandDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static java.util.Objects.isNull;

@Component
public class PostadresseUtlandMappingStrategy implements MappingStrategy {

    private static final String ADRESSE_UTLAND = "UTAD";

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(PostadresseUtlandDTO.class, PostadresseRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PostadresseUtlandDTO source, PostadresseRequest target, MappingContext context) {

                        target.setSfeAjourforing(PostadresseRequest.SfeAjourforing.builder()
                                .systemInfo(TpsSystemInfo.getDefault())
                                .endreAdresseLinjer(mapperFacade.map(source, PostadresseRequest.PostAdresse.class, context))
                                .build());
                    }
                })
                .register();

        factory.classMap(PostadresseUtlandDTO.class, PostadresseRequest.PostAdresse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PostadresseUtlandDTO source, PostadresseRequest.PostAdresse target, MappingContext context) {

                        target.setOffentligIdent((String) context.getProperty("ident"));
                        target.setTypeAdresse(ADRESSE_UTLAND);
                        target.setDatoAdresse((isNull(source.getDatoAdresse()) ? LocalDate.now() :
                                source.getDatoAdresse().toLocalDate()).toString());
                    }
                })
                .byDefault()
                .register();
    }
}