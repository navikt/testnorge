package no.nav.registre.testnorge.arena.consumer.rs.response.tpsf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatrikkelAdresse {

    private String undernr;
    private String gardsnr;
    private String festenr;
    private String mellomAdresse;
    private String bruksnr;
}
