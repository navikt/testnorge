package no.nav.dolly.mapper.strategy;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.join;
import static java.util.Collections.emptySet;
import static java.util.Objects.nonNull;
import static no.nav.dolly.mapper.BestillingAaregStatusMapper.buildAaregStatusMap;
import static no.nav.dolly.mapper.BestillingArenaforvalterStatusMapper.buildArenaStatusMap;
import static no.nav.dolly.mapper.BestillingInstdataStatusMapper.buildInstdataStatusMap;
import static no.nav.dolly.mapper.BestillingPdlForvalterStatusMapper.buildPdldataStatusMap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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
                        List<BestillingProgress> bestillinger = testgruppe.getBestillingProgress();
                        if (!bestillinger.isEmpty()) {
                            List<Long> bestillingListe = new ArrayList<>(bestillinger.size());
                            Set<String> environments = new TreeSet<>();
                            for (BestillingProgress progress : bestillinger) {
                                bestillingListe.add(progress.getBestillingId());
                                environments.addAll(nonNull(progress.getTpsfSuccessEnv()) ?
                                        newHashSet(progress.getTpsfSuccessEnv().split(",")) : emptySet());
                            }
                            bestillingListe.sort(Comparator.reverseOrder());
                            rsTestIdent.setBestillingId(bestillingListe);
                            rsTestIdent.setTpsfSuccessEnv(join(",", environments));
                            rsTestIdent.setKrrstubStatus(bestillinger.get(0).getKrrstubStatus());
                            rsTestIdent.setSigrunstubStatus(bestillinger.get(0).getSigrunstubStatus());
                            rsTestIdent.setArenaforvalterStatus(buildArenaStatusMap(bestillinger));
                            rsTestIdent.setAaregStatus(buildAaregStatusMap(bestillinger));
                            rsTestIdent.setPdlforvalterStatus(buildPdldataStatusMap(bestillinger));
                            rsTestIdent.setInstdataStatus(buildInstdataStatusMap(bestillinger));
                        }
                    }
                })
                .byDefault()
                .register();
    }
}
