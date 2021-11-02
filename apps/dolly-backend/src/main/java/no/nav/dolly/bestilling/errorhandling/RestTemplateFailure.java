package no.nav.dolly.bestilling.errorhandling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestTemplateFailure {

    String timestamp;
    String status;
    String error;
    String exception;
    String message;
    String path;

}
