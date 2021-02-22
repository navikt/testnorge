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
public class OffentligAdresse {

    private String bokstav;
    private String gatekode;
    private Integer husnr;
    private String gateNavn;

}
