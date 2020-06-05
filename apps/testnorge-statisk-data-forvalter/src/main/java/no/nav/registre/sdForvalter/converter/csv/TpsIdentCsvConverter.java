package no.nav.registre.sdForvalter.converter.csv;

import java.util.HashMap;
import java.util.Map;

import no.nav.registre.sdForvalter.domain.TpsIdent;

public class TpsIdentCsvConverter extends CsvConverter<TpsIdent> {

    static TpsIdentCsvConverter inst;

    private TpsIdentCsvConverter () {

    }

    public static TpsIdentCsvConverter inst () {
        if (inst == null) {
            inst = new TpsIdentCsvConverter();
        }
        return inst;
    }

    private enum Headers implements CsvHeader {
        FNR("FNR"),
        FIRST_NAME("Fornavn"),
        LAST_NAME("Etternavn"),
        ADDRESS("Adresse"),
        POST_NR("Postnummer"),
        CITY("Poststed"),
        GRUPPE("Gruppe"),
        OPPRINNELSE("Opprinnelse");

        private final String header;

        Headers(String header) {
            this.header = header;
        }

        @Override
        public String getValue() {
            return header;
        }
    }

    @Override
    protected RowConverter<TpsIdent> getRowConverter() {
        return map -> TpsIdent
                .builder()
                .fnr(getString(map, Headers.FNR))
                .firstName(getString(map, Headers.FIRST_NAME))
                .lastName(getString(map, Headers.LAST_NAME))
                .address(getString(map, Headers.ADDRESS))
                .postNr(getString(map, Headers.POST_NR))
                .city(getString(map, Headers.CITY))
                .gruppe(getString(map, Headers.GRUPPE))
                .opprinnelse(getString(map, Headers.OPPRINNELSE))
                .build();
    }

    @Override
    protected ObjectConverter<TpsIdent> getObjectConverter() {
        return item -> {
            Map<String, Object> map = new HashMap<>();
            map.put( Headers.FNR.getValue(), item.getFnr());
            map.put ( Headers.FIRST_NAME.getValue(), item.getFirstName());
            map.put( Headers.LAST_NAME.getValue(), item.getLastName());
            map.put( Headers.ADDRESS.getValue(), item.getAddress());
            map.put( Headers.POST_NR.getValue(), item.getPostNr());
            map.put( Headers.CITY.getValue(), item.getCity());
            map.put( Headers.GRUPPE.getValue(), item.getGruppe());
            map.put( Headers.OPPRINNELSE.getValue(), item.getOpprinnelse());
            return map;
        };
    }

    @Override
    CsvHeader[] getHeaders() {
        return Headers.values();
    }
}
