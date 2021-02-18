package no.nav.registre.sdforvalter.consumer.rs.domain;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.sdforvalter.domain.Ereg;
import no.nav.registre.sdforvalter.domain.EregListe;

@Value
public class OrgTreeList {
    List<OrgTree> list;

    public OrgTreeList(EregListe eregListe) {
        this.list = new ArrayList<>();
        for (Ereg ereg : eregListe.getListe()) {
            if (!contains(ereg)) {
                list.add(OrgTree.from(ereg, eregListe.getListe()));
            }
        }
    }

    public boolean contains(Ereg ereg) {
        return list.stream().anyMatch(value -> value.contains(ereg));
    }


}
