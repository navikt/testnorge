package no.nav.identpool.ident.rs.v1;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiError {
    private String message;
    private HttpStatus httpStatus;
}

