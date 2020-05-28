package no.nav.registre.sdForvalter.adapter;

import lombok.AllArgsConstructor;

import no.nav.registre.sdForvalter.database.model.GruppeModel;
import no.nav.registre.sdForvalter.database.model.OpprinnelseModel;
import no.nav.registre.sdForvalter.domain.FasteData;

@AllArgsConstructor
abstract class FasteDataAdapter {

    private final OpprinnelseAdapter opprinnelseAdapter;
    private final GruppeAdapter gruppeAdapter;

    OpprinnelseModel getOppinnelse(FasteData fasteData) {
        return fasteData.getOpprinnelse() != null
                ? opprinnelseAdapter.saveOpprinnelse(fasteData.getOpprinnelse())
                : null;
    }

    GruppeModel getGruppe(FasteData fasteData) {
        return fasteData.getGruppe() != null
                ? gruppeAdapter.fetchGruppe(fasteData.getGruppe())
                : null;
    }
}
