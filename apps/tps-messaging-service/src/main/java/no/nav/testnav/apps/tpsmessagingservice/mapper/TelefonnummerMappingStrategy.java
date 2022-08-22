package no.nav.testnav.apps.tpsmessagingservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.tpsmessagingservice.dto.KontaktopplysningerRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.TelefonnummerRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsSystemInfo;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TelefonTypeNummerDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TelefonTypeNummerDTO.TypeTelefon;
import no.nav.tps.ctg.s610.domain.TelefonType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TelefonnummerMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(TelefonTypeNummerDTO.class, KontaktopplysningerRequest.TelefonData.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(TelefonTypeNummerDTO source, KontaktopplysningerRequest.TelefonData target, MappingContext context) {

                        target.setTypeTelefon(source.getTelefontype());
                        target.setTelefonNr(source.getTelefonnummer());
                        target.setTelefonLandkode(source.getLandkode());
                        target.setDatoTelefon(LocalDate.now().toString());
                    }
                })
                .register();

        factory.classMap(TypeTelefon.class, TelefonType.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(TypeTelefon source, TelefonType target, MappingContext context) {

                        target.setTlfType(source.toString());
                    }
                })
                .register();

        factory.classMap(TelefonTypeNummerDTO.class, TelefonnummerRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(TelefonTypeNummerDTO source, TelefonnummerRequest target, MappingContext context) {

                        target.setSfeAjourforing(TelefonnummerRequest.SfeAjourforing.builder()
                                .systemInfo(TpsSystemInfo.getDefault())
                                .endreTelefon(mapperFacade.map(source, TelefonnummerRequest.TelefonOpplysninger.class, context))
                                .opphorTelefon(mapperFacade.map(source, TelefonnummerRequest.BrukertypeIdentifikasjon.class, context))
                                .build());
                    }
                })
                .register();

        factory.classMap(TelefonTypeNummerDTO.class, TelefonnummerRequest.TelefonOpplysninger.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(TelefonTypeNummerDTO source, TelefonnummerRequest.TelefonOpplysninger target, MappingContext context) {

                        target.setTelefonNr(source.getTelefonnummer());
                        target.setTelefonLandkode(source.getLandkode());
                        target.setDatoTelefon(LocalDate.now().toString());
                    }
                })
                .register();

        factory.classMap(TelefonTypeNummerDTO.class, TelefonnummerRequest.BrukertypeIdentifikasjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(TelefonTypeNummerDTO source, TelefonnummerRequest.BrukertypeIdentifikasjon target, MappingContext context) {

                        target.setOffentligIdent((String) context.getProperty("ident"));
                        target.setTypeTelefon(source.getTelefontype());
                    }
                })
                .register();

        factory.classMap(TelefonType.class, TelefonTypeNummerDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(TelefonType source, TelefonTypeNummerDTO target, MappingContext context) {

                        target.setTelefonnummer(source.getTlfNummer());
                        target.setLandkode(source.getTlfLandkode());
                        target.setTelefontype(TypeTelefon.valueOf(source.getTlfType()));
                    }
                })
                .register();
    }
}
