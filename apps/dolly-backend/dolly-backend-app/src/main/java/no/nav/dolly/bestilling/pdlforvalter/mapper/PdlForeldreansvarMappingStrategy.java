package no.nav.dolly.bestilling.pdlforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlForeldreansvar;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlForeldreansvar.Ansvar;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpplysning.Master;
import no.nav.dolly.domain.resultset.tpsf.Relasjon;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.resultset.tpsf.Relasjon.ROLLE.MOR;

@Component
public class PdlForeldreansvarMappingStrategy implements MappingStrategy {

    private static Ansvar getAnsvar(Relasjon relasjon) {

        if (relasjon.getFellesAnsvar()) {
            return Ansvar.FELLES;
        }
        return MOR == relasjon.getRelasjonTypeNavn() ? Ansvar.MOR : Ansvar.FAR;
    }

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Relasjon.class, PdlForeldreansvar.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Relasjon relasjon, PdlForeldreansvar foreldreansvar, MappingContext context) {

                        foreldreansvar.setAnsvarlig(relasjon.getPersonRelasjonMed().getIdent());
                        foreldreansvar.setAnsvar(getAnsvar(relasjon));
                        foreldreansvar.setKilde(CONSUMER);
                        foreldreansvar.setMaster(Master.FREG);
                    }
                })
                .register();
    }
}
