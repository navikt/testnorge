package no.nav.dolly.domain.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MalBestilling {

    private Long id;
    private String malnavn;
    private String malbestilling;
    private String miljoer;
    private LocalDateTime sistoppdatert;
}
