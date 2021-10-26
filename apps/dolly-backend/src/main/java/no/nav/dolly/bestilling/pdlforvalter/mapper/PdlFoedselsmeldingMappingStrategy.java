package no.nav.dolly.bestilling.pdlforvalter.mapper;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFoedsel;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpplysning.Master;
import no.nav.dolly.consumer.kodeverk.KodeverkConsumer;
import no.nav.dolly.domain.resultset.tpsf.InnvandretUtvandret;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.adresse.BoAdresse;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.resultset.tpsf.InnvandretUtvandret.InnUtvandret.INNVANDRET;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
@RequiredArgsConstructor
public class PdlFoedselsmeldingMappingStrategy implements MappingStrategy {

    private static final String NORGE = "NOR";
    private static final String KOMMUNER = "Kommuner";
    private static final String LAND = "Landkoder";

    private final KodeverkConsumer kodeverkConsumer;

    private SecureRandom secureRandom = new SecureRandom();

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Person.class, PdlFoedsel.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Person person, PdlFoedsel pdlFoedsel, MappingContext context) {

                        pdlFoedsel.setFoedselsaar(person.getFoedselsdato().getYear());
                        pdlFoedsel.setFoedeland(person.getInnvandretUtvandret().stream()
                                .filter(innvandretUtvandret -> innvandretUtvandret.getInnutvandret() == INNVANDRET)
                                .map(InnvandretUtvandret::getLandkode)
                                .reduce((first, second) -> second).orElse(NORGE));
                        pdlFoedsel.setFodekommune(NORGE.equals(pdlFoedsel.getFoedeland()) ? getFoedekommune(person) : null);
                        pdlFoedsel.setFoedested("Fødested i/på " + (isNotBlank(pdlFoedsel.getFodekommune()) ?
                                pdlFoedsel.getFodekommune() :
                                kodeverkConsumer.getKodeverkByName(LAND).get(pdlFoedsel.getFoedeland())));
                        pdlFoedsel.setKilde(CONSUMER);
                        pdlFoedsel.setMaster(Master.FREG);
                    }

                    private String getFoedekommune(Person person) {
                        if (!person.getBoadresse().isEmpty()) {
                            return kodeverkConsumer.getKodeverkByName(KOMMUNER).get(
                                    person.getBoadresse().stream()
                                            .map(BoAdresse::getKommunenr)
                                            .reduce((first, second) -> second)
                                            .get());
                        } else {
                            return kodeverkConsumer.getKodeverkByName(KOMMUNER).get(Math.floor(secureRandom.nextFloat() *
                                    kodeverkConsumer.getKodeverkByName(KOMMUNER).size()));
                        }
                    }

                })
                .byDefault()
                .register();
    }
}
