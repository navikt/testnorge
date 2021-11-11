package no.nav.testnav.apps.tpsmessagingservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.tpsmessagingservice.dto.EndringsmeldingRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.KontaktopplysningerRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.OpprettEgenansattRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsServiceRoutineEndring;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsSystemInfo;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.KontaktopplysningerRequestDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class EgenansattMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(EndringsmeldingRequest.class, OpprettEgenansattRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(EndringsmeldingRequest source, OpprettEgenansattRequest target, MappingContext context) {

                        var sfeAjourforing = new OpprettEgenansattRequest.SfeAjourforing();

                        sfeAjourforing.setSystemInfo(TpsSystemInfo.builder()
                                .kilde("Dolly")
                                .brukerID("anonymousUser")
                                .build());
                        sfeAjourforing.setEndreEgenAnsatt(TpsServiceRoutineEndring.builder()
                                .offentligIdent((String) context.getProperty("ident"))
                                        .fom((LocalDate) context.getProperty("fraOgMed"))
                                .build());

                        target.setSfeAjourforing(sfeAjourforing);
                    }
                })
                .register();
    }
}
