package no.nav.dolly.bestilling.arbeidssoekerregisteret.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArbeidssokerregisteretResponse {

            private String id;
            private String type;
            private Integer status;
            private String title;
            private String detail;
            private String instance;
            private LocalDateTime timestamp;
}

