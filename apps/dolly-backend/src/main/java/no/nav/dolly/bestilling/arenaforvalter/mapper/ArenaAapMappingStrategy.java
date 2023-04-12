package no.nav.dolly.bestilling.arenaforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.arenaforvalter.dto.Aap;
import no.nav.dolly.bestilling.arenaforvalter.dto.Aap115;
import no.nav.dolly.bestilling.arenaforvalter.dto.Aap115Request;
import no.nav.dolly.bestilling.arenaforvalter.dto.AapRequest;
import no.nav.dolly.bestilling.arenaforvalter.dto.Vilkaar;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.arenaforvalter.RsArenaAap;
import no.nav.dolly.domain.resultset.arenaforvalter.RsArenaAap115;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class ArenaAapMappingStrategy implements MappingStrategy {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
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
                            aap115Request.getVilkaar().addAll(getAlleVilkaar());
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

        factory.classMap(Arenadata.class, AapRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Arenadata arenadata, AapRequest aapRequest, MappingContext context) {

                        aapRequest.setPersonident((String) context.getProperty("ident"));
                        aapRequest.setMiljoe((String) context.getProperty("miljoe"));
                        aapRequest.setNyeAap(mapperFacade.mapAsList(arenadata.getAap(), Aap.class));
                    }
                })
                .register();

        factory.classMap(RsArenaAap.class, Aap.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsArenaAap arenadata, Aap aapRequest, MappingContext context) {

                        if (aapRequest.getVilkaar().isEmpty()) {
                            aapRequest.getVilkaar().addAll(getAlleVilkaar());
                        }

                        if (aapRequest.getGenSaksopplysninger().isEmpty()) {
                            aapRequest.getGenSaksopplysninger().addAll(getAlleSaksopplysninger(aapRequest.getFraDato()));
                        }

                        aapRequest.setAktivitetsfase(isBlank(aapRequest.getAktivitetsfase()) ? "UA" :
                                aapRequest.getAktivitetsfase());

                        aapRequest.setMedlemFolketrygden(isNull(aapRequest.getMedlemFolketrygden()) ?
                                new Aap.MedlemFolketrygden("MEDL1", "JA") :
                                aapRequest.getMedlemFolketrygden());

                        aapRequest.setUtfall(isBlank(aapRequest.getUtfall()) ? "JA" :
                                aapRequest.getUtfall());

                        aapRequest.setBegrunnelse(isBlank(aapRequest.getBegrunnelse()) ?
                                "Dolly legger til grunn at du er arbeidsufør på grunn av sykdom." :
                                aapRequest.getBegrunnelse());

                        aapRequest.setVedtaktype(isNull(aapRequest.getVedtaktype()) ?
                                Aap.VedtakType.O : aapRequest.getVedtaktype());
                    }
                })
                .byDefault()
                .register();
    }

    private List<Vilkaar> getAlleVilkaar() {
        return List.of(
                new Vilkaar("18-67AAR", "J"),
                new Vilkaar("INNTNEDS", "J"),
                new Vilkaar("SYKSKADLYT", "J"),
                new Vilkaar("AAARBEVNE", "J"),
                new Vilkaar("AANORSKFER", "J"),
                new Vilkaar("AATYPEJOBB", "J"),
                new Vilkaar("AANAAVJOBB", "J"),
                new Vilkaar("AANODVDOKU", "J"),
                new Vilkaar("AAMOTTSAMT", "J"),
                new Vilkaar("AASNARTARB", "J"),
                new Vilkaar("AASNARARBG", "J")
        );
    }

    private List<Aap.Saksopplysning> getAlleSaksopplysninger(LocalDate startdato) {

        return List.of(
                new Aap.Saksopplysning("KDATO", null, startdato.format(FORMAT)),
                new Aap.Saksopplysning("BTID", null, startdato.format(FORMAT)),
                new Aap.Saksopplysning("TUUIN", null, null),
                new Aap.Saksopplysning("UUFOR", null, null),
                new Aap.Saksopplysning("STUBE", null, null),
                new Aap.Saksopplysning("OTILF", null, null),
                new Aap.Saksopplysning("OTSEK", null, null),
                new Aap.Saksopplysning("OOPPL", null, null),
                new Aap.Saksopplysning("BDSAT", "OOPPL", null),
                new Aap.Saksopplysning("UFTID", "OOPPL", null),
                new Aap.Saksopplysning("BDSRP", "OOPPL", null),
                new Aap.Saksopplysning("GRDRP", "OOPPL", null),
                new Aap.Saksopplysning("GRDTU", "OOPPL", null),
                new Aap.Saksopplysning("BDSTU", "OOPPL", null),
                new Aap.Saksopplysning("GGRAD", "OOPPL", null),
                new Aap.Saksopplysning("ORIGG", "OOPPL", null),
                new Aap.Saksopplysning("YDATO", null, null),
                new Aap.Saksopplysning("YINNT", null, null),
                new Aap.Saksopplysning("YGRAD", null, null),
                new Aap.Saksopplysning("TLONN", null, null),
                new Aap.Saksopplysning("SPROS", "TLONN", null),
                new Aap.Saksopplysning("NORIN", "TLONN", null),
                new Aap.Saksopplysning("FAKIN", "TLONN", null),
                new Aap.Saksopplysning("EOS", null, null),
                new Aap.Saksopplysning("LAND", null, null)
        );
    }

    private static LocalDate toDate(LocalDateTime localDateTime) {

        return nonNull(localDateTime) ? localDateTime.toLocalDate() : null;
    }
}
