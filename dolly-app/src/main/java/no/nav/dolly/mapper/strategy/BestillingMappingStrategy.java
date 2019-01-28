package no.nav.dolly.mapper.strategy;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Objects.nonNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsBestilling;
import no.nav.dolly.domain.resultset.RsIdentStatus;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class BestillingMappingStrategy implements MappingStrategy {

    @Override public void register(MapperFactory factory) {
        factory.classMap(Bestilling.class, RsBestilling.class)
                .customize(new CustomMapper<Bestilling, RsBestilling>() {
                    @Override public void mapAtoB(Bestilling bestilling, RsBestilling rsBestilling, MappingContext context) {
                        rsBestilling.setEnvironments(Arrays.asList(bestilling.getMiljoer().split(",")));
                        rsBestilling.setGruppeId(bestilling.getGruppe().getId());
                        Map<String, Map<String, Set<String>>> errorEnvIdents = new HashMap<>();
                        Set<Testident> identer = bestilling.getGruppe().getTestidenter();
                        identer.forEach(ident -> ident.getBestillingProgress().forEach(progress -> {
                            if (bestilling.getId().equals(progress.getBestillingId())) {
                                prepStatus(errorEnvIdents, progress.getFeil(), ident);
                            }
                        }));
                        errorEnvIdents.keySet().forEach(env ->
                                rsBestilling.getStatus().add(RsIdentStatus.builder()
                                        .statusMelding(env)
                                        .environmentIdents(errorEnvIdents.get(env))
                                        .build())
                        );
                    }
                })
                .byDefault()
                .register();
    }

    private void prepStatus(Map<String, Map<String, Set<String>>> errorEnvIdents, String feil, Testident ident) {
        if (nonNull(feil)) {
            newArrayList(feil.split(",")).forEach(error -> {
                String environ = error.split(":", 2)[0];
                String errMsg = error.split(":", 2)[1].trim();
                if (errorEnvIdents.containsKey(errMsg)) {
                    if (errorEnvIdents.get(errMsg).containsKey(environ)) {
                        errorEnvIdents.get(errMsg).get(environ).add(ident.getIdent());
                    } else {
                        errorEnvIdents.get(errMsg).put(environ, newHashSet(ident.getIdent()));
                    }
                } else {
                    Map<String, Set<String>> entry = new HashMap();
                    entry.put(environ, newHashSet(ident.getIdent()));
                    errorEnvIdents.put(errMsg, entry);
                }
            });
        }
    }
}
