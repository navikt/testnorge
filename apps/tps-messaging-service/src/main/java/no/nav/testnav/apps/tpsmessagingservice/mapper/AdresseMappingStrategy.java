package no.nav.testnav.apps.tpsmessagingservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.GateadresseDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.MidlertidigAdresseDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PostadresseDTO;
import no.nav.tps.ctg.s610.domain.BoAdresseType;
import no.nav.tps.ctg.s610.domain.PostAdresseType;
import no.nav.tps.ctg.s610.domain.UtlandsAdresseType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
public class AdresseMappingStrategy implements MappingStrategy {

    private static final String POST_NORGE = "POST";
    private static final String NORGE = "NOR";

    private static String skipLeadZeros(String number) {

        return StringUtils.isNumeric(number) ?
                Integer.valueOf(number).toString() :
                number;
    }

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(PostAdresseType.class, PostadresseDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PostAdresseType source, PostadresseDTO target, MappingContext context) {

                        target.setPostLinje1(source.getAdresse1());
                        target.setPostLinje2(source.getAdresse2());
                        target.setPostLinje3(source.getAdresse3());

                        if (POST_NORGE.equals(source.getAdresseType())) {

                            target.setPostLinje3(format("%s %s", source.getPostnr(), source.getPoststed()));
                            target.setPostLand(NORGE);

                        } else {
                            target.setPostLand(source.getLand());
                        }
                    }
                })
                .register();

        factory.classMap(BoAdresseType.class, GateadresseDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BoAdresseType source, GateadresseDTO target, MappingContext context) {

                        target.setAdresse(source.getOffAdresse().getGateNavn());
                        target.setHusnummer(skipLeadZeros(source.getOffAdresse().getHusnr()));
                        target.setGatekode(source.getOffAdresse().getGatekode());
                    }
                })
                .byDefault()
                .register();

        factory.classMap(BoAdresseType.class, MatrikkeladresseDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BoAdresseType source, MatrikkeladresseDTO target, MappingContext context) {

                        target.setMellomnavn(source.getMatrAdresse().getMellomAdresse());
                        target.setGardsnr(skipLeadZeros(source.getMatrAdresse().getGardsnr()));
                        target.setBruksnr(skipLeadZeros(source.getMatrAdresse().getBruksnr()));
                        target.setFestenr(skipLeadZeros(source.getMatrAdresse().getFestenr()));
                        target.setUndernr(skipLeadZeros(source.getMatrAdresse().getUndernr()));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(UtlandsAdresseType.class, MidlertidigAdresseDTO.MidlertidigUtadAdresseDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(UtlandsAdresseType source, MidlertidigAdresseDTO.MidlertidigUtadAdresseDTO target, MappingContext context) {

                        target.setPostLinje1(source.getAdresse1());
                        target.setPostLinje2(source.getAdresse2());
                        target.setPostLinje3(source.getAdresse3());
                        target.setPostLand(source.getLand());
                    }
                })
                .register();
    }
}
