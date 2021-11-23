package no.nav.testnav.apps.tpsmessagingservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.tpsmessagingservice.dto.TelefonnummerRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsSystemInfo;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TelefonnummerDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TelefonnummerMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(TelefonnummerDTO.class, TelefonnummerRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(TelefonnummerDTO source, TelefonnummerRequest target, MappingContext context) {

                        target.setSfeAjourforing(TelefonnummerRequest.SfeAjourforing.builder()
                                .systemInfo(TpsSystemInfo.getDefault())
                                .endreTelefon(mapperFacade.map(source, TelefonnummerRequest.TelefonOpplysninger.class, context))
                                .opphorTelefon(mapperFacade.map(source, TelefonnummerRequest.BrukertypeIdentifikasjon.class, context))
                                .build());
                    }
                })
                .register();

        factory.classMap(TelefonnummerDTO.class, TelefonnummerRequest.TelefonOpplysninger.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(TelefonnummerDTO source, TelefonnummerRequest.TelefonOpplysninger target, MappingContext context) {

                        target.setTelefonNr(source.getTelefonnummer());
                        target.setTelefonLandkode(source.getLandkode());
                        target.setDatoTelefon(LocalDate.now().toString());
                    }
                })
                .register();

        factory.classMap(TelefonnummerDTO.class, TelefonnummerRequest.BrukertypeIdentifikasjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(TelefonnummerDTO source, TelefonnummerRequest.BrukertypeIdentifikasjon target, MappingContext context) {

                        target.setOffentligIdent((String) context.getProperty("ident"));
                        target.setTypeTelefon(source.getTelefontype());
                    }
                })
                .register();
    }
}
