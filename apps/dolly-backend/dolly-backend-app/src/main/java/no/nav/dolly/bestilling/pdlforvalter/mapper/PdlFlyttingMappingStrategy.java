package no.nav.dolly.bestilling.pdlforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlInnflytting;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlInnflyttingHistorikk;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpplysning;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpplysning.Master;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlUtflytting;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlUtflyttingHistorikk;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.resultset.tpsf.InnvandretUtvandret.InnUtvandret.INNVANDRET;
import static no.nav.dolly.domain.resultset.tpsf.InnvandretUtvandret.InnUtvandret.UTVANDRET;

@Component
public class PdlFlyttingMappingStrategy implements MappingStrategy {

    private static LocalDate getDate(LocalDateTime timestamp){

        return  nonNull(timestamp) ? timestamp.toLocalDate() : null;
    }

    @Override public void register(MapperFactory factory) {

        factory.classMap(Person.class, PdlInnflyttingHistorikk.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Person person, PdlInnflyttingHistorikk historikk, MappingContext context) {

                        person.getInnvandretUtvandret().forEach(innvandretUtvandret -> {

                            if (INNVANDRET == innvandretUtvandret.getInnutvandret()) {
                                historikk.getInnflyttinger().add(
                                        PdlInnflytting.builder()
                                                .fraflyttingsland(innvandretUtvandret.getLandkode())
                                                .folkeregistermetadata(PdlOpplysning.Folkeregistermetadata.builder()
                                                        .gyldighetstidspunkt(getDate(innvandretUtvandret.getFlyttedato()))
                                                        .build())
                                                .kilde(CONSUMER)
                                                .master(Master.FREG)
                                                .build()
                                );
                            }
                        });
                    }
                })
                .register();

        factory.classMap(Person.class, PdlUtflyttingHistorikk.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Person person, PdlUtflyttingHistorikk historikk, MappingContext context) {

                        person.getInnvandretUtvandret().forEach(innvandretUtvandret -> {

                            if (UTVANDRET == innvandretUtvandret.getInnutvandret()) {
                                historikk.getUtflyttinger().add(
                                        PdlUtflytting.builder()
                                                .tilflyttingsland(innvandretUtvandret.getLandkode())
                                                .utflyttingsdato(getDate(innvandretUtvandret.getFlyttedato()))
                                                .kilde(CONSUMER)
                                                .master(Master.FREG)
                                                .build()
                                );
                            }
                        });
                    }
                })
                .register();
    }
}
