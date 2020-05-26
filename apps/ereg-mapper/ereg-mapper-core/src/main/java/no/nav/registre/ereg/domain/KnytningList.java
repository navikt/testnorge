package no.nav.registre.ereg.domain;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.ereg.provider.rs.request.KnytningRs;

public class KnytningList extends Flatfil {

    private final List<String> records;

    public KnytningList(List<String> records) {
        this.records = records;
    }

    public List<KnytningRs> toListOfKnytningRs() {
        return records.stream()
                .map(this::getKnytning)
                .collect(Collectors.toList());
    }

    private KnytningRs getKnytning(String record) {
        Knytning knytning = new Knytning(record);
        return knytning.toKnytningRs(record);
    }
}
