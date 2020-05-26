package no.nav.registre.ereg.domain;

import static java.util.Objects.isNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.registre.ereg.provider.rs.request.EregDataRequest;

public class Organisasjon extends Flatfil {
    private static final String UNIX_NEW_LINE = "\n";
    private static final String ENHET_TAG = "ENH ";
    private static final String NAVN_TAG = "NAVN";
    private static final String EPOST_TAG = "EPOS";
    private static final String POSTADR_TAG = "PADR";
    private static final String FORRETNINGSADR_TAG = "FADR";
    private static final String KNYTNING_TAG = "K";


    private final List<String> records;
    private final Navn navn;
    private final Adresse forretningsadr;
    private final Adresse postadr;
    private final KnytningList knytninger;


    public Organisasjon(String records) {
        this.records = Arrays.asList(records.split(UNIX_NEW_LINE));
        this.navn = getNavnRecord().map(Navn::new).orElse(null);
        this.postadr = getPostAdrRecord().map(Adresse::new).orElse(null);
        this.forretningsadr = getForretningsAdrRecord().map(Adresse::new).orElse(null);
        this.knytninger = new KnytningList(getKnytningerRecords());
    }

    public EregDataRequest toEregDataRequest() {
        return EregDataRequest.builder()
                .orgnr(getOrgnr())
                .enhetstype((getEnhetstype()))
                .endringsType(getEndringstype())
                .navn(isNull(navn) ? null : navn.toNavnRs())
                .forretningsAdresse(isNull(forretningsadr) ? null : forretningsadr.toAdresseRs())
                .adresse(isNull(postadr) ? null : postadr.toAdresseRs())
                .knytninger(isNull(knytninger) ? null : knytninger.toListOfKnytningRs())
                .epost(getEpostAdr())
                .build();
    }

    private String getEnhetRecord() {
        return records.stream()
                .filter(record -> record.startsWith(ENHET_TAG))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Finner ikke enhetsrecord"));
    }

    private Optional<String> getNavnRecord() {
        return records.stream()
                .filter(record -> record.startsWith(NAVN_TAG))
                .findFirst();
    }

    private Optional<String> getEpostRecord() {
        return records.stream()
                .filter(record -> record.startsWith(EPOST_TAG))
                .findFirst();
    }

    private Optional<String> getForretningsAdrRecord() {
        return records.stream()
                .filter(record -> record.startsWith(FORRETNINGSADR_TAG))
                .findFirst();
    }

    private Optional<String> getPostAdrRecord() {
        return records.stream()
                .filter(record -> record.startsWith(POSTADR_TAG))
                .findFirst();
    }

    private List<String> getKnytningerRecords() {
        String enhetstype = getEnhetstype();
        return records.stream()
                .filter(record -> record.startsWith(enhetstype) && record.substring(8, 9).equals(KNYTNING_TAG))
                .collect(Collectors.toList());
    }

    public String getOrgnr() {
        return getEnhetRecord().substring(4, 13);
    }

    public String getEnhetstype() {
        return getValueFromRecord(getEnhetRecord(), 13, 17);
    }

    public String getEndringstype() {
        return getValueFromRecord(getEnhetRecord(), 17, 18);
    }

    public String getEpostAdr() {
        return getValueFromOptionalRecord(getEpostRecord(), 8, 158);
    }
}
