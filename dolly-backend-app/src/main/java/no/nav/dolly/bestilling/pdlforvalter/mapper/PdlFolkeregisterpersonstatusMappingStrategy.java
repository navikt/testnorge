package no.nav.dolly.bestilling.pdlforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFolkeregisterpersonstatus;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpplysning.Master;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlFolkeregisterpersonstatus.Folkeregisterpersonstatus;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;

@Component
public class PdlFolkeregisterpersonstatusMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Person.class, PdlFolkeregisterpersonstatus.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Person person,
                                        PdlFolkeregisterpersonstatus personstatus, MappingContext context) {

                        personstatus.setKilde(CONSUMER);
                        personstatus.setMaster(Master.FREG);
                        personstatus.setStatus(getStatus(person.getPersonStatus()));
                    }
                })
                .byDefault()
                .register();
    }

    private static Folkeregisterpersonstatus getStatus(String status) {

        if (isNull(status)) {
            return Folkeregisterpersonstatus.MIDLERTIDIG;
        }
        return switch (status) {
            case "BOSA" -> Folkeregisterpersonstatus.BOSATT;
            case "DØD", "DØDD" -> Folkeregisterpersonstatus.DOED;
            case "FØDR" -> Folkeregisterpersonstatus.FOEDSELSREGISTRERT;
            case "FOSV" -> Folkeregisterpersonstatus.FORSVUNNET;
            case "UREG", "UTAN" -> Folkeregisterpersonstatus.IKKE_BOSATT;
            case "UTPE" -> Folkeregisterpersonstatus.OPPHOERT;
            case "UTVA" -> Folkeregisterpersonstatus.UTFLYTTET;
            default -> Folkeregisterpersonstatus.MIDLERTIDIG;
        };
    }
}
