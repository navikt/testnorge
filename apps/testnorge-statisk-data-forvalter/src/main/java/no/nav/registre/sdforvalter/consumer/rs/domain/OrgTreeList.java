package no.nav.registre.sdforvalter.consumer.rs.domain;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.sdforvalter.domain.Ereg;
import no.nav.registre.sdforvalter.domain.EregListe;

@Slf4j
@Value
public class OrgTreeList {
    List<OrgTree> list;

    public OrgTreeList(EregListe eregListe) {
        this.list = new ArrayList<>();
        for (Ereg ereg : eregListe.getListe()) {
            if (!contains(ereg) && ereg.getJuridiskEnhet() == null) {
                list.add(OrgTree.from(ereg, eregListe.getListe()));
            }
        }
        var sum = list.stream().map(OrgTree::size).reduce(0, Integer::sum);
        log.info("{}/{} organisasjoner er sattopp i organisasjon traer", sum, eregListe.size());
    }

    public boolean contains(Ereg ereg) {
        return list.stream().anyMatch(value -> value.contains(ereg));
    }
}
