package no.nav.dolly.bestilling.instdata.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteResponse {

    private LocalDateTime timestamp;
    private String status;
    private String error;
    private String message;
    private String path;
}
