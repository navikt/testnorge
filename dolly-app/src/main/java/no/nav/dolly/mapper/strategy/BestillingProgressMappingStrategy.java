package no.nav.dolly.mapper.strategy;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsBestillingProgress;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class BestillingProgressMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(BestillingProgress.class, RsBestillingProgress.class)
                .customize(new CustomMapper<BestillingProgress, RsBestillingProgress>() {
                    @Override
                    public void mapAtoB(BestillingProgress progress, RsBestillingProgress rsProgress, MappingContext context) {
                        rsProgress.setBestillingsId(progress.getBestillingId());

                        if (isNotBlank(progress.getTpsfSuccessEnv())) {
                            rsProgress.setTpsfSuccessEnv(
                                    new ArrayList<>(Arrays.asList(progress.getTpsfSuccessEnv().split(",")))
                            );
                        }

                        if (isNotBlank(progress.getFeil())) {
                            rsProgress.setFeil(
                                    new ArrayList<>(Arrays.asList(progress.getFeil().split(",")))
                            );
                        }

                        if (isNotBlank(progress.getSigrunstubStatus())) {
                            rsProgress.setSigrunstubStatus(
                                    progress.getSigrunstubStatus()
                            );
                        }

                        if (isNotBlank(progress.getKrrstubStatus())) {
                            rsProgress.setKrrstubStatus(
                                    progress.getKrrstubStatus()
                            );
                        }

                        if (isNotBlank(progress.getAaregStatus())) {
                            rsProgress.setAaregStatus(
                                    progress.getAaregStatus()
                            );
                        }
                    }
                })
                .exclude("tpsfSuccessEnv")
                .exclude("feil")
                .byDefault()
                .register();
    }
}
