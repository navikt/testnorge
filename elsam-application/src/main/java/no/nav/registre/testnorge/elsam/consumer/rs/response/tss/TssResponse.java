package no.nav.registre.testnorge.elsam.consumer.rs.response.tss;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TssResponse {

    private List<Response110> response110;
    private List<Response111> response111;
    private List<Response125> response125;

}
