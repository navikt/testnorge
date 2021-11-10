package no.nav.testnav.apps.tpsmessagingservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.tpsmessagingservice.dto.KontaktopplysningerRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsServiceRoutineEndring;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsSystemInfo;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.KontaktopplysningerRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class KontaktopplysningerMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(KontaktopplysningerRequestDTO.class, KontaktopplysningerRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(KontaktopplysningerRequestDTO source, KontaktopplysningerRequest target, MappingContext context) {

                        target.setSystemInfo(TpsSystemInfo.builder()
                                .kilde("Dolly")
                                .brukerID("anonymousUser")
                                .build());
                        target.setEndreKontaktopplysninger(TpsServiceRoutineEndring.builder()
                                .serviceRutinenavn("KontaktopplysningEndringer")
                                .offentligIdent(source.getIdent())
                                .build());
                    }
                })
                .byDefault()
                .register();
    }
}
