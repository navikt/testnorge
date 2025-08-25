package no.nav.dolly.mapper;

import lombok.experimental.UtilityClass;
import no.nav.dolly.domain.jpa.Bestilling;

@UtilityClass
public class BestillingMapper {

    public Bestilling shallowCopyBestilling(Bestilling bestilling) {

        return Bestilling.builder()
                .id(bestilling.getId())
                .gruppeId(bestilling.getGruppeId())
                .antallIdenter(bestilling.getAntallIdenter())
                .bestKriterier(bestilling.getBestKriterier())
                .beskrivelse(bestilling.getBeskrivelse())
                .kildeMiljoe(bestilling.getKildeMiljoe())
                .miljoer(bestilling.getMiljoer())
                .navSyntetiskIdent(bestilling.getNavSyntetiskIdent())
                .opprettFraIdenter(bestilling.getOpprettFraIdenter())
                .pdlImport(bestilling.getPdlImport())
                .sistOppdatert(bestilling.getSistOppdatert())
                .gjenopprettetFraIdent(bestilling.getGjenopprettetFraIdent())
                .opprettetFraGruppeId(bestilling.getOpprettetFraGruppeId())
                .opprettetFraId(bestilling.getOpprettetFraId())
                .feil(bestilling.getFeil())
                .ferdig(true)
                .stoppet(false)
                .build();
    }
}
