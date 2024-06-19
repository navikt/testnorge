package no.nav.testnav.inntektsmeldinggeneratorservice.provider.adapter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeXmlAdapter extends XmlAdapter<String, LocalDateTime> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public LocalDateTime unmarshal(String v) {
        return LocalDateTime.parse(v, DATE_TIME_FORMATTER);
    }

    @Override
    public String marshal(LocalDateTime v) {
        return DATE_TIME_FORMATTER.format(v);
    }
}