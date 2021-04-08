package no.nav.registre.sdforvalter.consumer.rs.domain;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.sdforvalter.domain.Ereg;
import no.nav.registre.sdforvalter.domain.EregListe;

@Slf4j
@Value
public class OrgTreeList {
    List<OrgTree> list;

    public OrgTreeList(EregListe eregListe) {
        this.list = new ArrayList<>();

        eregListe
                .getListe()
                .stream()
                .filter(ereg -> !contains(ereg) && ereg.getJuridiskEnhet() == null)
                .forEach(ereg -> OrgTree.from(ereg, eregListe.getListe()));


        var sum = list.stream().map(OrgTree::size).reduce(0, Integer::sum);
        if (eregListe.size() != sum) {
            log.warn("Bare {}/{} organisasjoner er sattopp i organisasjon traer.", sum, eregListe.size());
            log.warn("Organisasjonene finnes ikke i organisasjon treet {}.", notInTree(eregListe));
        }
    }

    public boolean contains(Ereg ereg) {
        return list.stream().anyMatch(value -> value.contains(ereg));
    }

    private Set<String> notInTree(EregListe eregListe) {
        return eregListe
                .stream()
                .filter(value -> !isInnOrgTree(value))
                .map(Ereg::getOrgnr)
                .collect(Collectors.toSet());
    }

    private boolean isInnOrgTree(Ereg ereg) {
        return list.stream().anyMatch(value -> value.contains(ereg));
    }

}
