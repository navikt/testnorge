package no.nav.registre.testnorge.arena.consumer.rs.response.pensjon;

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
public class HttpStatus {

    private String reasonPhrase;
    private Integer status;
}
