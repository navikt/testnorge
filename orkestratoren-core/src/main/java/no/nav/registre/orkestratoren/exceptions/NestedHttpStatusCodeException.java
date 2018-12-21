package no.nav.registre.orkestratoren.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Getter
public class NestedHttpStatusCodeException extends RuntimeException {

    List<HttpStatusCodeException> nestedExceptions;
    List<String> funksjonelleFeil;

    public NestedHttpStatusCodeException(HttpStatusCodeException e) {
        super(e.getMessage(), e);
        nestedExceptions = new ArrayList<>();
        funksjonelleFeil = new ArrayList<>();
        nestedExceptions.add(e);
    }

    public void addException(HttpStatusCodeException e) {
        nestedExceptions.add(e);
    }

    public void addFunksjonellFeil(String feil) {
        funksjonelleFeil.add(feil);
    }

    public String toString() {
        StringBuilder formatert = new StringBuilder();
        for (Exception e : nestedExceptions) {
            formatert.append(e);
            formatert.append(System.lineSeparator());
        }
        return formatert.toString();
    }

    public String getResponseBodyAsString() {
        StringBuilder formatert = new StringBuilder();
        for (HttpStatusCodeException e : nestedExceptions) {
            formatert.append(e.getResponseBodyAsString());
            formatert.append(System.lineSeparator());
        }
        return formatert.toString();
    }

    public List<HttpStatus> getStatusCodes() {
        List<HttpStatus> codes = new ArrayList<>(nestedExceptions.size());
        for (HttpStatusCodeException e : nestedExceptions) {
            codes.add(e.getStatusCode());
        }
        return codes;
    }

    public HttpStatus getStatusCode() {
        return nestedExceptions.get(nestedExceptions.size() - 1).getStatusCode();
    }

}
