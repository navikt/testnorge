package no.nav.registre.testnorge.organisasjonmottak.domain;

import java.util.List;
import java.util.stream.Collectors;

public class Knytning extends ToFlatfil {
    private final List<Virksomhet> driverVirksomheter;

    public Knytning(no.nav.registre.testnorge.libs.avro.organisasjon.Knytning knytning) {
        super(knytning.getMetadata());
        driverVirksomheter = knytning
                .getDriverVirksomhenter()
                .stream()
                .map(Virksomhet::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isUpdate() {
        return true;
    }

    @Override
    public Flatfil toFlatfil() {
        Flatfil flatfil = new Flatfil();
        Record record = new Record();
        record.append(createEHN());
        driverVirksomheter.forEach(value -> {
            record.append(value.toRecordLine());
        });
        flatfil.add(record);
        return flatfil;
    }
}
