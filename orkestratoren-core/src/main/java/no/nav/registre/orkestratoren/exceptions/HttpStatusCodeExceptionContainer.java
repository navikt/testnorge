package no.nav.registre.orkestratoren.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor
public class HttpStatusCodeExceptionContainer extends RuntimeException {

    final List<HttpStatusCodeException> nestedExceptions = new ArrayList<>();
    final List<String> feilmeldingBeskrivelser = new ArrayList<>();

    public void addException(HttpStatusCodeException e) {
        nestedExceptions.add(e);
    }

    public void addFeilmeldingBeskrivelse(String feil) {
        feilmeldingBeskrivelser.add(feil);
    }

    public String getResponseBodyAsString() {
        StringBuilder formatert = new StringBuilder();
        for (HttpStatusCodeException e : nestedExceptions) {
            formatert.append(e.getMessage()).append(e.getResponseBodyAsString()).append(System.lineSeparator());
        }

        formatert.append("FEILMELDINGER").append(System.lineSeparator());

        for (String s : feilmeldingBeskrivelser) {
            formatert.append(s).append(System.lineSeparator());
        }

        return formatert.toString();
    }

    @JsonIgnore
    public HttpStatus getGeneralStatusCode() {
        HttpStatus status = null;
        Optional<HttpStatusCodeException> exception = nestedExceptions.stream().filter(e -> e.getStatusCode().is4xxClientError()).findFirst();
        if (exception.isPresent()) {
            status =  exception.get().getStatusCode();
        } else if (nestedExceptions.isEmpty()) {
            status = nestedExceptions.get(0).getStatusCode();
        }
        return status;
    }

}
