package no.nav.dolly.bestilling.instdata.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.instdata.domain.InstdataKdiDTO;
import no.nav.dolly.domain.resultset.inst.RsInstdataKdi;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class InstdataKdiMappingStrategy implements MappingStrategy {

    private static final String IDENT = "ident";
    private static final String INNMELDELSE_HENDELSE_ID = "innmeldelse_hendelse_id";

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsInstdataKdi.class, InstdataKdiDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInstdataKdi kilde, InstdataKdiDTO dest, MappingContext context) {

                        var miljoe = (String) context.getProperty("miljoe");
                        dest.setEnvironment(miljoe);
                        dest.setData(new InstdataKdiDTO.Data());

                        var innmeldingHendelseId = kilde.getInnsettelse().stream()
                                .map(RsInstdataKdi.Hendelse::getHendelseId)
                                .max(String::compareTo)
                                .orElse(null);
                        context.setProperty(INNMELDELSE_HENDELSE_ID, innmeldingHendelseId);

                        dest.getData().setInnsettelse(mapperFacade.mapAsList(kilde.getInnsettelse(), InstdataKdiDTO.Innsettelse.class, context));
                        dest.getData().setAvbruddStart(mapperFacade.mapAsList(kilde.getAvbruddStart(), InstdataKdiDTO.AvbruddStart.class, context));
                        dest.getData().setAvbruddSlutt(mapperFacade.mapAsList(kilde.getAvbruddSlutt(), InstdataKdiDTO.AvbruddSlutt.class, context));
                        dest.getData().setForventetLoeslatelse(mapperFacade.mapAsList(kilde.getForventetLoeslatelse(), InstdataKdiDTO.ForventetLoeslatelse.class, context));
                        dest.getData().setLoeslatelse(mapperFacade.mapAsList(kilde.getLoeslatelse(), InstdataKdiDTO.Loeslatelse.class, context));
                        dest.getData().setAnnullering(mapperFacade.mapAsList(kilde.getAnnullering(), InstdataKdiDTO.Annullering.class, context));
                    }
                })
                .register();

        factory.classMap(RsInstdataKdi.Innsettelse.class, InstdataKdiDTO.Innsettelse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInstdataKdi.Innsettelse kilde, InstdataKdiDTO.Innsettelse dest, MappingContext context) {

                        dest.setNorskident((String) context.getProperty(IDENT));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsInstdataKdi.AvbruddStart.class, InstdataKdiDTO.AvbruddStart.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInstdataKdi.AvbruddStart kilde, InstdataKdiDTO.AvbruddStart dest, MappingContext context) {

                        dest.setNorskident((String) context.getProperty(IDENT));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsInstdataKdi.AvbruddSlutt.class, InstdataKdiDTO.AvbruddSlutt.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInstdataKdi.AvbruddSlutt kilde, InstdataKdiDTO.AvbruddSlutt dest, MappingContext context) {

                        dest.setNorskident((String) context.getProperty(IDENT));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsInstdataKdi.ForventetLoeslatelse.class, InstdataKdiDTO.ForventetLoeslatelse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInstdataKdi.ForventetLoeslatelse kilde, InstdataKdiDTO.ForventetLoeslatelse dest, MappingContext context) {

                        dest.setNorskident((String) context.getProperty(IDENT));
                        if (isBlank(kilde.getInnmeldingHendelseId())) {
                            dest.setInnmeldingHendelseId((String) context.getProperty(INNMELDELSE_HENDELSE_ID));
                        }
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsInstdataKdi.Loeslatelse.class, InstdataKdiDTO.Loeslatelse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInstdataKdi.Loeslatelse kilde, InstdataKdiDTO.Loeslatelse dest, MappingContext context) {

                        dest.setNorskident((String) context.getProperty(IDENT));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsInstdataKdi.Annullering.class, InstdataKdiDTO.Annullering.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInstdataKdi.Annullering kilde, InstdataKdiDTO.Annullering dest, MappingContext context) {

                        dest.setNorskident((String) context.getProperty(IDENT));
                    }
                })
                .byDefault()
                .register();
    }
}