package no.nav.dolly.mapper.strategy;

import static java.lang.String.join;
import static java.util.Arrays.sort;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsTestidentBestillingId;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class TestIdentMappingStrategy implements MappingStrategy {

    @Override public void register(MapperFactory factory) {
        factory.classMap(Testident.class, RsTestidentBestillingId.class)
                .customize(new CustomMapper<Testident, RsTestidentBestillingId>() {
                    @Override
                    public void mapAtoB(Testident testgruppe, RsTestidentBestillingId rsTestIdent, MappingContext context) {
                        List<BestillingProgress> testgrupper = testgruppe.getBestillingProgress();
                        if (!testgrupper.isEmpty()) {
                            BestillingProgress bestillingProgress = testgrupper.get(0);
                            rsTestIdent.setBestillingId(bestillingProgress.getBestillingId());
                            String[] environments = bestillingProgress.getTpsfSuccessEnv().split(",");
                            sort(environments);
                            rsTestIdent.setTpsfSuccessEnv(join(",", environments));
                            rsTestIdent.setKrrstubStatus(bestillingProgress.getKrrstubStatus());
                            rsTestIdent.setSigrunstubStatus(bestillingProgress.getSigrunstubStatus());
                            rsTestIdent.setSisteOppdatering(LocalDateTime.now());
                        }
                    }
                })
                .byDefault()
                .register();
    }
}
