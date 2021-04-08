package no.nav.registre.sdforvalter.consumer.rs.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.sdforvalter.domain.Ereg;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrgTree {
    Ereg organisasjon;
    List<OrgTree> underorganisasjon;

    public static OrgTree from(Ereg ereg, List<Ereg> liste) {
        return new OrgTree(
                ereg,
                findAllWithJuridiskenhet(ereg, liste)
        );
    }

    public int size() {
        return underorganisasjon.stream().map(OrgTree::size).reduce(1, Integer::sum);
    }

    public boolean contains(Ereg ereg) {
        return organisasjon.getOrgnr().equals(ereg.getOrgnr()) || underorganisasjon.stream().anyMatch(value -> value.contains(ereg));
    }

    private static List<OrgTree> findAllWithJuridiskenhet(Ereg juridsikenhet, List<Ereg> liste) {
        return liste
                .stream()
                .filter(ereg -> ereg.getJuridiskEnhet() != null && ereg.getJuridiskEnhet().equals(juridsikenhet.getOrgnr()))
                .map(ereg -> OrgTree.from(ereg, liste))
                .collect(Collectors.toList());
    }

}
