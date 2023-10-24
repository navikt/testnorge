package no.nav.testnav.endringsmeldingservice.domain;

import no.nav.testnav.endringsmeldingservice.consumer.response.EndringsmeldingResponse;

import java.util.List;

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
                .toList();
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
