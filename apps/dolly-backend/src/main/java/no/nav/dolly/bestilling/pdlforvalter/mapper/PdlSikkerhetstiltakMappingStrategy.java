package no.nav.dolly.bestilling.pdlforvalter.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpplysning.Master;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlSikkerhetstiltak;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Component
@RequiredArgsConstructor
public class PdlSikkerhetstiltakMappingStrategy implements MappingStrategy {

    private static LocalDate getDate(LocalDateTime timestamp) {
        return nonNull(timestamp) ? timestamp.toLocalDate() : null;
    }

    private static String getBeskrivelse(String type) {

        if (isBlank(type)) {
            return null;
        }

        return switch (type) {
            case "FYUS" -> "Fysisk utestengelse";
            case "TFUS" -> "Telefonisk utestengelse";
            case "FTUS" -> "Fysisk/telefonisk utestengelse";
            case "DIUS" -> "Digital utestengelse";
            case "TOAN" -> "To ansatte i samtale";
            default -> null;
        };
    }

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Person.class, PdlSikkerhetstiltak.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Person person, PdlSikkerhetstiltak sikkerhetstiltak, MappingContext context) {

                        sikkerhetstiltak.setTiltakstype(person.getTypeSikkerhetTiltak());
                        sikkerhetstiltak.setBeskrivelse(nullcheckSetDefaultValue(person.getBeskrSikkerhetTiltak(),
                                getBeskrivelse(person.getTypeSikkerhetTiltak())));
                        sikkerhetstiltak.setGyldigFraOgMed(getDate(person.getSikkerhetTiltakDatoFom()));
                        sikkerhetstiltak.setGyldigTilOgMed(getDate(person.getSikkerhetTiltakDatoTom()));

                        sikkerhetstiltak.setKilde(CONSUMER);
                        sikkerhetstiltak.setMaster(Master.PDL);
                    }
                })
                .register();
    }
}