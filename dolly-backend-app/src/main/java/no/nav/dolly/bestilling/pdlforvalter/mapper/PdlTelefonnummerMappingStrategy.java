package no.nav.dolly.bestilling.pdlforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpplysning.Master;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlTelefonnummer;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class PdlTelefonnummerMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Person.class, PdlTelefonnummer.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Person person, PdlTelefonnummer telefonnummer, MappingContext context) {

                        if (isNotBlank(person.getTelefonnummer_1())) {
                            telefonnummer.getTelfonnumre().add(PdlTelefonnummer.Entry.builder()
                                    .kilde(CONSUMER)
                                    .master(Master.PDL)
                                    .prioritet(1)
                                    .landskode(person.getTelefonLandskode_1())
                                    .nummer(person.getTelefonnummer_1())
                                    .build());
                        }
                        if (isNotBlank(person.getTelefonnummer_2())) {
                            telefonnummer.getTelfonnumre().add(PdlTelefonnummer.Entry.builder()
                                    .kilde(CONSUMER)
                                    .master(Master.PDL)
                                    .prioritet(2)
                                    .landskode(person.getTelefonLandskode_2())
                                    .nummer(person.getTelefonnummer_2())
                                    .build());
                        }
                    }
                })
                .register();
    }
}
