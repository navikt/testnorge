package no.nav.testnav.endringsmeldingservice.domain;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.testnav.endringsmeldingservice.consumer.response.EndringsmeldingResponse;

public class Status {
    private final String personId;
    private final List<String> errors;

    public Status(EndringsmeldingResponse endringsmeldingResponse) {
        personId = endringsmeldingResponse.getPersonId();
        errors = endringsmeldingResponse
                .getStatus()
                .values()
                .stream()
                .filter(value -> !value.equals("OK"))
                .collect(Collectors.toList());
    }

    public List<String> getErrors() {
        return errors;
    }

    public boolean isOk() {
        return errors.isEmpty();
    }

    public String getPersonId() {
        return personId;
    }
}
