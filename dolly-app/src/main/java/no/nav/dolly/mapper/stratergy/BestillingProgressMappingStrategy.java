package no.nav.dolly.mapper.stratergy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsBestilling;
import no.nav.dolly.domain.resultset.RsBestillingProgress;
import no.nav.dolly.mapper.MappingStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.stereotype.Component;

import static no.nav.dolly.util.UtilFunctions.isNullOrEmpty;

@Component
public class BestillingProgressMappingStrategy implements MappingStrategy{
    @Override
    public void register(MapperFactory factory) {
        factory.classMap(BestillingProgress.class, RsBestillingProgress.class)
                .customize(new CustomMapper<BestillingProgress, RsBestillingProgress>() {
                    @Override
                    public void mapAtoB(BestillingProgress progress, RsBestillingProgress rsProgress, MappingContext context) {
                        rsProgress.setId(progress.getId());
                        rsProgress.setBestillingsId(progress.getBestillingId());
                        rsProgress.setIdent(progress.getIdent());
                        rsProgress.setFeil(progress.getFeil());

                        if(!isNullOrEmpty(progress.getTpsfSuccessEnv())){
                            rsProgress.setTpsfSuccessEnv(
                                    new ArrayList<>(Arrays.asList(progress.getTpsfSuccessEnv().split(",")))
                            );
                        }

                        if(!isNullOrEmpty(progress.getSigrunSuccessEnv())){
                            rsProgress.setSigrunSuccessEnv(
                                    new ArrayList<>(Arrays.asList(progress.getSigrunSuccessEnv().split(",")))
                            );
                        }

                        if(!isNullOrEmpty(progress.getAaregSuccessEnv())){
                            rsProgress.setAaregSuccessEnv(
                                   new ArrayList<>(Arrays.asList(progress.getAaregSuccessEnv().split(",")))
                            );
                        }
                    }
                })
                .register();
    }
}
