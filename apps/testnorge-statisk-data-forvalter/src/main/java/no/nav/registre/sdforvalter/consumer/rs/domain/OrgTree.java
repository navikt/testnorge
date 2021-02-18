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

    public boolean contains(Ereg ereg){
        return organisasjon.getOrgnr().equals(ereg.getOrgnr()) || underorganisasjon.stream().anyMatch(value -> value.contains(ereg));
    }

    private static List<OrgTree> findAllWithJuridiskenhet(Ereg juridsikenhet, List<Ereg> liste){
        return liste
                .stream()
                .filter(ereg -> ereg.getOrgnr().equals(juridsikenhet.getOrgnr()))
                .map(value -> OrgTree.from(value, liste))
                .collect(Collectors.toList());
    }

}
