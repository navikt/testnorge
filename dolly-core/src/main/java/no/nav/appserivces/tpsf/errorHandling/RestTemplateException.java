package no.nav.appserivces.tpsf.errorHandling;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestTemplateException {

    String timestamp;
    String status;
    String error;
    String exception;
    String message;
    String path;

}
