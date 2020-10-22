package no.nav.identpool.rs.v1.support;

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

