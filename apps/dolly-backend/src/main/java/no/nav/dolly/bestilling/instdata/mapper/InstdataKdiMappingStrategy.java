package no.nav.dolly.bestilling.instdata.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.instdata.domain.InstdataKdiDTO;
import no.nav.dolly.bestilling.instdata.domain.InstdataKdiResponse;
import no.nav.dolly.domain.resultset.inst.RsInstdataKdi;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Component
public class InstdataKdiMappingStrategy implements MappingStrategy {

    private static final String IDENT = "ident";
    private static final String TIMESTAMP = "timestamp";

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsInstdataKdi.class, InstdataKdiDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInstdataKdi kilde, InstdataKdiDTO dest, MappingContext context) {

                        var instdataKdiResponse = (InstdataKdiResponse) context.getProperty("instKdiData");

                        var kdidata = instdataKdiResponse.getInstdataKdi().getData();
                        var maksId = Stream.of(List.of(kdidata.getInnsettelse(),
                                kdidata.getAvbruddStart(),
                                kdidata.getAvbruddSlutt(),
                                kdidata.getLoeslatelse(),
                                kdidata.getForventetLoeslatelse()))
                                .flatMap(Collection::stream)
                                .flatMap(Collection::stream)
                                .map(InstdataKdiDTO.Hendelse::getHendelseId)
                                .max(String::compareTo)
                                .orElse("0");

                        context.setProperty("maksId", maksId);
                        context.setProperty(IDENT, instdataKdiResponse.getIdent());
                        context.setProperty(TIMESTAMP, LocalDateTime.now());

                        dest.setEnvironment(instdataKdiResponse.getEnvironment());
                        dest.getData().setInnsettelse(mapperFacade.mapAsList(kilde.getData().getInnsettelse(), InstdataKdiDTO.Innsettelse.class, context));
                        dest.getData().setAvbruddStart(mapperFacade.mapAsList(kilde.getData().getAvbruddStart(), InstdataKdiDTO.AvbruddStart.class, context));
                        dest.getData().setAvbruddSlutt(mapperFacade.mapAsList(kilde.getData().getAvbruddSlutt(), InstdataKdiDTO.AvbruddSlutt.class, context));
                        dest.getData().setForventetLoeslatelse(mapperFacade.mapAsList(kilde.getData().getForventetLoeslatelse(), InstdataKdiDTO.ForventetLoeslatelse.class, context));
                        dest.getData().setLoeslatelse(mapperFacade.mapAsList(kilde.getData().getForventetLoeslatelse(), InstdataKdiDTO.Loeslatelse.class, context));
                        dest.getData().setAnnullering(mapperFacade.mapAsList(kilde.getData().getAnnullering(), InstdataKdiDTO.Annullering.class, context));
                    }
                })
                .register();

        factory.classMap(RsInstdataKdi.Innsettelse.class, InstdataKdiDTO.Innsettelse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInstdataKdi.Innsettelse kilde, InstdataKdiDTO.Innsettelse dest, MappingContext context) {

                        dest.setNorskident((String) context.getProperty(IDENT));
                        dest.setPubliseringstidspunkt((LocalDateTime) context.getProperty(TIMESTAMP));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsInstdataKdi.AvbruddStart.class, InstdataKdiDTO.AvbruddStart.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInstdataKdi.AvbruddStart kilde, InstdataKdiDTO.AvbruddStart dest, MappingContext context) {

                        dest.setNorskident((String) context.getProperty(IDENT));
                        dest.setPubliseringstidspunkt((LocalDateTime) context.getProperty(TIMESTAMP));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsInstdataKdi.AvbruddSlutt.class, InstdataKdiDTO.AvbruddSlutt.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInstdataKdi.AvbruddSlutt kilde, InstdataKdiDTO.AvbruddSlutt dest, MappingContext context) {

                        dest.setNorskident((String) context.getProperty(IDENT));
                        dest.setPubliseringstidspunkt((LocalDateTime) context.getProperty(TIMESTAMP));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsInstdataKdi.ForventetLoeslatelse.class, InstdataKdiDTO.ForventetLoeslatelse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInstdataKdi.ForventetLoeslatelse kilde, InstdataKdiDTO.ForventetLoeslatelse dest, MappingContext context) {

                        dest.setNorskident((String) context.getProperty(IDENT));
                        dest.setPubliseringstidspunkt((LocalDateTime) context.getProperty(TIMESTAMP));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsInstdataKdi.Loeslatelse.class, InstdataKdiDTO.Loeslatelse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInstdataKdi.Loeslatelse kilde, InstdataKdiDTO.Loeslatelse dest, MappingContext context) {

                        dest.setNorskident((String) context.getProperty(IDENT));
                        dest.setPubliseringstidspunkt((LocalDateTime) context.getProperty(TIMESTAMP));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsInstdataKdi.Annullering.class, InstdataKdiDTO.Annullering.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInstdataKdi.Annullering kilde, InstdataKdiDTO.Annullering dest, MappingContext context) {

                        dest.setNorskident((String) context.getProperty(IDENT));
                        dest.setPubliseringstidspunkt((LocalDateTime) context.getProperty(TIMESTAMP));
                    }
                })
                .byDefault()
                .register();
    }

    private static Long getBestillingId(String maksId, Long bestillingId) {

        return maksId.length() == 30 ? Long.parseLong(maksId.substring(2,  21)) : bestillingId;
    }

    private static Long getLoepenummer(String maksId) {

        return maksId.length() == 30 ? Long.parseLong(maksId.substring(20)) : 0L;
    }

    private static String getMaxHendelseId(List<InstdataKdiDTO.Hendelse> response) {

        return response.stream()
                .map(InstdataKdiDTO.Hendelse::getHendelseId)
                .max(String::compareTo)
                .orElse("0");
    }

    private static String makeHendelseId(Long bestillingId, Integer eksisterendeLoepenr, Integer offsetLoepenr) {

        return "0x%0d18%0d10".formatted(bestillingId, eksisterendeLoepenr + offsetLoepenr);
    }
}
