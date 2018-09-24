package no.nav.identpool.ident.rest.v1;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
    private String message;
    private HttpStatus httpStatus;
}

