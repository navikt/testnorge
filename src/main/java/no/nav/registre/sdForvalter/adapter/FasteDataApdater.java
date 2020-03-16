package no.nav.registre.sdForvalter.adapter;

import lombok.AllArgsConstructor;

import no.nav.registre.sdForvalter.database.model.GruppeModel;
import no.nav.registre.sdForvalter.database.model.OpprinnelseModel;
import no.nav.registre.sdForvalter.domain.FasteData;

@AllArgsConstructor
abstract class FasteDataApdater {

    private final OpprinnelseAdapter opprinnelseAdapter;
    private final GruppeAdapter gruppeAdapter;

    OpprinnelseModel getOppinnelse(FasteData fasteData) {
        return fasteData.getOpprinelse() != null
                ? opprinnelseAdapter.saveOpprinnelse(fasteData.getOpprinelse())
                : null;
    }

    GruppeModel getGruppe(FasteData fasteData) {
        return fasteData.getGruppe() != null
                ? gruppeAdapter.fetchGruppe(fasteData.getGruppe())
                : null;
    }
}
