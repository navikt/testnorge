package no.nav.testnav.apps.tpsmessagingservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.tpsmessagingservice.dto.EgenansattRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.EndringsmeldingRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsEndringsopplysninger;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsSystemInfo;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static java.util.Objects.isNull;

@Component
public class EgenansattMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(EndringsmeldingRequest.class, EgenansattRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(EndringsmeldingRequest source, EgenansattRequest target, MappingContext context) {

                        var sfeAjourforing = new EgenansattRequest.SfeAjourforing();

                        sfeAjourforing.setSystemInfo(TpsSystemInfo.builder()
                                .kilde("Dolly")
                                .brukerID("anonymousUser")
                                .build());
                        sfeAjourforing.setEndreEgenAnsatt(TpsEndringsopplysninger.builder()
                                .offentligIdent((String) context.getProperty("ident"))
                                .fom((isNull(context.getProperty("fraOgMed")) ? LocalDate.now() :
                                        (LocalDate) context.getProperty("fraOgMed")).toString())
                                .build());
                        sfeAjourforing.setOpphorEgenAnsatt(TpsEndringsopplysninger.builder()
                                .offentligIdent((String) context.getProperty("ident"))
                                .build());

                        target.setSfeAjourforing(sfeAjourforing);
                    }
                })
                .register();
    }
}
