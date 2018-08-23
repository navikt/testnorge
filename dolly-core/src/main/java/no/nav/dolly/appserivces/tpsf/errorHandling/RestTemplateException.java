package no.nav.dolly.appserivces.tpsf.errorHandling;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
