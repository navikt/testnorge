package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.adapter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateXmlAdapter extends XmlAdapter<String, LocalDate> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public LocalDate unmarshal(String v) {
        return LocalDate.parse(v, DATE_FORMATTER);
    }

    @Override
    public String marshal(LocalDate v) {
        return DATE_FORMATTER.format(v);
    }
}