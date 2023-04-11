package no.nav.dolly.bestilling.arenaforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.arenaforvalter.dto.Aap115;
import no.nav.dolly.bestilling.arenaforvalter.dto.Aap115Request;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.arenaforvalter.RsArenaAap115;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class ArenaAapMappingStrategy implements MappingStrategy {


    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Arenadata.class, Aap115Request.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Arenadata arenadata, Aap115Request aap115Request, MappingContext context) {

                        aap115Request.setPersonident((String) context.getProperty("ident"));
                        aap115Request.setMiljoe((String) context.getProperty("miljoe"));
                        aap115Request.setNyeAap115(mapperFacade.mapAsList(arenadata.getAap115(), Aap115.class));
                    }
                })
                .register();

        factory.classMap(RsArenaAap115.class, Aap115.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsArenaAap115 arenadata, Aap115 aap115Request, MappingContext context) {

                        if (aap115Request.getMedisinskOpplysning().isEmpty()) {
                            aap115Request.getMedisinskOpplysning().add(Aap115.MedisinskOpplysning.builder()
                                    .type("HOVED")
                                    .klassifisering("5")
                                    .diagnose("L86")
                                    .kilde("LEGE")
                                    .kildeDato(toDate(arenadata.getFraDato()))
                                    .build());
                        }

                        if (aap115Request.getVilkaar().isEmpty()) {
                            aap115Request.getVilkaar().addAll(List.of(
                                    new Aap115.Vilkaar("18-67AAR", "J"),
                                    new Aap115.Vilkaar("INNTNEDS", "J"),
                                    new Aap115.Vilkaar("SYKSKADLYT", "J"),
                                    new Aap115.Vilkaar("AAARBEVNE", "J"),
                                    new Aap115.Vilkaar("AANORSKFER", "J"),
                                    new Aap115.Vilkaar("AATYPEJOBB", "J"),
                                    new Aap115.Vilkaar("AANAAVJOBB", "J"),
                                    new Aap115.Vilkaar("AANODVDOKU", "J"),
                                    new Aap115.Vilkaar("AAMOTTSAMT", "J"),
                                    new Aap115.Vilkaar("AASNARTARB", "J"),
                                    new Aap115.Vilkaar("AASNARARBG", "J")));
                        }
                        aap115Request.setUtfall(isBlank(aap115Request.getUtfall()) ? "JA" :
                                aap115Request.getUtfall());
                        aap115Request.setBegrunnelse(isBlank(aap115Request.getBegrunnelse()) ?
                                "Dolly legger til grunn at du er arbeidsufør på grunn av sykdom." :
                                aap115Request.getBegrunnelse());
                        aap115Request.setVedtaktype(isNull(aap115Request.getVedtaktype()) ?
                                Aap115.VedtaksType.O : aap115Request.getVedtaktype());
                    }
                })
                .byDefault()
                .register();
    }

    private static LocalDate toDate(LocalDateTime localDateTime) {

        return nonNull(localDateTime) ? localDateTime.toLocalDate() : null;
    }
}
