package no.nav.identpool.rs.v1.support;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
    private String message;
    private HttpStatus httpStatus;
}

