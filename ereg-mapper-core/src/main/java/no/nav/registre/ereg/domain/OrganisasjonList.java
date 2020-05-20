package no.nav.registre.ereg.domain;

import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.ereg.provider.rs.request.EregDataRequest;

public class OrganisasjonList {
    private static final String RECORD_START_TAG = "\nENH ";
    private static final String UNIX_NEW_LINE = "\n";

    private final List<Organisasjon> list = new ArrayList<>();

    public OrganisasjonList( String flatfil){
        String[] array = flatfil.split(UNIX_NEW_LINE);
        String flatfilOnlyRecords = String.join(UNIX_NEW_LINE, Arrays.asList(array).subList(1, array.length - 1));
        if(Strings.isNotBlank(flatfilOnlyRecords) ) {
            for (String record : flatfilOnlyRecords.split("(?=" + RECORD_START_TAG+")")) {
                list.add(new Organisasjon(record));
            }
        }
    }

    public List<EregDataRequest> toEregDataRequestList() {
        return list.stream().map(Organisasjon::toEregDataRequest).collect(Collectors.toList());
    }
}

