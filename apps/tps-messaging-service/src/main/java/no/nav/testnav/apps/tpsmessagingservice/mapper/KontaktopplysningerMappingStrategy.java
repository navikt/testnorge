package no.nav.testnav.apps.tpsmessagingservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.tpsmessagingservice.dto.KontaktopplysningerRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.SfeSpraak;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsServiceRoutineEndring;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsSystemInfo;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.KontaktopplysningerRequestDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class KontaktopplysningerMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(KontaktopplysningerRequestDTO.class, KontaktopplysningerRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(KontaktopplysningerRequestDTO source, KontaktopplysningerRequest target, MappingContext context) {

                        var sfeAjourforing = new KontaktopplysningerRequest.SfeAjourforing();

                        sfeAjourforing.setSystemInfo(TpsSystemInfo.builder()
                                .kilde("Dolly")
                                .brukerID("anonymousUser")
                                .build());
                        sfeAjourforing.setEndreKontaktopplysninger(TpsServiceRoutineEndring.builder()
                                .offentligIdent((String) context.getProperty("ident"))
                                .build());

                        sfeAjourforing.setEndringAvSprak(mapperFacade.map(source.getEndringAvSprak(), SfeSpraak.class, context));
                        sfeAjourforing.setEndringAvKontonr(mapperFacade.map(source.getEndringAvKontonr(), KontaktopplysningerRequestDTO.Kontonummer.class));

                        target.setSfeAjourforing(sfeAjourforing);
                    }
                })
                .register();

        factory.classMap(KontaktopplysningerRequestDTO.Spraak.class, SfeSpraak.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(KontaktopplysningerRequestDTO.Spraak source, SfeSpraak target, MappingContext context) {

                        mapperFacade.map(source, target, context);
                    }
                })
                .byDefault()
                .register();

        factory.classMap(KontaktopplysningerRequestDTO.NyttSprak.class, SfeSpraak.NyttSprak.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(KontaktopplysningerRequestDTO.NyttSprak source, SfeSpraak.NyttSprak target, MappingContext context) {

                        target.setDatoSprak(((LocalDate) context.getProperty("datoSprak")).toString());
                    }
                })
                .byDefault()
                .exclude("datoSprak")
                .register();
    }
}
